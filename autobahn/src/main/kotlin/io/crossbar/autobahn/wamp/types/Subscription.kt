package io.crossbar.autobahn.wamp.types

import com.fasterxml.jackson.core.type.TypeReference
import io.crossbar.autobahn.wamp.Session
import java8.util.concurrent.CompletableFuture

class Subscription(
    @JvmField val subscription: Long,
    @JvmField val topic: String,
    @JvmField val resultTypeRef: TypeReference<*>?,
    @JvmField val resultTypeClass: Class<*>?,
    @JvmField val handler: Any,
    @JvmField val session: Session?
) {
    private var active: Boolean = true

    fun unsubscribe(): CompletableFuture<Int>? {
        return session?.unsubscribe(this)
    }

    fun setInactive() {
        if (active) {
            active = false
        } else {
            throw IllegalStateException("Subscription already invactive")
        }
    }

    fun isActive(): Boolean {
        return active
    }
}
