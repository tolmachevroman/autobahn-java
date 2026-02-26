package io.crossbar.autobahn.websocket.messages

/// WebSockets ping to send or received.
class Ping : Message {
    @JvmField
    var payload: ByteArray? = null

    constructor()

    constructor(payload: ByteArray?) {
        this.payload = payload
    }
}
