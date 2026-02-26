package io.crossbar.autobahn.utils

object Platform {
    private var isPlatformChecked = false
    private var isAndroid = false

    /**
     * Checks if code is running on Android.
     *
     * @return boolean representing whether the underlying platform
     *     is Android based
     */
    @JvmStatic
    fun isAndroid(): Boolean {
        if (!isPlatformChecked) {
            isAndroid = System.getProperty("java.vendor") == "The Android Project"
            isPlatformChecked = true
        }
        return isAndroid
    }
}
