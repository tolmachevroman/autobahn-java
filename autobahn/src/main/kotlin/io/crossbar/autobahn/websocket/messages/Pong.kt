package io.crossbar.autobahn.websocket.messages

/// WebSockets pong to send or received.
class Pong : Message {
    @JvmField
    var payload: ByteArray? = null

    constructor()

    constructor(payload: ByteArray?) {
        this.payload = payload
    }
}
