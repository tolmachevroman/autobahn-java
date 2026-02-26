package io.crossbar.autobahn.wamp.types

class ReceptionResult(
    @JvmField val wasEncrypted: Boolean,
    @JvmField val encAlgo: String?,
    @JvmField val encKey: Any?,
    @JvmField val encSerializer: String?,
    @JvmField val payload: ByteArray?
)
