package io.crossbar.autobahn.wamp.types

class SessionDetails(
    @JvmField val realm: String,
    @JvmField val sessionID: Long,
    @JvmField val authid: String?,
    @JvmField val authrole: String?,
    @JvmField val authmethod: String?
)
