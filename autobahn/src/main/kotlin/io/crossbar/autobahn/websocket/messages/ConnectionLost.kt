package io.crossbar.autobahn.websocket.messages

/// WebSockets connection lost
class ConnectionLost(@JvmField val reason: String) : Message() {
    constructor() : this("WebSockets connection lost")
}
