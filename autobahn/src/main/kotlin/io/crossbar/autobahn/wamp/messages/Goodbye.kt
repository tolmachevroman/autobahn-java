package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Goodbye(
    @JvmField val reason: String,
    @JvmField val message: String?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 6
        private const val DEFAULT_REASON = "wamp.close.normal"

        @JvmStatic
        fun parse(wmsg: List<Any>): Goodbye {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "GOODBYE", 3)
            val details = wmsg[1] as Map<String, Any>
            val message = if (details.containsKey("message")) details["message"] as? String else null
            return Goodbye(wmsg[2] as String, message)
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
