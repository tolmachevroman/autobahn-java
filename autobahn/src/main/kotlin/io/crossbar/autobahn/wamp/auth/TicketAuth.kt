package io.crossbar.autobahn.wamp.auth

import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.interfaces.IAuthenticator
import io.crossbar.autobahn.wamp.types.Challenge
import io.crossbar.autobahn.wamp.types.ChallengeResponse
import java8.util.concurrent.CompletableFuture

class TicketAuth(
    @JvmField val authid: String,
    @JvmField val ticket: String,
    @JvmField val authextra: Map<String, Any>? = null
) : IAuthenticator {

    companion object {
        const val authmethod = "ticket"
    }

    constructor(authid: String, ticket: String) : this(authid, ticket, null)

    override fun onChallenge(session: Session?, challenge: Challenge): CompletableFuture<ChallengeResponse> {
        return CompletableFuture.completedFuture(ChallengeResponse(ticket, authextra))
    }

    override fun getAuthMethod(): String {
        return authmethod
    }
}
