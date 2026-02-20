package io.crossbar.autobahn.wamp.requests

import java8.util.concurrent.CompletableFuture

class UnregisterRequest(
    request: Long,
    @JvmField val onReply: CompletableFuture<Int>,
    @JvmField val registrationID: Long
) : Request(request)
