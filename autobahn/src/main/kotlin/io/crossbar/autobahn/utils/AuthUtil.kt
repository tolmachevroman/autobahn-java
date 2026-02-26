package io.crossbar.autobahn.utils

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.Formatter

/**
 * Utility class for authentication-related operations.
 */
object AuthUtil {

    private const val SSH_BEGIN = "-----BEGIN OPENSSH PRIVATE KEY-----"
    private const val SSH_END = "-----END OPENSSH PRIVATE KEY-----"
    private const val OPENSSH_KEY_V1 = "openssh-key-v1"

    private fun getBase64ClassAndroid(): Class<*> {
        return Class.forName("android.util.Base64")
    }

    private fun getBase64ClassJava8(): Class<*> {
        return Class.forName("java.util.Base64")
    }

    @JvmStatic
    fun toBinary(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) +
                    Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    @JvmStatic
    fun toHexString(buf: ByteArray): String {
        val formatter = Formatter()
        for (b in buf) {
            formatter.format("%02x", b)
        }
        return formatter.toString()
    }

    @JvmStatic
    @Throws(Exception::class)
    fun decodeString(privateKey: String): ByteArray {
        return if (Platform.isAndroid()) {
            val base64Class = getBase64ClassAndroid()
            val method = base64Class.getMethod("decode", String::class.java, Int::class.javaPrimitiveType)
            val field = base64Class.getField("DEFAULT")
            method.invoke(null, privateKey, field.getInt(base64Class)) as ByteArray
        } else {
            val base64Class = getBase64ClassJava8()
            val encoderObject = base64Class.getMethod("getDecoder").invoke(null)
            encoderObject.javaClass.getMethod("decode", String::class.java)
                .invoke(encoderObject, privateKey) as ByteArray
        }
    }

    @JvmStatic
    @Throws(Exception::class)
    fun encodeToString(challenge: ByteArray): String {
        return if (Platform.isAndroid()) {
            val base64Class = getBase64ClassAndroid()
            val method = base64Class.getMethod("encodeToString", ByteArray::class.java, Int::class.javaPrimitiveType)
            val field = base64Class.getField("DEFAULT")
            val result = method.invoke(null, challenge, field.getInt(base64Class)) as String
            result.trim()
        } else {
            val base64Class = getBase64ClassJava8()
            val encoderObject = base64Class.getMethod("getEncoder").invoke(null)
            val result = encoderObject.javaClass.getMethod("encodeToString", ByteArray::class.java)
                .invoke(encoderObject, challenge) as String
            result.trim()
        }
    }

    @JvmStatic
    @Throws(Exception::class)
    fun parseOpenSSHFile(file: File): Map<String, ByteArray> {
        val br = BufferedReader(FileReader(file))
        val lines = mutableListOf<String>()
        var sCurrentLine: String?
        while (br.readLine().also { sCurrentLine = it } != null) {
            sCurrentLine?.let { lines.add(it) }
        }
        return parseOpenSSHFile(lines)
    }

    @Throws(Exception::class)
    private fun parseOpenSSHFile(lines: MutableList<String>): Map<String, ByteArray> {
        if (lines.firstOrNull() != SSH_BEGIN || lines.lastOrNull() != SSH_END) {
            throw RuntimeException("Invalid OPENSSH file")
        }
        lines.removeAt(0)
        lines.removeAt(lines.size - 1)
        val base64String = lines.joinToString("")
        val rawKey = decodeString(base64String)

        val verify = rawKey.copyOfRange(0, OPENSSH_KEY_V1.length)
        if (String(verify) != OPENSSH_KEY_V1) {
            throw RuntimeException("Invalid OPENSSH file")
        }

        var occurred = false
        var index = 0
        for (i in rawKey.indices) {
            if (rawKey[i] == 's'.toByte()
                && rawKey[i + 1] == 's'.toByte()
                && rawKey[i + 2] == 'h'.toByte()
                && rawKey[i + 3] == '-'.toByte()
                && rawKey[i + 4] == 'e'.toByte()
                && rawKey[i + 5] == 'd'.toByte()
                && rawKey[i + 6] == '2'.toByte()
                && rawKey[i + 7] == '5'.toByte()
                && rawKey[i + 8] == '5'.toByte()
                && rawKey[i + 9] == '1'.toByte()
                && rawKey[i + 10] == '9'.toByte()
                && rawKey[i + 11] == 0x00.toByte()
                && rawKey[i + 12] == 0x00.toByte()
                && rawKey[i + 13] == 0x00.toByte()
                && rawKey[i + 14] == ' '.toByte()
            ) {
                index = i + 15
                if (occurred) {
                    break
                }
                occurred = true
            }
        }

        val publicKey = rawKey.copyOfRange(index, index + 32)

        index += 32
        for (i in index until rawKey.size) {
            if (rawKey[i] == 0x00.toByte()
                && rawKey[i + 1] == 0x00.toByte()
                && rawKey[i + 2] == 0x00.toByte()
                && rawKey[i + 3] == '@'.toByte()
            ) {
                index = i + 4
                break
            }
        }

        val privateKey = rawKey.copyOfRange(index, index + 32)

        return mapOf(
            "pubkey" to publicKey,
            "privkey" to privateKey
        )
    }
}
