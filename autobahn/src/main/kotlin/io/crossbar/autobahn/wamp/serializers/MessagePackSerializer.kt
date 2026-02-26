package io.crossbar.autobahn.wamp.serializers

import io.crossbar.autobahn.wamp.interfaces.ISerializer
import org.msgpack.jackson.dataformat.MessagePackFactory

class MessagePackSerializer : ISerializer(MessagePackFactory()) {

    companion object {
        const val NAME = "wamp.2.msgpack"
        const val RAWSOCKET_SERIALIZER_ID = 2
    }
}
