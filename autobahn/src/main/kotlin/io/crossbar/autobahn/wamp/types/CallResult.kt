package io.crossbar.autobahn.wamp.types

import com.fasterxml.jackson.core.type.TypeReference

class CallResult {
    @JvmField val results: List<Any?>?
    @JvmField val kwresults: Map<String, Any?>?
    @JvmField val resultTypeRef: TypeReference<*>?
    @JvmField val resultTypeClass: Class<*>?

    constructor(results: List<Any?>?, kwresults: Map<String, Any?>?) {
        this.results = results
        this.kwresults = kwresults
        this.resultTypeRef = null
        this.resultTypeClass = null
    }

    constructor(results: List<Any?>?) {
        this.results = results
        this.kwresults = null
        this.resultTypeRef = null
        this.resultTypeClass = null
    }

    constructor() {
        this.results = null
        this.kwresults = null
        this.resultTypeRef = null
        this.resultTypeClass = null
    }

    constructor(
        results: List<Any?>?,
        kwresults: Map<String, Any?>?,
        resultTypeRef: TypeReference<*>?,
        resultTypeClass: Class<*>?
    ) {
        this.results = results
        this.kwresults = kwresults
        this.resultTypeRef = resultTypeRef
        this.resultTypeClass = resultTypeClass
    }
}
