package io.crossbar.autobahn.wamp.types

class CloseDetails(
    @JvmField val reason: String,
    @JvmField val message: String?
) {
    companion object {
        const val REASON_DEFAULT = "wamp.close.normal"
        const val REASON_TRANSPORT_LOST = "wamp.close.transport_lost"
    }
}
