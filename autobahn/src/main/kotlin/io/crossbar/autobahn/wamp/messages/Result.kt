package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.exceptions.ProtocolError
import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil
import io.crossbar.autobahn.wamp.utils.Shortcuts.getOrDefault

class Result(
    @JvmField val request: Long,
    @JvmField val args: List<Any>?,
    @JvmField val kwargs: Map<String, Any>?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 50

        @JvmStatic
        fun parse(wmsg: List<Any>): Result {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "RESULT", 3, 5)

            val request = MessageUtil.parseLong(wmsg[1])
            val args = if (wmsg.size > 3) {
                if (wmsg[3] is ByteArray) {
                    throw ProtocolError("Binary payload not supported")
                }
                wmsg[3] as? List<Any>
            } else null

            val kwargs = if (wmsg.size > 4) wmsg[4] as? Map<String, Any> else null

            return Result(request, args, kwargs)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        marshaled.add(emptyMap<Any, Any>())
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
