package io.crossbar.autobahn.wamp.types

/**
 * Exit information for session close.
 *
 * @property exitCode The exit code (0 for clean exit)
 * @property reason The reason for exit (may be null)
 * @property code Compatibility alias for exitCode (deprecated, use exitCode)
 */
class ExitInfo(
    @JvmField val exitCode: Int,
    @JvmField val reason: String?
) {
    /**
     * Compatibility field for code access from Java.
     * @deprecated Use exitCode instead
     */
    @Deprecated("Use exitCode instead")
    @JvmField
    val code: Int = exitCode

    /**
     * Constructor for clean exit.
     * @param wasClean true if the exit was clean
     */
    @JvmOverloads
    constructor(wasClean: Boolean) : this(
        if (wasClean) 0 else 1,
        null
    )
}
