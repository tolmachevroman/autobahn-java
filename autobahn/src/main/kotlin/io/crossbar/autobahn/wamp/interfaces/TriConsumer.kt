package io.crossbar.autobahn.wamp.interfaces

fun interface TriConsumer<T, U, V> {
    fun accept(t: T, u: U, v: V)
}
