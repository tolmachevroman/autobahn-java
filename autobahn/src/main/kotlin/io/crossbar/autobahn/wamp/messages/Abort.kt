package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil
import io.crossbar.autobahn.wamp.utils.Shortcuts.getOrDefault

class Abort(
    @JvmField val reason: String,
    @JvmField val message: String?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 3

        @JvmStatic
        fun parse(wmsg: List<Any>): Abort {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "ABORT", 3)
            val details = wmsg[1] as Map<String, Any>
            val message = getOrDefault(details, "message", null)
            val reason = wmsg[2] as String
            return Abort(reason, message)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        val details = HashMap<String, Any?>()
        if (message != null) {
            details["message"] = message
        }
        marshaled.add(details)
        marshaled.add(reason)
        return marshaled
    }
}
