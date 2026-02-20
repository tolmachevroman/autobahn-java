package io.crossbar.autobahn.wamp.auth

import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.interfaces.IAuthenticator
import io.crossbar.autobahn.wamp.types.Challenge
import io.crossbar.autobahn.wamp.types.ChallengeResponse
import java8.util.concurrent.CompletableFuture

class AnonymousAuth : IAuthenticator {

    companion object {
        const val authmethod = "anonymous"
    }

    @JvmField
    val authid: String? = null

    override fun onChallenge(session: Session?, challenge: Challenge): CompletableFuture<ChallengeResponse> {
        throw UnsupportedOperationException("Anonymous auth does not support challenge.")
    }

    override fun getAuthMethod(): String {
        return authmethod
    }
}
