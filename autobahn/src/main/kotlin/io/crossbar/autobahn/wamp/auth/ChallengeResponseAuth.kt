package io.crossbar.autobahn.wamp.auth

import io.crossbar.autobahn.utils.AuthUtil
import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.interfaces.IAuthenticator
import io.crossbar.autobahn.wamp.types.Challenge
import io.crossbar.autobahn.wamp.types.ChallengeResponse
import java.nio.charset.StandardCharsets
import java.security.Key
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java8.util.concurrent.CompletableFuture
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.bouncycastle.crypto.params.KeyParameter

class ChallengeResponseAuth(
    @JvmField val authid: String,
    @JvmField val secret: String,
    @JvmField val authrole: String? = null,
    @JvmField val authextra: Map<String, Any>? = null
) : IAuthenticator {

    companion object {
        const val authmethod = "wampcra"
    }

    constructor(authid: String, secret: String) : this(authid, secret, null, null)
    constructor(authid: String, secret: String, authextra: Map<String, Any>?) : this(authid, secret, null, authextra)

    private fun deriveKey(password: String, salt: String, iterations: Int, keySize: Int): String {
        val gen = PKCS5S2ParametersGenerator(SHA256Digest())
        gen.init(
            password.toByteArray(StandardCharsets.UTF_8),
            salt.toByteArray(StandardCharsets.UTF_8),
            iterations
        )
        val keyRaw = (gen.generateDerivedParameters(keySize * 8) as KeyParameter).key
        return AuthUtil.encodeToString(keyRaw)
    }

    private fun computeWCS(key: String, challenge: String): String {
        val sha256HMAC = Mac.getInstance("HmacSHA256")
        val secretKey: Key = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), sha256HMAC.algorithm)
        sha256HMAC.init(secretKey)
        return AuthUtil.encodeToString(sha256HMAC.doFinal(challenge.toByteArray(StandardCharsets.UTF_8)))
    }

    override fun onChallenge(session: Session?, challenge: Challenge): CompletableFuture<ChallengeResponse> {
        return try {
            val key = if (challenge.extra.containsKey("salt")) {
                deriveKey(
                    secret,
                    challenge.extra["salt"] as String,
                    challenge.extra["iterations"] as Int,
                    challenge.extra["keylen"] as Int
                )
            } else {
                secret
            }

            val signature = computeWCS(key, challenge.extra["challenge"] as String)
            CompletableFuture.completedFuture(ChallengeResponse(signature, authextra))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun getAuthMethod(): String {
        return authmethod
    }
}
