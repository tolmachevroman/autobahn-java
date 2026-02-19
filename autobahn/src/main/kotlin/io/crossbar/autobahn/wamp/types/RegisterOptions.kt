package io.crossbar.autobahn.wamp.types

class RegisterOptions(
    @JvmField var match: String?,
    @JvmField var invoke: String?
) {
    companion object {
        const val MATCH_EXACT = "exact"
        const val MATCH_PREFIX = "prefix"
        const val MATCH_WILDCARD = "wildcard"

        const val INVOKE_SINGLE = "single"
        const val INVOKE_FIRST = "first"
        const val INVOKE_LAST = "last"
        const val INVOKE_ROUNDROBIN = "roundrobin"
        const val INVOKE_RANDOM = "random"
        const val INVOKE_ALL = "all"
    }

    fun message_attr(): Map<String, Any?>? {
        // TODO: implement.
        return null
    }
}
