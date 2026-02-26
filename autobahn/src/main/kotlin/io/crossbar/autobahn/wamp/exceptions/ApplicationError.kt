package io.crossbar.autobahn.wamp.exceptions

class ApplicationError : Error {
    @JvmField val args: List<Any>?
    @JvmField val kwargs: Map<String, Any>?

    constructor(message: String, args: List<Any>?, kwargs: Map<String, Any>?) : super(message) {
        this.args = args
        this.kwargs = kwargs
    }

    constructor(message: String) : super(message) {
        this.args = null
        this.kwargs = null
    }
}
