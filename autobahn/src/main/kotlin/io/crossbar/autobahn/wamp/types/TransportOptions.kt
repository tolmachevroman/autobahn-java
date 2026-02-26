package io.crossbar.autobahn.wamp.types

import com.fasterxml.jackson.databind.ObjectMapper

open class TransportOptions {
    @JvmField
    var maxFramePayloadSize: Int = 128 * 1024

    @JvmField
    var autoPingInterval: Int = 10

    @JvmField
    var autoPingTimeout: Int = 5

    @JvmField
    var objectMapper: ObjectMapper? = null

    // Getter methods for Java compatibility
    fun getMaxFramePayloadSize(): Int = maxFramePayloadSize
    fun getAutoPingInterval(): Int = autoPingInterval
    fun getAutoPingTimeout(): Int = autoPingTimeout
    fun getObjectMapper(): ObjectMapper? = objectMapper

    // Setter methods for Java compatibility
    fun setMaxFramePayloadSize(size: Int) {
        if (size > 0) {
            maxFramePayloadSize = size
        }
    }

    fun setAutoPingInterval(seconds: Int) {
        autoPingInterval = seconds
    }

    fun setAutoPingTimeout(seconds: Int) {
        autoPingTimeout = seconds
    }

    fun setObjectMapper(mapper: ObjectMapper?) {
        objectMapper = mapper
    }
}
