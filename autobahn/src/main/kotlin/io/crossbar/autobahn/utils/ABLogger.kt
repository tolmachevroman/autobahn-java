package io.crossbar.autobahn.utils

/**
 * Logger factory that provides platform-appropriate logger implementations.
 */
object ABLogger {
    @JvmStatic
    fun getLogger(tag: String): IABLogger {
        return try {
            val loggerClass = if (Platform.isAndroid()) {
                Class.forName("io.crossbar.autobahn.utils.ABALogger")
            } else {
                Class.forName("io.crossbar.autobahn.utils.ABJLogger")
            }
            loggerClass.getConstructor(String::class.java).newInstance(tag) as IABLogger
        } catch (e: Exception) {
            throw RuntimeException(e.message, e)
        }
    }
}
