package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.exceptions.ProtocolError
import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil
import io.crossbar.autobahn.wamp.utils.Shortcuts.getOrDefault

class Interrupt(
    @JvmField val request: Long,
    @JvmField val mode: String?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 69
        private const val ABORT = "abort"
        private const val KILL = "kill"

        @JvmStatic
        fun parse(wmsg: List<Any>): Interrupt {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "INTERRUPT", 3)
            val request = MessageUtil.parseLong(wmsg[1])
            val options = wmsg[2] as Map<String, Any>
            val mode = getOrDefault(options, "mode", null)
            if (mode != null) {
                if (mode != ABORT && mode != KILL) {
                    throw ProtocolError("invalid value $mode for 'mode' option in INTERRUPT")
                }
            }
            return Interrupt(request, mode)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        if (mode != null) {
            val options = HashMap<String, Any>()
            options["mode"] = mode
            marshaled.add(options)
        } else {
            marshaled.add(emptyMap<Any, Any>())
        }
        return marshaled
    }
}
