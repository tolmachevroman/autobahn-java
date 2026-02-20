package io.crossbar.autobahn.wamp.requests

import com.fasterxml.jackson.core.type.TypeReference
import io.crossbar.autobahn.wamp.types.Subscription
import java8.util.concurrent.CompletableFuture

class SubscribeRequest(
    request: Long,
    @JvmField val topic: String,
    @JvmField val onReply: CompletableFuture<Subscription>,
    @JvmField val resultTypeRef: TypeReference<*>?,
    @JvmField val resultTypeClass: Class<*>?,
    @JvmField val handler: Any
) : Request(request) {

    init {
        if (resultTypeRef != null && resultTypeClass != null) {
            throw IllegalArgumentException("Can only provide one of resultTypeRef or resultTypeClass")
        }
    }
}
