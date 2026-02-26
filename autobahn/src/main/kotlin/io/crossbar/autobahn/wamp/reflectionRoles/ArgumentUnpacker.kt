package io.crossbar.autobahn.wamp.reflectionRoles

import io.crossbar.autobahn.wamp.interfaces.ISerializer
import io.crossbar.autobahn.wamp.utils.Platform
import java.lang.reflect.Method
import java.lang.reflect.Parameter

class ArgumentUnpacker(method: Method) {
    private val parameters: Array<ParameterInfo>

    init {
        parameters = if (!io.crossbar.autobahn.utils.Platform.isAndroid() || Platform.getAndroidAPIVersion() >= 26) {
            method.parameters.mapIndexed { index, parameter ->
                ParameterInfo(index, parameter.name, parameter.type)
            }.toTypedArray()
        } else {
            method.parameterTypes.mapIndexed { index, parameterType ->
                ParameterInfo(index, "arg$index", parameterType)
            }.toTypedArray()
        }
    }

    fun unpackParameters(
        serializer: ISerializer,
        list: List<Any>?,
        map: Map<String, Any>?
    ): Array<Any?> {
        val result = arrayOfNulls<Any>(parameters.size)
        var namedParametersStartPosition = 0

        if (list != null) {
            namedParametersStartPosition = list.size
            for (i in list.indices) {
                result[i] = serializer.convertValue(list[i], parameters[i].getType())
            }
        }

        if (map != null) {
            for (i in namedParametersStartPosition until parameters.size) {
                val currentParameter = parameters[i]
                val parameterName = currentParameter.getName()

                if (!map.containsKey(parameterName)) {
                    // TODO: throw a WampException indicating that a
                    // TODO: parameter with name parameterName or position i was not present.
                } else {
                    result[i] = serializer.convertValue(map[parameterName], currentParameter.getType())
                }
            }
        }

        return result
    }
}
