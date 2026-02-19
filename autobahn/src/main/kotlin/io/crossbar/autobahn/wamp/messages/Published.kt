package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Published(
    @JvmField val request: Long,
    @JvmField val publication: Long
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 17

        @JvmStatic
        fun parse(wmsg: List<Any>): Published {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "PUBLISHED", 3)
            return Published(MessageUtil.parseLong(wmsg[1]), MessageUtil.parseLong(wmsg[2]))
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        marshaled.add(publication)
        return marshaled
    }
}
