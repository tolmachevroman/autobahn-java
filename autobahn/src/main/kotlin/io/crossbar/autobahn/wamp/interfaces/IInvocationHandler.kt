package io.crossbar.autobahn.wamp.interfaces

import io.crossbar.autobahn.wamp.types.InvocationDetails
import io.crossbar.autobahn.wamp.types.InvocationResult

fun interface IInvocationHandler {
    fun apply(
        args: List<Any>?,
        kwargs: Map<String, Any>?,
        details: InvocationDetails
    ): InvocationResult
}
