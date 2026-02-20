package io.crossbar.autobahn.wamp.interfaces

import io.crossbar.autobahn.wamp.types.EventDetails
import io.crossbar.autobahn.wamp.types.ReceptionResult
import java8.util.concurrent.CompletableFuture

fun interface IEventHandler {
    fun apply(
        args: List<Any>?,
        kwargs: Map<String, Any>?,
        details: EventDetails
    ): CompletableFuture<ReceptionResult>
}
