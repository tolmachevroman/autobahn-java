package io.crossbar.autobahn.wamp.types

class ChallengeResponse(
    @JvmField val signature: String,
    @JvmField val extra: Map<String, Any?>? = null
)
