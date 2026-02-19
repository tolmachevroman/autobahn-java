package io.crossbar.autobahn.wamp.types

import io.crossbar.autobahn.wamp.Session

class EventDetails(
    @JvmField val subscription: Subscription,
    @JvmField val publication: Long,
    @JvmField val topic: String,
    @JvmField val retained: Boolean,
    @JvmField val publisherSessionID: Long,
    @JvmField val publisherAuthID: String?,
    @JvmField val publisherAuthRole: String?,
    @JvmField val session: Session?
)
