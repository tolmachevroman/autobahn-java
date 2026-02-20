package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.exceptions.ProtocolError
import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil
import io.crossbar.autobahn.wamp.utils.Shortcuts.getOrDefault

class Publish(
    @JvmField val request: Long,
    @JvmField val topic: String,
    @JvmField val args: List<Any>?,
    @JvmField val kwargs: Map<String, Any>?,
    @JvmField val acknowledge: Boolean,
    @JvmField val excludeMe: Boolean,
    @JvmField val retain: Boolean
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 16

        @JvmStatic
        fun parse(wmsg: List<Any>): Publish {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "PUBLISH", 4, 6)
            val request = MessageUtil.parseLong(wmsg[1])
            val options = wmsg[2] as Map<String, Any>
            val topic = wmsg[3] as String

            val args = if (wmsg.size > 4) {
                if (wmsg[4] is ByteArray) {
                    throw ProtocolError("Binary payload not supported")
                }
                wmsg[4] as? List<Any>
            } else null

            val kwargs = if (wmsg.size > 5) wmsg[5] as? Map<String, Any> else null

            val acknowledge = getOrDefault(options, "acknowledge", false) ?: false
            val excludeMe = getOrDefault(options, "exclude_me", true) ?: true
            val retain = getOrDefault(options, "retain", false) ?: false

            return Publish(request, topic, args, kwargs, acknowledge, excludeMe, retain)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        val options = HashMap<String, Any>()
        if (acknowledge) {
            options["acknowledge"] = acknowledge
        }
        if (!excludeMe) {
            options["exclude_me"] = excludeMe
        }
        if (retain) {
            options["retain"] = retain
        }
        marshaled.add(options)
        marshaled.add(topic)
        if (kwargs != null) {
            if (args == null) {
                marshaled.add(emptyList<Any>())
            } else {
                marshaled.add(args)
            }
            marshaled.add(kwargs)
        } else if (args != null) {
            marshaled.add(args)
        }
        return marshaled
    }
}
