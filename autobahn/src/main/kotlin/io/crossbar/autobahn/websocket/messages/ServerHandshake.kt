package io.crossbar.autobahn.websocket.messages

/// Initial WebSockets handshake (server response).
class ServerHandshake(@JvmField var headers: Map<String, String>?) : Message()
