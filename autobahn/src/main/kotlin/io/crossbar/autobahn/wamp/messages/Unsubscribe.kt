package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Unsubscribe(
    @JvmField val request: Long,
    @JvmField val subscription: Long
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 34

        @JvmStatic
        fun parse(wmsg: List<Any>): Unsubscribe {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "UNSUBSCRIBE", 3)
            return Unsubscribe(MessageUtil.parseLong(wmsg[1]), MessageUtil.parseLong(wmsg[2]))
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        marshaled.add(subscription)
        return marshaled
    }
}
