package io.crossbar.autobahn.wamp.reflectionRoles

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class WampTopic(val value: String)
