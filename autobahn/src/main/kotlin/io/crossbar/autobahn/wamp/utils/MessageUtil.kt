package io.crossbar.autobahn.wamp.utils

import io.crossbar.autobahn.wamp.exceptions.ProtocolError

object MessageUtil {
    /**
     * Validate a raw WAMP message object based on supplied criteria.
     *
     * @param wmsg wamp message to validate
     * @param messageType type of the wamp message
     * @param messageVerboseName verbose name of the message
     * @param lengthMin minimum number of items the wamp message is expected to have
     * @param lengthMax maximum number of items the wamp message is expected to have
     */
    @JvmStatic
    fun validateMessage(
        wmsg: List<Any>,
        messageType: Int,
        messageVerboseName: String,
        lengthMin: Int,
        lengthMax: Int
    ) {
        if (wmsg.isEmpty() || (wmsg[0] !is Int) || wmsg[0] != messageType) {
            throw IllegalArgumentException("Invalid message.")
        }

        if (wmsg.size < lengthMin || wmsg.size > lengthMax) {
            throw ProtocolError("Invalid message length ${wmsg.size} for $messageVerboseName")
        }
    }

    /**
     * Validate a raw WAMP message object based on supplied criteria.
     *
     * @param wmsg wamp message to validate
     * @param messageType type of the wamp message
     * @param messageVerboseName verbose name of the message
     * @param length number of items the wamp message is expected to have
     */
    @JvmStatic
    fun validateMessage(
        wmsg: List<Any>,
        messageType: Int,
        messageVerboseName: String,
        length: Int
    ) {
        validateMessage(wmsg, messageType, messageVerboseName, length, length)
    }

    /**
     * Parse the supplied object as long.
     *
     * @param obj the object to cast
     * @return long value of the object
     */
    @JvmStatic
    fun parseLong(obj: Any?): Long {
        return when (obj) {
            is Int -> obj.toLong()
            is Long -> obj
            else -> 0L
        }
    }
}
