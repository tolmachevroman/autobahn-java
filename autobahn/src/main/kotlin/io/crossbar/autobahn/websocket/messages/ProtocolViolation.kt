package io.crossbar.autobahn.websocket.messages

import io.crossbar.autobahn.websocket.exceptions.WebSocketException

/// WebSockets reader detected WS protocol violation.
class ProtocolViolation(@JvmField var exception: WebSocketException) : Message()
