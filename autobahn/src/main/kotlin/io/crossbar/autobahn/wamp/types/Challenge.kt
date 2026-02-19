package io.crossbar.autobahn.wamp.types

class Challenge(
    @JvmField val authMethod: String,
    @JvmField val extra: Map<String, Any?>
)
