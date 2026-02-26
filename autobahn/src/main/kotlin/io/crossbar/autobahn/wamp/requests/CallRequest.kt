package io.crossbar.autobahn.wamp.requests

import com.fasterxml.jackson.core.type.TypeReference
import io.crossbar.autobahn.wamp.types.CallOptions
import java8.util.concurrent.CompletableFuture

class CallRequest<T>(
    request: Long,
    @JvmField val procedure: String,
    @JvmField val onReply: CompletableFuture<T>,
    @JvmField val options: CallOptions?,
    @JvmField val resultTypeRef: TypeReference<T>?,
    @JvmField val resultTypeClass: Class<T>?
) : Request(request) {

    init {
        if (resultTypeRef != null && resultTypeClass != null) {
            throw IllegalArgumentException("Can only provide one of resultTypeRef or resultTypeClass")
        }
    }
}
