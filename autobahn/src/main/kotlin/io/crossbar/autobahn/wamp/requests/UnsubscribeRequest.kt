package io.crossbar.autobahn.wamp.requests

import java8.util.concurrent.CompletableFuture

class UnsubscribeRequest(
    request: Long,
    @JvmField val onReply: CompletableFuture<Int>,
    @JvmField val subscriptionID: Long
) : Request(request)
