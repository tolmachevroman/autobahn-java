package io.crossbar.autobahn.wamp.types

import io.crossbar.autobahn.wamp.Session

class InvocationDetails(
    @JvmField val registration: Registration,
    @JvmField val procedure: String,
    @JvmField val callerSessionID: Long,
    @JvmField val callerAuthID: String?,
    @JvmField val callerAuthRole: String?,
    @JvmField val session: Session?
)
