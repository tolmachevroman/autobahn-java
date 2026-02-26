package io.crossbar.autobahn.wamp.reflectionRoles

import com.fasterxml.jackson.core.type.TypeReference
import java.lang.reflect.Type

internal class ReflectionTypeReference(private val newType: Type) : TypeReference<Any>() {
    override fun getType(): Type = newType
}
