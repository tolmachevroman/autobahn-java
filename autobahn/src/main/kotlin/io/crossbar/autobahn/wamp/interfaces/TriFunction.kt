package io.crossbar.autobahn.wamp.interfaces

fun interface TriFunction<T, U, V, R> {
    fun apply(t: T, u: U, v: V): R
}
