package io.crossbar.autobahn.wamp.coroutines

import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.types.SubscribeOptions
import io.crossbar.autobahn.wamp.types.Subscription
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java8.util.function.Consumer

/**
 * Subscribes to a topic and returns a Flow of events.
 * The flow collects events until the subscription is cancelled.
 *
 * @param topic The topic URI to subscribe to
 * @param options Subscription options (optional)
 * @return A Flow of event args
 */
fun Session.subscribeAsFlow(
    topic: String,
    options: SubscribeOptions? = null
): Flow<List<Any>?> = callbackFlow {
    val handler = Consumer<List<Any>?> { args ->
        trySend(args)
    }
    
    val subscriptionFuture = if (options != null) {
        subscribe(topic, handler, options)
    } else {
        subscribe(topic, handler)
    }
    
    // Wait for subscription to be established
    val subscription = subscriptionFuture.await()
    
    // When the flow collector is cancelled, unsubscribe
    awaitClose {
        try {
            unsubscribe(subscription).get()
        } catch (e: Exception) {
            // Ignore unsubscribe errors on close
        }
    }
}

/**
 * Data class representing a WAMP event.
 */
data class WampEvent(
    val args: List<Any>?,
    val topic: String? = null
)

/**
 * Subscribe and receive events as a Flow of WampEvent objects.
 */
fun Session.subscribeAsFlowV2(
    topic: String,
    options: SubscribeOptions? = null
): Flow<WampEvent> = callbackFlow {
    val handler = Consumer<List<Any>?> { args ->
        trySend(WampEvent(args, topic))
    }
    
    val subscription = if (options != null) {
        subscribe(topic, handler, options)
    } else {
        subscribe(topic, handler)
    }.await()
    
    awaitClose {
        try {
            unsubscribe(subscription).get()
        } catch (e: Exception) {
            // Ignore errors on close
        }
    }
}
