package io.crossbar.autobahn.websocket.messages

/// WebSockets binary message to send or received.
class BinaryMessage(@JvmField var payload: ByteArray) : Message()
