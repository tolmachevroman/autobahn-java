package io.crossbar.autobahn.websocket.messages

/// WebSockets raw (UTF-8) text message to send or received.
class RawTextMessage(@JvmField var payload: ByteArray) : Message()
