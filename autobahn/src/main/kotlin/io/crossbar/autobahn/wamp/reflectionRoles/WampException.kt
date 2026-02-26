package io.crossbar.autobahn.wamp.reflectionRoles

class WampException : Exception {
    private val errorUri: String
    private val details: Map<String, Any>?
    private val arguments: List<Any>?
    private val kwArguments: Map<String, Any>?

    constructor(errorUri: String, vararg arguments: Any) : super() {
        this.errorUri = errorUri
        this.details = null
        this.arguments = arguments.toList()
        this.kwArguments = null
    }

    constructor(
        errorUri: String,
        details: Map<String, Any>?,
        arguments: List<Any>?,
        kwArguments: Map<String, Any>?
    ) : super() {
        this.errorUri = errorUri
        this.details = details
        this.arguments = arguments
        this.kwArguments = kwArguments
    }

    fun getErrorUri(): String = errorUri
    fun getDetails(): Map<String, Any>? = details
    fun getArguments(): List<Any>? = arguments
    fun getKwArguments(): Map<String, Any>? = kwArguments
}
