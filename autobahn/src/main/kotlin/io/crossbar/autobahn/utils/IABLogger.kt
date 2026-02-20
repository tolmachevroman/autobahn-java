package io.crossbar.autobahn.utils

/**
 * Logger interface for Autobahn library.
 */
interface IABLogger {
    fun v(msg: String)
    fun v(msg: String, throwable: Throwable)
    fun d(msg: String)
    fun i(msg: String)
    fun w(msg: String)
    fun w(msg: String, throwable: Throwable)
    fun e(msg: String)
}
