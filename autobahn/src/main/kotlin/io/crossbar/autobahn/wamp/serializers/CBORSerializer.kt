package io.crossbar.autobahn.wamp.serializers

import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import io.crossbar.autobahn.wamp.interfaces.ISerializer

class CBORSerializer : ISerializer(CBORFactory()) {

    companion object {
        const val NAME = "wamp.2.cbor"
        const val RAWSOCKET_SERIALIZER_ID = 3
    }
}
