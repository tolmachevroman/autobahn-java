package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Authenticate(
    @JvmField val signature: String,
    @JvmField val extra: Map<String, Any>?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 5

        @JvmStatic
        fun parse(wmsg: List<Any>): Authenticate {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "AUTHENTICATE", 3)
            return Authenticate(wmsg[1] as String, wmsg[2] as? Map<String, Any>)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(signature)
        marshaled.add(extra ?: HashMap<String, Any>())
        return marshaled
    }
}
