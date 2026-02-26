package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.exceptions.ProtocolError
import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.types.SubscribeOptions
import io.crossbar.autobahn.wamp.utils.MessageUtil
import java.util.Map

class Subscribe(
    @JvmField val request: Long,
    @JvmField val options: SubscribeOptions,
    @JvmField val topic: String
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 32
        private const val MATCH_EXACT = "exact"
        private const val MATCH_PREFIX = "prefix"
        private const val MATCH_WILDCARD = "wildcard"

        @JvmStatic
        fun parse(wmsg: List<Any>): Subscribe {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "SUBSCRIBE", 4)
            val request = MessageUtil.parseLong(wmsg[1])
            @Suppress("UNCHECKED_CAST")
            val options = SubscribeOptions(wmsg[2] as Map<String, Any>)

            val match = options.getMatch()
            if (match != null && match != MATCH_EXACT && match != MATCH_PREFIX && match != MATCH_WILDCARD) {
                throw ProtocolError("match must be one of exact, prefix or wildcard.")
            }

            val topic = wmsg[3] as String
            return Subscribe(request, options, topic)
        }
    }

    init {
        val match = options.getMatch()
        if (match != null && match != MATCH_EXACT && match != MATCH_PREFIX && match != MATCH_WILDCARD) {
            throw IllegalArgumentException("match must be one of exact, prefix or wildcard.")
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        @Suppress("UNCHECKED_CAST")
        val optionsCopy = SubscribeOptions(options as Map<String, Any>)
        val match = optionsCopy.getMatch()
        if (match == MATCH_EXACT) {
            optionsCopy.removeMatch()
        }
        marshaled.add(optionsCopy)
        marshaled.add(topic)
        return marshaled
    }
}
