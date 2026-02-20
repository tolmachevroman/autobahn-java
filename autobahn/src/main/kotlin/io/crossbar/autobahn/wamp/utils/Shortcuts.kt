package io.crossbar.autobahn.wamp.utils

object Shortcuts {
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getOrDefault(obj: Map<*, *>, key: Any?, defaultValue: T?): T? {
        return if (obj.containsKey(key)) {
            obj[key] as? T
        } else {
            defaultValue
        }
    }
}
