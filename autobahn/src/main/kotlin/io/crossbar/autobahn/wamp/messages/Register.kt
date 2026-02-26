package io.crossbar.autobahn.wamp.messages

import io.crossbar.autobahn.wamp.exceptions.ProtocolError
import io.crossbar.autobahn.wamp.interfaces.IMessage
import io.crossbar.autobahn.wamp.utils.MessageUtil

class Register(
    @JvmField val request: Long,
    @JvmField val procedure: String,
    @JvmField val match: String?,
    @JvmField val invoke: String?
) : IMessage {

    companion object {
        const val MESSAGE_TYPE = 64
        private const val MATCH_EXACT = "exact"
        private const val MATCH_PREFIX = "prefix"
        private const val MATCH_WILDCARD = "wildcard"

        private const val INVOKE_SINGLE = "single"
        private const val INVOKE_FIRST = "first"
        private const val INVOKE_LAST = "last"
        private const val INVOKE_ROUNDROBIN = "roundrobin"
        private const val INVOKE_RANDOM = "random"
        private const val INVOKE_ALL = "all"

        @JvmStatic
        fun parse(wmsg: List<Any>): Register {
            MessageUtil.validateMessage(wmsg, MESSAGE_TYPE, "REGISTER", 4)
            val request = MessageUtil.parseLong(wmsg[1])
            val options = wmsg[2] as Map<String, Any>

            var match: String? = null
            if (options.containsKey("match")) {
                match = options["match"] as String
                if (match != MATCH_EXACT && match != MATCH_PREFIX && match != MATCH_WILDCARD) {
                    throw ProtocolError("match must be one of exact, prefix or wildcard.")
                }
            }

            var invoke: String? = null
            if (options.containsKey("invoke")) {
                invoke = options["invoke"] as String
                if (invoke != INVOKE_SINGLE && invoke != INVOKE_FIRST && invoke != INVOKE_LAST &&
                    invoke != INVOKE_ROUNDROBIN && invoke != INVOKE_RANDOM && invoke != INVOKE_ALL) {
                    throw IllegalArgumentException("invoke must be one of single, first, last, roundrobin, random or all.")
                }
            }

            val procedure = wmsg[3] as String
            return Register(request, procedure, match, invoke)
        }
    }

    init {
        if (match != null) {
            if (match != MATCH_EXACT && match != MATCH_PREFIX && match != MATCH_WILDCARD) {
                throw IllegalArgumentException("match must be one of exact, prefix or wildcard.")
            }
        }
        if (invoke != null) {
            if (invoke != INVOKE_SINGLE && invoke != INVOKE_FIRST && invoke != INVOKE_LAST &&
                invoke != INVOKE_ROUNDROBIN && invoke != INVOKE_RANDOM && invoke != INVOKE_ALL) {
                throw IllegalArgumentException("invoke must be one of single, first, last, roundrobin, random or all.")
            }
        }
    }

    override fun marshal(): List<Any> {
        val marshaled = ArrayList<Any>()
        marshaled.add(MESSAGE_TYPE)
        marshaled.add(request)
        val options = HashMap<String, Any?>()
        if (match != null && match != MATCH_EXACT) {
            options["match"] = match
        }
        if (invoke != null && invoke != INVOKE_SINGLE) {
            options["invoke"] = invoke
        }
        marshaled.add(options)
        marshaled.add(procedure)
        return marshaled
    }
}
