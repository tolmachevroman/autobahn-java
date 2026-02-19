package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Unregister(
    @JvmField val request: Long,
    @JvmField val registration: Long
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 66

        @JvmStatic
        fun parse(wmsg: List<Any>): Unregister {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "UNREGISTER", 3)
            return Unregister(MessageUtil.parseLong(wmsg[1]), MessageUtil.parseLong(wmsg[2]))
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
