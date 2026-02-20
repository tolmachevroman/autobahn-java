package io.crossbar.autobahn.websocket.types

/**
 * WebSockets connection options. This can be supplied to WebSocketConnection in connect().
 * Note that the latter copies the options provided to connect(), so any change after
 * connect will have no effect.
 */
class WebSocketOptions {
    private var maxFramePayloadSize: Int = 128 * 1024
    private var maxMessagePayloadSize: Int = 128 * 1024
    private var receiveTextMessagesRaw: Boolean = false
    private var tcpNoDelay: Boolean = true
    private var socketReceiveTimeout: Int = 0
    private var socketConnectTimeout: Int = 6000
    private var validateIncomingUtf8: Boolean = true
    private var maskClientFrames: Boolean = true
    private var reconnectInterval: Int = 0  // no reconnection by default
    private var tlsProtocols: Array<String>? = null
    private var autoPingInterval: Int = 10
    private var autoPingTimeout: Int = 5

    /**
     * Construct default options.
     */
    constructor()

    /**
     * Construct options as copy from other options object.
     *
     * @param other Options to copy.
     */
    constructor(other: WebSocketOptions) {
        maxFramePayloadSize = other.maxFramePayloadSize
        maxMessagePayloadSize = other.maxMessagePayloadSize
        receiveTextMessagesRaw = other.receiveTextMessagesRaw
        tcpNoDelay = other.tcpNoDelay
        socketReceiveTimeout = other.socketReceiveTimeout
        socketConnectTimeout = other.socketConnectTimeout
        validateIncomingUtf8 = other.validateIncomingUtf8
        maskClientFrames = other.maskClientFrames
        reconnectInterval = other.reconnectInterval
        tlsProtocols = other.tlsProtocols
        autoPingInterval = other.autoPingInterval
        autoPingTimeout = other.autoPingTimeout
    }

    fun setReceiveTextMessagesRaw(enabled: Boolean) {
        receiveTextMessagesRaw = enabled
    }

    fun getReceiveTextMessagesRaw(): Boolean = receiveTextMessagesRaw

    fun setMaxFramePayloadSize(size: Int) {
        if (size > 0) {
            maxFramePayloadSize = size
            if (maxMessagePayloadSize < maxFramePayloadSize) {
                maxMessagePayloadSize = maxFramePayloadSize
            }
        }
    }

    fun getMaxFramePayloadSize(): Int = maxFramePayloadSize

    fun setMaxMessagePayloadSize(size: Int) {
        if (size > 0) {
            maxMessagePayloadSize = size
            if (maxMessagePayloadSize < maxFramePayloadSize) {
                maxFramePayloadSize = maxMessagePayloadSize
            }
        }
    }

    fun getMaxMessagePayloadSize(): Int = maxMessagePayloadSize

    fun setTcpNoDelay(enabled: Boolean) {
        tcpNoDelay = enabled
    }

    fun getTcpNoDelay(): Boolean = tcpNoDelay

    fun setSocketReceiveTimeout(timeoutMs: Int) {
        if (timeoutMs >= 0) {
            socketReceiveTimeout = timeoutMs
        }
    }

    fun getSocketReceiveTimeout(): Int = socketReceiveTimeout

    fun setSocketConnectTimeout(timeoutMs: Int) {
        if (timeoutMs >= 0) {
            socketConnectTimeout = timeoutMs
        }
    }

    fun getSocketConnectTimeout(): Int = socketConnectTimeout

    fun setValidateIncomingUtf8(enabled: Boolean) {
        validateIncomingUtf8 = enabled
    }

    fun getValidateIncomingUtf8(): Boolean = validateIncomingUtf8

    fun setMaskClientFrames(enabled: Boolean) {
        maskClientFrames = enabled
    }

    fun getMaskClientFrames(): Boolean = maskClientFrames

    fun setReconnectInterval(reconnectInterval: Int) {
        this.reconnectInterval = reconnectInterval
    }

    fun getReconnectInterval(): Int = reconnectInterval

    fun getTLSEnabledProtocols(): Array<String>? = tlsProtocols

    fun setTLSEnabledProtocols(protocols: Array<String>?) {
        this.tlsProtocols = protocols
    }

    fun setAutoPingInterval(seconds: Int) {
        autoPingInterval = seconds
    }

    fun getAutoPingInterval(): Int = autoPingInterval

    fun setAutoPingTimeout(seconds: Int) {
        autoPingTimeout = seconds
    }

    fun getAutoPingTimeout(): Int = autoPingTimeout
}
