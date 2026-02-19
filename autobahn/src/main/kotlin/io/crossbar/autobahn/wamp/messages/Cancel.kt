package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil
import io.crossbar.autobahn.wamp.utils.Shortcuts.getOrDefault

class Cancel(
    @JvmField val request: Long,
    @JvmField val mode: String?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 49
        private const val SKIP = "skip"
        private const val ABORT = "abort"
        private const val KILL = "kill"

        @JvmStatic
        fun parse(wmsg: List<Any>): Cancel {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "CANCEL", 3)
            val request = MessageUtil.parseLong(wmsg[1])
            val options = wmsg[2] as Map<String, Any>
            val mode = getOrDefault(options, "mode", null)
            return Cancel(request, mode)
        }
    }

    init {
        if (mode != null) {
            if (mode != SKIP && mode != ABORT && mode != KILL) {
                throw IllegalArgumentException("mode must either be skip, abort or kill")
            }
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
