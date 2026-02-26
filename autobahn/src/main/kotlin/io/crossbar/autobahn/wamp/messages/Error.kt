package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.exceptions.ProtocolError
import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Error(
    @JvmField val requestType: Int,
    @JvmField val request: Long,
    @JvmField val error: String,
    @JvmField val args: List<Any>?,
    @JvmField val kwargs: Map<String, Any>?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 8

        @JvmStatic
        fun parse(wmsg: List<Any>): Error {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "ERROR", 5, 7)
            val requestType = wmsg[1] as Int
            val request = MessageUtil.parseLong(wmsg[2])
            val details = wmsg[3] as Map<String, Any>
            val error = wmsg[4] as String

            if (wmsg.size == 6 && wmsg[5] is ByteArray) {
                throw ProtocolError("Binary payload not supported")
            }

            val args = if (wmsg.size > 5) wmsg[5] as? List<Any> else null
            val kwargs = if (wmsg.size > 6) wmsg[6] as? Map<String, Any> else null

            return Error(requestType, request, error, args, kwargs)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(requestType)
        marshaled.add(request)
        marshaled.add(HashMap<String, Any>())
        marshaled.add(error)
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
