package io.crossbar.autobahn.wamp.coroutines

import io.crossbar.autobahn.wamp.Client
import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.types.SessionDetails
import io.crossbar.autobahn.wamp.types.CloseDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * DSL builder for creating a WAMP session with coroutines.
 *
 * Example usage:
 * ```kotlin
 * val session = wampSession("ws://localhost:8080/ws", "realm1") {
 *     onJoin { details ->
 *         println("Joined realm: ${details.realm}")
 *     }
 *     
 *     onLeave { details ->
 *         println("Left realm: ${details.reason}")
 *     }
 * }
 * 
 * // Connect and join
 * session.connectSuspend()
 * 
 * // Subscribe to a topic
 * val subscription = session.subscribeSuspend("com.example.topic") { args, kwargs ->
 *     println("Received: $args")
 * }
 * 
 * // Call a procedure
 * val result = session.callSuspend("com.example.procedure", listOf("arg1"))
 * 
 * // Clean up
 * session.leave()
 * ```
 */
class CoroutineSessionBuilder private constructor(
    private val url: String,
    private val realm: String
) {
    private val session = Session()
    private var onJoinHandler: (suspend (SessionDetails) -> Unit)? = null
    private var onLeaveHandler: (suspend (CloseDetails) -> Unit)? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    companion object {
        fun create(url: String, realm: String, block: CoroutineSessionBuilder.() -> Unit): CoroutineSessionBuilder {
            return CoroutineSessionBuilder(url, realm).apply(block)
        }
    }
    
    /**
     * Called when the session successfully joins a realm.
     */
    fun onJoin(handler: suspend (SessionDetails) -> Unit) {
        onJoinHandler = handler
        session.addOnJoinListener { _, details ->
            scope.launch {
                handler(details)
            }
        }
    }
    
    /**
     * Called when the session leaves a realm.
     */
    fun onLeave(handler: suspend (CloseDetails) -> Unit) {
        onLeaveHandler = handler
        session.addOnLeaveListener { _, details ->
            scope.launch {
                handler(details)
            }
        }
    }
    
    /**
     * Connect and join the realm (suspend function).
     */
    suspend fun connectSuspend(): SessionDetails {
        val client = Client(session, url, realm)
        // Start connection in background
        val exitFuture = client.connect()
        // Wait for join
        return session.joinSuspend(realm)
    }
    
    /**
     * Get the underlying Session object for direct access.
     */
    fun getSession(): Session = session
    
    /**
     * Cancel the coroutine scope and clean up.
     */
    fun cancel() {
        scope.cancel()
    }
}

/**
 * Top-level DSL function for creating a WAMP session with coroutines.
 */
fun wampSession(
    url: String,
    realm: String,
    block: CoroutineSessionBuilder.() -> Unit
): CoroutineSessionBuilder {
    return CoroutineSessionBuilder.create(url, realm, block)
}
