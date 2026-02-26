package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Challenge(
    @JvmField val method: String,
    @JvmField val extra: Map<String, Any>?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 4

        @JvmStatic
        fun parse(wmsg: List<Any>): Challenge {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "CHALLENGE", 3)
            return Challenge(wmsg[1] as String, wmsg[2] as? Map<String, Any>)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(method)
        marshaled.add(extra ?: emptyMap<Any, Any>())
        return marshaled
    }
}
