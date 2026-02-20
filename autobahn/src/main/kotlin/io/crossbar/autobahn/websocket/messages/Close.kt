package io.crossbar.autobahn.websocket.messages

/// WebSockets close to send or received.
class Close : Message {
    @JvmField
    var code: Int = -1
    @JvmField
    var reason: String? = null
    // Not to be delivered on the wire, only for local use.
    @JvmField
    var isReply: Boolean = false

    constructor()

    constructor(code: Int) {
        this.code = code
    }

    // For local use only.
    constructor(code: Int, isReply: Boolean) {
        this.code = code
        this.isReply = isReply
    }

    constructor(code: Int, reason: String?) {
        this.code = code
        this.reason = reason
    }

    constructor(code: Int, reason: String?, isReply: Boolean) {
        this.code = code
        this.isReply = isReply
        this.reason = reason
    }
}
