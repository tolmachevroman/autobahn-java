package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Subscribed(
    @JvmField val request: Long,
    @JvmField val subscription: Long
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 33

        @JvmStatic
        fun parse(wmsg: List<Any>): Subscribed {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "SUBSCRIBED", 3)
            return Subscribed(MessageUtil.parseLong(wmsg[1]), MessageUtil.parseLong(wmsg[2]))
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
