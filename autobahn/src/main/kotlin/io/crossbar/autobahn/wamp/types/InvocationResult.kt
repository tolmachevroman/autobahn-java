package io.crossbar.autobahn.wamp.types

class InvocationResult {
    @JvmField val results: List<Any?>?
    @JvmField val kwresults: Map<String, Any?>?

    constructor(results: List<Any?>?, kwresults: Map<String, Any?>?) {
        this.results = results
        this.kwresults = kwresults
    }

    constructor(result: Any?) {
        this.results = if (result != null) listOf(result) else null
        this.kwresults = null
    }

    constructor(results: List<Any?>?) {
        this.results = results
        this.kwresults = null
    }

    constructor() {
        this.results = null
        this.kwresults = null
    }
}
