package io.crossbar.autobahn.wamp.types

import io.crossbar.autobahn.wamp.Session
import java8.util.concurrent.CompletableFuture

class Registration(
    @JvmField val registration: Long,
    @JvmField val procedure: String,
    @JvmField val endpoint: Any,
    @JvmField val session: Session?
) {
    private var active: Boolean = true

    fun unregister(): CompletableFuture<Int>? {
        return session?.unregister(this)
    }

    fun setInactive() {
        if (active) {
            active = false
        } else {
            throw IllegalStateException("Registration already invactive")
        }
    }

    fun isActive(): Boolean {
        return active
    }
}
