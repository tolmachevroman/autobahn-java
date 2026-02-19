package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Unsubscribed(
    @JvmField val request: Long,
    @JvmField val subscription: Long,
    @JvmField val reason: String?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 35
        private const val SUBSCRIPTION_NULL = -1L

        @JvmStatic
        fun parse(wmsg: List<Any>): Unsubscribed {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "UNSUBSCRIBED", 2, 3)

            val request = MessageUtil.parseLong(wmsg[1])
            var subscription = SUBSCRIPTION_NULL
            var reason: String? = null
            if (wmsg.size > 2) {
                val details = wmsg[2] as Map<String, Any>
                if (details.containsKey("subscription")) {
                    subscription = MessageUtil.parseLong(details["subscription"])
                }
                if (details.containsKey("reason")) {
                    reason = details["reason"] as String
                }
            }

            return Unsubscribed(request, subscription, reason)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        if (subscription != SUBSCRIPTION_NULL || reason != null) {
            val details = HashMap<String, Any?>()
            if (reason != null) {
                details["reason"] = reason
            }
            if (subscription != SUBSCRIPTION_NULL) {
                details["subscription"] = subscription
            }
            marshaled.add(details)
        }
        return marshaled
    }
}
