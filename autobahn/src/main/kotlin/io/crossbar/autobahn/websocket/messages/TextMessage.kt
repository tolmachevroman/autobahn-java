package io.crossbar.autobahn.websocket.messages

/// WebSockets text message to send or received.
class TextMessage(@JvmField var payload: String) : Message()
