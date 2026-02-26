package io.crossbar.autobahn.wamp.reflectionRoles

import io.crossbar.autobahn.wamp.interfaces.IInvocationHandler
import io.crossbar.autobahn.wamp.interfaces.ISerializer
import io.crossbar.autobahn.wamp.types.InvocationDetails
import io.crossbar.autobahn.wamp.types.InvocationResult
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class MethodInvocationHandler(
    private val instance: Any,
    private val method: Method,
    private val serializer: ISerializer
) : IInvocationHandler {

    private val unpacker = ArgumentUnpacker(method)

    override fun apply(
        list: List<Any>?,
        map: Map<String, Any>?,
        invocationDetails: InvocationDetails
    ): InvocationResult {
        val parameters = unpacker.unpackParameters(serializer, list, map)

        return try {
            val result = method.invoke(instance, *parameters)
            InvocationResult(result)
        } catch (e: Exception) {
            if (e is InvocationTargetException) {
                val targetException = e.targetException
                val convertedException = convertRuntimeException(targetException)
                // TODO: throw convertedException
                // TODO: It is currently not possible to throw an exception from this interface.
            }
            InvocationResult(null)
        }
    }

    private fun convertRuntimeException(targetException: Throwable): Throwable {
        return if (targetException is WampException) {
            targetException
        } else {
            WampException("wamp.error.runtime_error", targetException.message ?: "")
        }
    }
}
