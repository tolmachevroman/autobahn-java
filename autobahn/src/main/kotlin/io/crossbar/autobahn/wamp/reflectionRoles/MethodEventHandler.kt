package io.crossbar.autobahn.wamp.reflectionRoles

import io.crossbar.autobahn.wamp.interfaces.ISerializer
import io.crossbar.autobahn.wamp.interfaces.TriConsumer
import io.crossbar.autobahn.wamp.types.EventDetails
import java.lang.reflect.Method

class MethodEventHandler(
    private val instance: Any,
    private val method: Method,
    private val serializer: ISerializer
) : TriConsumer<List<Any>?, Map<String, Any>?, EventDetails> {

    private val unpacker = ArgumentUnpacker(method)

    override fun accept(t: List<Any>?, u: Map<String, Any>?, v: EventDetails) {
        try {
            val parameters = unpacker.unpackParameters(serializer, t, u)
            method.invoke(instance, *parameters)
        } catch (ex: Throwable) {
            // TODO: deal with exception
        }
    }
}
