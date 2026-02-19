package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.exceptions.ProtocolError
import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil
import io.crossbar.autobahn.wamp.utils.Shortcuts.getOrDefault

class Event(
    @JvmField val subscription: Long,
    @JvmField val publication: Long,
    @JvmField val topic: String?,
    @JvmField val retained: Boolean,
    @JvmField val args: List<Any>?,
    @JvmField val kwargs: Map<String, Any>?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 36

        @JvmStatic
        fun parse(wmsg: List<Any>): Event {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "EVENT", 3, 6)
            val subscription = MessageUtil.parseLong(wmsg[1])
            val publication = MessageUtil.parseLong(wmsg[2])
            val details = wmsg[3] as Map<String, Any>
            val topic = details["topic"] as? String
            val retained = getOrDefault(details, "retained", false)

            val args = if (wmsg.size > 4) {
                if (wmsg[4] is ByteArray) {
                    throw ProtocolError("Binary payload not supported")
                }
                wmsg[4] as? List<Any>
            } else null

            val kwargs = if (wmsg.size > 5) wmsg[5] as? Map<String, Any> else null

            return Event(subscription, publication, topic, retained, args, kwargs)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(subscription)
        marshaled.add(publication)
        val details = HashMap<String, Any?>()
        if (topic != null) {
            details["topic"] = topic
        }
        if (retained) {
            details["retained"] = retained
        }
        marshaled.add(details)
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
