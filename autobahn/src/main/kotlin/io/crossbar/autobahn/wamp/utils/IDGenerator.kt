package io.crossbar.autobahn.wamp.utils

class IDGenerator {
    private var nextId: Long = 0

    fun next(): Long {
        nextId += 1
        if (nextId > 9007199254740992L) {
            nextId = 1
        }
        return nextId
    }
}
