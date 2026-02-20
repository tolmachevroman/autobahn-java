package io.crossbar.autobahn.wamp.reflectionRoles

import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.interfaces.ISerializer
import io.crossbar.autobahn.wamp.types.Registration
import io.crossbar.autobahn.wamp.types.Subscription
import io.crossbar.autobahn.wamp.utils.Platform
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java8.util.concurrent.CompletableFuture

class ReflectionServices(
    private val session: Session,
    private val serializer: ISerializer
) {

    fun registerCallee(instance: Any): List<CompletableFuture<Registration>> {
        val registrations = mutableListOf<CompletableFuture<Registration>>()
        val classType = instance::class.java

        for (method in classType.methods) {
            if (method.isAnnotationPresent(WampProcedure::class.java)) {
                val currentRegistration = registerSingleMethod(instance, method)
                registrations.add(currentRegistration)
            }
        }

        for (interfaceType in classType.interfaces) {
            for (method in interfaceType.methods) {
                if (method.isAnnotationPresent(WampProcedure::class.java)) {
                    val currentRegistration = registerSingleMethod(instance, method)
                    registrations.add(currentRegistration)
                }
            }
        }

        return registrations
    }

    private fun registerSingleMethod(instance: Any, method: Method): CompletableFuture<Registration> {
        val annotation = method.getAnnotation(WampProcedure::class.java)
        val currentMethodHandler = MethodInvocationHandler(instance, method, serializer)
        return session.register(annotation.value, currentMethodHandler)
    }

    fun registerSubscriber(instance: Any): List<CompletableFuture<Subscription>> {
        val subscriptions = mutableListOf<CompletableFuture<Subscription>>()
        val classType = instance::class.java

        for (method in classType.methods) {
            if (method.isAnnotationPresent(WampTopic::class.java)) {
                val currentSubscription = singleSubscribe(instance, method)
                subscriptions.add(currentSubscription)
            }
        }

        for (interfaceType in classType.interfaces) {
            for (method in interfaceType.methods) {
                if (method.isAnnotationPresent(WampTopic::class.java)) {
                    val currentSubscription = singleSubscribe(instance, method)
                    subscriptions.add(currentSubscription)
                }
            }
        }

        return subscriptions
    }

    private fun singleSubscribe(instance: Any, method: Method): CompletableFuture<Subscription> {
        val annotation = method.getAnnotation(WampTopic::class.java)
        val currentMethodHandler = MethodEventHandler(instance, method, serializer)
        return session.subscribe(annotation.value, currentMethodHandler)
    }

    fun <TProxy> getCalleeProxy(proxyClass: Class<TProxy>): TProxy {
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(
            proxyClass.classLoader,
            arrayOf(proxyClass),
            CalleeProxyInvocationHandler(session)
        ) as TProxy
    }
}
