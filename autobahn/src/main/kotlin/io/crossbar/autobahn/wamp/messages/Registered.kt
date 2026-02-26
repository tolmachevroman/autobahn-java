package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Registered(
    @JvmField val request: Long,
    @JvmField val registration: Long
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 65

        @JvmStatic
        fun parse(wmsg: List<Any>): Registered {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "REGISTERED", 3)
            return Registered(MessageUtil.parseLong(wmsg[1]), MessageUtil.parseLong(wmsg[2]))
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        marshaled.add(registration)
        return marshaled
    }
}
