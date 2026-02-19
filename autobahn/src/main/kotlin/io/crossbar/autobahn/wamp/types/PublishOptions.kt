package io.crossbar.autobahn.wamp.types

class PublishOptions(
    @JvmField val acknowledge: Boolean,
    @JvmField val excludeMe: Boolean,
    @JvmField val retain: Boolean = false
) {
    // Secondary constructor for 2-arg calls
    constructor(acknowledge: Boolean, excludeMe: Boolean) : this(acknowledge, excludeMe, false)
}
