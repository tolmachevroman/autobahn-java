package io.crossbar.autobahn.wamp.interfaces

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.crossbar.autobahn.utils.ABLogger
import io.crossbar.autobahn.utils.IABLogger
import java.io.IOException

abstract class ISerializer {

    companion object {
        private val LOGGER: IABLogger = ABLogger.getLogger(ISerializer::class.java.name)
    }

    @JvmField
    val mapper: ObjectMapper

    constructor(factory: JsonFactory) {
        mapper = ObjectMapper(factory)
        mapper.findAndRegisterModules()
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    constructor(objectMapper: ObjectMapper) {
        mapper = objectMapper
    }

    fun serialize(message: List<Any>?): ByteArray? {
        return try {
            mapper.writeValueAsBytes(message)
        } catch (e: Exception) {
            LOGGER.v(e.message ?: "Serialization error", e)
            null
        }
    }

    fun unserialize(payload: ByteArray, isBinary: Boolean): List<Any>? {
        return try {
            mapper.readValue(payload, object : TypeReference<List<Any>>() {})
        } catch (e: IOException) {
            LOGGER.v(e.message ?: "Deserialization error", e)
            null
        }
    }

    fun <T> convertValue(fromValue: Any?, toValueTypeRef: TypeReference<T>): T? {
        return mapper.convertValue(fromValue, toValueTypeRef)
    }

    fun <T> convertValue(fromValue: Any?, toValueTypeClass: Class<T>): T? {
        return mapper.convertValue(fromValue, toValueTypeClass)
    }

    open fun isBinary(): Boolean {
        return true
    }
}
