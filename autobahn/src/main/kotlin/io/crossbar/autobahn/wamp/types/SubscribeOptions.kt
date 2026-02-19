package io.crossbar.autobahn.wamp.types

import java.util.HashMap
import java.util.Map

class SubscribeOptions : HashMap<String, Any> {

    companion object {
        const val KEY_MATCH = "match"
        const val KEY_GET_RETAINED = "get_retained"
    }

    constructor() : super()

    constructor(match: String?, getRetained: Boolean) : this() {
        match?.let { putMatch(it) }
        putGetRetained(getRetained)
    }

    constructor(origin: Map<String, Any>) : this() {
        putAll(origin as kotlin.collections.Map<String, Any>)
    }

    fun putMatch(match: String) {
        put(KEY_MATCH, match)
    }

    fun putGetRetained(getRetained: Boolean) {
        put(KEY_GET_RETAINED, getRetained)
    }

    fun getMatch(): String? {
        val value = get(KEY_MATCH)
        return if (value is String) value else null
    }

    fun getRetained(): Boolean? {
        val value = get(KEY_GET_RETAINED)
        return if (value is Boolean) value else null
    }

    fun removeMatch() {
        remove(KEY_MATCH)
    }

    fun removeGetRetained() {
        remove(KEY_GET_RETAINED)
    }
}
