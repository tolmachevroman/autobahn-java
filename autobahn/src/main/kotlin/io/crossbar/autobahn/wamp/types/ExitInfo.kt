package io.crossbar.autobahn.wamp.types

class ExitInfo {
    @JvmField val exitCode: Int
    @JvmField val reason: String?

    constructor(wasClean: Boolean) {
        this.exitCode = if (wasClean) 0 else 1
        this.reason = null
    }

    constructor(exitCode: Int, reason: String?) {
        this.exitCode = exitCode
        this.reason = reason
    }
}
