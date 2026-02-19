package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil
import io.crossbar.autobahn.wamp.utils.Shortcuts.getOrDefault

class Unregistered(
    @JvmField val request: Long,
    @JvmField val registration: Long,
    @JvmField val reason: String?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 67
        private const val REGISTRATION_NULL = -1L

        @JvmStatic
        fun parse(wmsg: List<Any>): Unregistered {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "UNREGISTERED", 2, 3)

            var registration = REGISTRATION_NULL
            var reason: String? = null
            if (wmsg.size > 2) {
                val details = wmsg[2] as Map<String, Any>
                registration = getOrDefault(details, "registration", registration)
                reason = getOrDefault(details, "reason", reason)
            }

            return Unregistered(MessageUtil.parseLong(wmsg[1]), registration, reason)
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        if (registration != REGISTRATION_NULL || reason != null) {
            val details = HashMap<String, Any?>()
            if (reason != null) {
                details["reason"] = reason
            }
            if (registration != REGISTRATION_NULL) {
                details["registration"] = registration
            }
            marshaled.add(details)
        }
        return marshaled
    }
}
