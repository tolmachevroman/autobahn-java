package io.crossbar.autobahn.websocket.messages

/// An exception occurred in the WS reader or WS writer.
class Error(@JvmField var exception: Exception) : Message()
