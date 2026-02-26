package io.crossbar.autobahn.wamp.utils

import io.crossbar.autobahn.utils.Platform
import io.crossbar.autobahn.wamp.interfaces.ITransport
import java8.util.concurrent.ForkJoinPool
import java.util.concurrent.Executor

object Platform {
    /**
     * Checks if the underlying platform is Android and if the
     * API level is greater than equal to the requested value.
     *
     * Returns 0 if the platform is not Android.
     */
    @JvmStatic
    fun getAndroidAPIVersion(): Int {
        return if (Platform.isAndroid()) {
            try {
                val klass = Class.forName("android.os.Build\$VERSION")
                klass.getField("SDK_INT").getInt(null)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        } else {
            0
        }
    }

    /**
     * Automatically returns a WebSocket based transport for WAMP based on the
     * underlying platform.
     *
     * @param webSocketURL websocket url to use for initializing of the transport
     * @return an instance of ITransport suitable for the underlying platform.
     * @throws RuntimeException most probably if the path of transport we are trying
     *     to initialize changed OR its constructor changed.
     */
    @JvmStatic
    fun autoSelectTransport(webSocketURL: String): ITransport {
        return try {
            val transportClass = if (Platform.isAndroid()) {
                Class.forName("io.crossbar.autobahn.wamp.transports.WebSocket")
            } else {
                Class.forName("io.crossbar.autobahn.wamp.transports.NettyWebSocket")
            }
            transportClass.getConstructor(String::class.java).newInstance(webSocketURL) as ITransport
        } catch (e: Exception) {
            throw RuntimeException(e.message, e)
        }
    }

    /**
     * Auto selects the Executor based on the underlying platform.
     * On Android we want autobahn to call the user facing code's
     * callbacks on the main thread so that apps are able to update the UI.
     *
     * @return Executor instance suitable for current platform
     */
    @JvmStatic
    fun autoSelectExecutor(): Executor {
        return if (Platform.isAndroid()) {
            CurrentThreadExecutor()
        } else {
            ForkJoinPool.commonPool()
        }
    }
}
