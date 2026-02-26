package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.exceptions.ProtocolError
import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Invocation(
    @JvmField val request: Long,
    @JvmField val registration: Long,
    @JvmField val details: Map<String, Any>?,
    @JvmField val args: List<Any>?,
    @JvmField val kwargs: Map<String, Any>?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 68

        @JvmStatic
        fun parse(wmsg: List<Any>): Invocation {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "INVOCATION", 4, 6)
            val request = MessageUtil.parseLong(wmsg[1])
            val registration = MessageUtil.parseLong(wmsg[2])
            val details = wmsg[3] as? Map<String, Any>

            val args = if (wmsg.size > 4) {
                if (wmsg[4] is ByteArray) {
                    throw ProtocolError("Binary payload not supported")
                }
                wmsg[4] as? List<Any>
            } else null

            val kwargs = if (wmsg.size > 5) wmsg[5] as? Map<String, Any> else null

            return Invocation(request, registration, details, args, kwargs)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        marshaled.add(registration)
        if (details == null) {
            marshaled.add(emptyMap<Any, Any>())
        } else {
            marshaled.add(details)
        }
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
