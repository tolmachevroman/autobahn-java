package io.crossbar.autobahn.wamp.coroutines

import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.interfaces.IAuthenticator
import io.crossbar.autobahn.wamp.interfaces.IInvocationHandler
import io.crossbar.autobahn.wamp.types.CallOptions
import io.crossbar.autobahn.wamp.types.CallResult
import io.crossbar.autobahn.wamp.types.InvocationDetails
import io.crossbar.autobahn.wamp.types.InvocationResult
import io.crossbar.autobahn.wamp.types.Publication
import io.crossbar.autobahn.wamp.types.PublishOptions
import io.crossbar.autobahn.wamp.types.Registration
import io.crossbar.autobahn.wamp.types.RegisterOptions
import io.crossbar.autobahn.wamp.types.SessionDetails
import io.crossbar.autobahn.wamp.types.SubscribeOptions
import io.crossbar.autobahn.wamp.types.Subscription
import java8.util.concurrent.CompletableFuture
import java8.util.function.Consumer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Suspends until the session is connected and joined to a realm.
 *
 * @param realm The realm to join
 * @param authenticators List of authenticators to use (optional)
 * @return SessionDetails of the joined session
 */
suspend fun Session.joinSuspend(
    realm: String,
    authenticators: List<IAuthenticator>? = null
): SessionDetails {
    return if (authenticators != null) {
        join(realm, authenticators).await()
    } else {
        join(realm).await()
    }
}

/**
 * Suspends until subscription is established.
 *
 * @param topic The topic URI to subscribe to
 * @param handler The event handler callback (args only)
 * @param options Subscription options (optional)
 * @return The established Subscription
 */
suspend fun Session.subscribeSuspend(
    topic: String,
    handler: (List<Any>?) -> Unit,
    options: SubscribeOptions? = null
): Subscription {
    val future = if (options != null) {
        subscribe(topic, Consumer { args -> handler(args) }, options)
    } else {
        subscribe(topic, Consumer { args -> handler(args) })
    }
    return future.await()
}

/**
 * Suspends until publication is confirmed.
 *
 * @param topic The topic URI to publish to
 * @param args Positional arguments (optional)
 * @param kwargs Keyword arguments (optional)
 * @param options Publish options (optional)
 * @return The Publication confirmation
 */
suspend fun Session.publishSuspend(
    topic: String,
    args: List<Any>? = null,
    kwargs: Map<String, Any>? = null,
    options: PublishOptions? = null
): Publication {
    val future = if (options != null) {
        publish(topic, args, kwargs, options)
    } else {
        publish(topic, args, kwargs)
    }
    return future.await()
}

/**
 * Suspends until registration is established.
 *
 * @param procedure The procedure URI to register
 * @param endpoint The invocation handler returning CompletableFuture<InvocationResult>
 * @param options Registration options (optional)
 * @return The established Registration
 */
suspend fun Session.registerSuspend(
    procedure: String,
    endpoint: IInvocationHandler,
    options: RegisterOptions? = null
): Registration {
    val future = if (options != null) {
        register(procedure, endpoint, options)
    } else {
        register(procedure, endpoint)
    }
    return future.await()
}

/**
 * Suspends until procedure call result is received.
 *
 * @param procedure The procedure URI to call
 * @param args Positional arguments (optional)
 * @param kwargs Keyword arguments (optional)
 * @param options Call options (optional)
 * @return The CallResult
 */
suspend fun Session.callSuspend(
    procedure: String,
    args: List<Any>? = null,
    kwargs: Map<String, Any>? = null,
    options: CallOptions? = null
): CallResult {
    val future = if (options != null) {
        call(procedure, args, kwargs, options)
    } else {
        call(procedure, args, kwargs)
    }
    return future.await()
}

/**
 * Suspends until unsubscription is confirmed.
 *
 * @param subscription The subscription to unsubscribe
 */
suspend fun Session.unsubscribeSuspend(subscription: Subscription): Int {
    return unsubscribe(subscription).await()
}

/**
 * Suspends until unregistration is confirmed.
 *
 * @param registration The registration to unregister
 */
suspend fun Session.unregisterSuspend(registration: Registration): Int {
    return unregister(registration).await()
}

/**
 * Extension function to convert CompletableFuture to suspend function.
 */
suspend fun <T> CompletableFuture<T>.await(): T = suspendCancellableCoroutine { continuation ->
    whenComplete { result, error ->
        if (error != null) {
            continuation.resumeWithException(error)
        } else {
            continuation.resume(result)
        }
    }
}
