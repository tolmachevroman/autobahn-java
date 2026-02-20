package io.crossbar.autobahn.wamp.auth

import io.crossbar.autobahn.utils.AuthUtil
import io.crossbar.autobahn.utils.Pair
import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.interfaces.IAuthenticator
import io.crossbar.autobahn.wamp.types.Challenge
import io.crossbar.autobahn.wamp.types.ChallengeResponse
import io.xconn.cryptology.CryptoSign
import io.xconn.cryptology.KeyPair
import org.bouncycastle.util.encoders.Hex
import java.io.File
import java8.util.concurrent.CompletableFuture

class CryptosignAuth : IAuthenticator {

    companion object {
        const val authmethod = "cryptosign"

        @JvmStatic
        fun generateSigningKeyPair(): Pair<String, String> {
            val keyPair = CryptoSign.generateKeyPair()
            val publicKeyHex = Hex.toHexString(keyPair.publicKey)
            val privateKeyHex = Hex.toHexString(keyPair.privateKey)
            return Pair(publicKeyHex, privateKeyHex)
        }

        @JvmStatic
        fun getPublicKey(privateKeyRaw: ByteArray): String {
            val publicKeyBytes = CryptoSign.getPublicKey(privateKeyRaw)
            return AuthUtil.toHexString(publicKeyBytes)
        }
    }

    @JvmField
    val authid: String

    @JvmField
    val authrole: String?

    @JvmField
    val authextra: Map<String, Any>?

    private val privateKeyRaw: ByteArray

    constructor(authid: String, privateKey: String) : this(
        authid,
        null,
        privateKey,
        hashMapOf("pubkey" to getPublicKey(AuthUtil.toBinary(privateKey)))
    )

    constructor(authid: String, privateKey: String, authextra: Map<String, Any>?) : this(
        authid,
        null,
        privateKey,
        authextra
    )

    constructor(authid: String, privateKey: String, publicKey: String) : this(
        authid,
        null,
        privateKey,
        hashMapOf("pubkey" to publicKey)
    )

    constructor(authid: String, authrole: String?, privateKey: String, authextra: Map<String, Any>?) {
        this.authid = authid
        this.authrole = authrole
        this.authextra = authextra
        this.privateKeyRaw = try {
            AuthUtil.toBinary(privateKey)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    constructor(authid: String, privateKeyFile: File) {
        this.authid = authid
        try {
            val keydata = AuthUtil.parseOpenSSHFile(privateKeyFile)
            this.authextra = hashMapOf("pubkey" to AuthUtil.toHexString(keydata["pubkey"]!!))
            this.privateKeyRaw = keydata["privkey"]!!
            this.authrole = null
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun onChallenge(session: Session?, challenge: Challenge): CompletableFuture<ChallengeResponse> {
        val hexChallenge = challenge.extra["challenge"] as String
        val rawChallenge = AuthUtil.toBinary(hexChallenge)
        val signed = CryptoSign.sign(privateKeyRaw, rawChallenge)
        val signatureHex = AuthUtil.toHexString(signed)
        val res = signatureHex + hexChallenge
        return CompletableFuture.completedFuture(ChallengeResponse(res, null))
    }

    override fun getAuthMethod(): String {
        return authmethod
    }
}
