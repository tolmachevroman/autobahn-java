package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Welcome(
    @JvmField val session: Long,
    @JvmField val roles: Map<String, Map<*, *>>?,
    @JvmField val realm: String?,
    @JvmField val authid: String?,
    @JvmField val authrole: String?,
    @JvmField val authmethod: String?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 2

        @JvmStatic
        fun parse(wmsg: List<Any>): Welcome {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "WELCOME", 3)
            val session = MessageUtil.parseLong(wmsg[1])
            val details = wmsg[2] as Map<String, Any>
            val roles = details["roles"] as? Map<String, Map<*, *>>
            val realm = details["realm"] as? String
            val authid = details["authid"] as? String
            val authrole = details["authrole"] as? String
            val authmethod = details["authmethod"] as? String
            return Welcome(session, roles, realm, authid, authrole, authmethod)
        }
    }

    override fun marshal(): List<Any> {
        throw UnsupportedOperationException("Welcome only to be sent by a server library.")
    }
}
