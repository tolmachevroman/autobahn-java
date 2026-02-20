package io.crossbar.autobahn.wamp.reflectionRoles

import io.crossbar.autobahn.wamp.Session
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.ExecutionException
import java8.util.concurrent.CompletableFuture

class CalleeProxyInvocationHandler(private val session: Session) : InvocationHandler {

    override fun invoke(proxy: Any?, method: Method, args: Array<Any>?): Any? {
        return if (method.returnType == CompletableFuture::class.java) {
            handleAsync(method, args)
        } else {
            handleSync(method, args)
        }
    }

    fun handleSync(method: Method, args: Array<Any>?): Any? {
        return try {
            val task = innerHandleAsync(method, args, method.returnType)
            task.get()
        } catch (ex: ExecutionException) {
            val cause = ex.cause
            if (cause is RuntimeException) {
                throw cause
            } else {
                throw RuntimeException(cause?.message, cause)
            }
        } catch (ex: RuntimeException) {
            throw ex
        } catch (ex: Exception) {
            throw RuntimeException(ex.message, ex)
        }
    }

    fun handleAsync(method: Method, args: Array<Any>?): Any? {
        val taskType = method.genericReturnType
        val returnType = getTaskGenericParameterType(taskType)
        return innerHandleAsync(method, args, returnType)
    }

    private fun getTaskGenericParameterType(taskType: Type): Type {
        return (taskType as ParameterizedType).actualTypeArguments[0]
    }

    private fun innerHandleAsync(method: Method, args: Array<Any>?, returnType: Type?): CompletableFuture<*> {
        val annotation = method.getAnnotation(WampProcedure::class.java)
        val procedureUri = annotation.value
        val callArguments = args?.toList()
        return session.call(procedureUri, callArguments, ReflectionTypeReference(returnType!!))
    }
}
