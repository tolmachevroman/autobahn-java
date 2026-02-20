package io.crossbar.autobahn.websocket.messages

/// Initial WebSockets handshake (client request).
class ClientHandshake : Message {
    @JvmField
    var host: String
    @JvmField
    var path: String
    @JvmField
    var query: String? = null
    @JvmField
    var origin: String? = null
    @JvmField
    var subprotocols: Array<String>? = null
    @JvmField
    var headerList: Map<String, String>? = null

    constructor(host: String) {
        this.host = host
        this.path = "/"
    }

    internal constructor(host: String, path: String, origin: String?) : this(host) {
        this.path = path
        this.origin = origin
    }

    internal constructor(host: String, path: String, origin: String?, subprotocols: Array<String>?) : this(host, path, origin) {
        this.subprotocols = subprotocols
    }
}
