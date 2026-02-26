package io.crossbar.autobahn.wamp.requests

import io.crossbar.autobahn.wamp.types.Registration
import java8.util.concurrent.CompletableFuture

class RegisterRequest(
    request: Long,
    @JvmField val onReply: CompletableFuture<Registration>,
    @JvmField val procedure: String,
    @JvmField val endpoint: Any
) : Request(request)
