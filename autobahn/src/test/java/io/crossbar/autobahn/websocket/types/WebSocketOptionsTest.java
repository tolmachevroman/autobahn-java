///////////////////////////////////////////////////////////////////////////////
//
//   AutobahnJava - http://crossbar.io/autobahn
//
//   Copyright (c) Crossbar.io Technologies GmbH and contributors
//
//   Licensed under the MIT License.
//   http://www.opensource.org/licenses/mit-license.php
//
///////////////////////////////////////////////////////////////////////////////

package io.crossbar.autobahn.websocket.types;

import org.junit.Test;

import static org.junit.Assert.*;

public class WebSocketOptionsTest {

    @Test
    public void testDefaultValues() {
        WebSocketOptions options = new WebSocketOptions();

        assertEquals(128 * 1024, options.getMaxFramePayloadSize());
        assertEquals(128 * 1024, options.getMaxMessagePayloadSize());
        assertFalse(options.getReceiveTextMessagesRaw());
        assertTrue(options.getTcpNoDelay());
        assertEquals(0, options.getSocketReceiveTimeout());
        assertEquals(6000, options.getSocketConnectTimeout());
        assertTrue(options.getValidateIncomingUtf8());
        assertTrue(options.getMaskClientFrames());
        assertEquals(0, options.getReconnectInterval());
        assertNull(options.getTLSEnabledProtocols());
        assertEquals(10, options.getAutoPingInterval());
        assertEquals(5, options.getAutoPingTimeout());
    }

    @Test
    public void testSetMaxFramePayloadSize() {
        WebSocketOptions options = new WebSocketOptions();
        options.setMaxFramePayloadSize(256 * 1024);

        assertEquals(256 * 1024, options.getMaxFramePayloadSize());
    }

    @Test
    public void testSetMaxMessagePayloadSize() {
        WebSocketOptions options = new WebSocketOptions();
        options.setMaxMessagePayloadSize(512 * 1024);

        assertEquals(512 * 1024, options.getMaxMessagePayloadSize());
    }

    @Test
    public void testPayloadSizeSynchronization() {
        WebSocketOptions options = new WebSocketOptions();
        
        // Setting frame size larger should also set message size
        options.setMaxFramePayloadSize(256 * 1024);
        assertEquals(256 * 1024, options.getMaxMessagePayloadSize());
        
        // Reset for next test
        options = new WebSocketOptions();
        
        // Setting message size smaller should also set frame size
        options.setMaxMessagePayloadSize(64 * 1024);
        assertEquals(64 * 1024, options.getMaxFramePayloadSize());
    }

    @Test
    public void testSetReceiveTextMessagesRaw() {
        WebSocketOptions options = new WebSocketOptions();
        options.setReceiveTextMessagesRaw(true);

        assertTrue(options.getReceiveTextMessagesRaw());
    }

    @Test
    public void testSetTcpNoDelay() {
        WebSocketOptions options = new WebSocketOptions();
        options.setTcpNoDelay(false);

        assertFalse(options.getTcpNoDelay());
    }

    @Test
    public void testSetSocketTimeouts() {
        WebSocketOptions options = new WebSocketOptions();
        
        options.setSocketReceiveTimeout(5000);
        assertEquals(5000, options.getSocketReceiveTimeout());
        
        options.setSocketConnectTimeout(10000);
        assertEquals(10000, options.getSocketConnectTimeout());
    }

    @Test
    public void testSetValidateIncomingUtf8() {
        WebSocketOptions options = new WebSocketOptions();
        options.setValidateIncomingUtf8(false);

        assertFalse(options.getValidateIncomingUtf8());
    }

    @Test
    public void testSetMaskClientFrames() {
        WebSocketOptions options = new WebSocketOptions();
        options.setMaskClientFrames(false);

        assertFalse(options.getMaskClientFrames());
    }

    @Test
    public void testSetReconnectInterval() {
        WebSocketOptions options = new WebSocketOptions();
        options.setReconnectInterval(5000);

        assertEquals(5000, options.getReconnectInterval());
    }

    @Test
    public void testSetTLSEnabledProtocols() {
        WebSocketOptions options = new WebSocketOptions();
        String[] protocols = {"TLSv1.2", "TLSv1.3"};
        options.setTLSEnabledProtocols(protocols);

        assertArrayEquals(protocols, options.getTLSEnabledProtocols());
    }

    @Test
    public void testSetAutoPingInterval() {
        WebSocketOptions options = new WebSocketOptions();
        options.setAutoPingInterval(30);

        assertEquals(30, options.getAutoPingInterval());
    }

    @Test
    public void testSetAutoPingTimeout() {
        WebSocketOptions options = new WebSocketOptions();
        options.setAutoPingTimeout(15);

        assertEquals(15, options.getAutoPingTimeout());
    }

    @Test
    public void testCopyConstructor() {
        WebSocketOptions original = new WebSocketOptions();
        original.setMaxFramePayloadSize(256 * 1024);
        original.setReconnectInterval(3000);
        original.setValidateIncomingUtf8(false);

        WebSocketOptions copy = new WebSocketOptions(original);

        assertEquals(original.getMaxFramePayloadSize(), copy.getMaxFramePayloadSize());
        assertEquals(original.getReconnectInterval(), copy.getReconnectInterval());
        assertEquals(original.getValidateIncomingUtf8(), copy.getValidateIncomingUtf8());
        
        // Ensure values are actually copied, not shared
        original.setReconnectInterval(1000);
        assertEquals(3000, copy.getReconnectInterval());
    }

    @Test
    public void testInvalidPayloadSizeIgnored() {
        WebSocketOptions options = new WebSocketOptions();
        int originalSize = options.getMaxFramePayloadSize();
        
        // Should be ignored
        options.setMaxFramePayloadSize(-1);
        assertEquals(originalSize, options.getMaxFramePayloadSize());
        
        // Should be ignored
        options.setMaxFramePayloadSize(0);
        assertEquals(originalSize, options.getMaxFramePayloadSize());
    }

    @Test
    public void testInvalidTimeoutIgnored() {
        WebSocketOptions options = new WebSocketOptions();
        int originalTimeout = options.getSocketReceiveTimeout();
        
        // Negative should be ignored
        options.setSocketReceiveTimeout(-1);
        assertEquals(originalTimeout, options.getSocketReceiveTimeout());
    }
}
