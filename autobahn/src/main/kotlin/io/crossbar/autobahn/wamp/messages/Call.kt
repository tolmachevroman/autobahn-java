package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.exceptions.ProtocolError
import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil
import io.crossbar.autobahn.wamp.utils.Shortcuts.getOrDefault

class Call(
    @JvmField val request: Long,
    @JvmField val procedure: String,
    @JvmField val args: List<Any>?,
    @JvmField val kwargs: Map<String, Any>?,
    @JvmField val timeout: Int
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 48
        private const val TIMEOUT_DEFAULT = 0

        @JvmStatic
        fun parse(wmsg: List<Any>): Call {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "CALL", 4, 6)

            val request = MessageUtil.parseLong(wmsg[1])
            val options = wmsg[2] as Map<String, Any>
            val procedure = wmsg[3] as String

            val args = if (wmsg.size > 4) {
                if (wmsg[4] is ByteArray) {
                    throw ProtocolError("Binary payload not supported")
                }
                wmsg[4] as? List<Any>
            } else null

            val kwargs = if (wmsg.size > 5) wmsg[5] as? Map<String, Any> else null

            val timeout = getOrDefault(options, "timeout", TIMEOUT_DEFAULT) ?: TIMEOUT_DEFAULT

            return Call(request, procedure, args, kwargs, timeout)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        val options = HashMap<String, Any>()
        if (timeout > TIMEOUT_DEFAULT) {
            options["timeout"] = timeout
        }
        marshaled.add(options)
        marshaled.add(procedure)
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
