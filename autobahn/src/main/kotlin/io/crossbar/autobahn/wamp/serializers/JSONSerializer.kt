package io.crossbar.autobahn.wamp.serializers

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import io.crossbar.autobahn.wamp.interfaces.ISerializer

class JSONSerializer : ISerializer {

    companion object {
        const val NAME = "wamp.2.json"
        const val RAWSOCKET_SERIALIZER_ID = 1
    }

    constructor() : super(JsonFactory())
    constructor(objectMapper: ObjectMapper) : super(objectMapper)

    override fun isBinary(): Boolean {
        return false
    }
}
