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

package io.crossbar.autobahn.websocket;

import org.junit.Test;

import io.crossbar.autobahn.websocket.exceptions.ParseFailed;

import static org.junit.Assert.*;

public class FrameProtocolTest {

    private final FrameProtocol protocol = new FrameProtocol();

    @Test
    public void testPingFrame() throws ParseFailed {
        byte[] payload = "ping".getBytes();
        byte[] frame = protocol.ping(payload);

        assertNotNull(frame);
        assertTrue(frame.length > 0);

        // Check FIN bit and opcode (9 = ping)
        byte b0 = frame[0];
        assertTrue((b0 & 0x80) != 0);  // FIN bit
        assertEquals(9, b0 & 0x0F);    // Opcode

        // Check MASK bit
        byte b1 = frame[1];
        assertTrue((b1 & 0x80) != 0);  // MASK bit

        // Payload length (masked bit removed)
        int payloadLen = b1 & 0x7F;
        assertEquals(4, payloadLen);
    }

    @Test
    public void testPongFrame() throws ParseFailed {
        byte[] payload = "pong".getBytes();
        byte[] frame = protocol.pong(payload);

        assertNotNull(frame);

        // Check opcode (10 = pong)
        byte b0 = frame[0];
        assertEquals(10, b0 & 0x0F);
    }

    @Test
    public void testPingEmptyPayload() throws ParseFailed {
        byte[] frame = protocol.ping(null);

        assertNotNull(frame);
        // With masking, empty payload = 2 bytes header + 4 bytes mask = 6 bytes
        assertEquals(6, frame.length);
        byte b1 = frame[1];
        assertEquals(0x80, b1 & 0xFF);  // MASK bit only, zero length (masked)
    }

    @Test(expected = ParseFailed.class)
    public void testPingPayloadTooLarge() throws ParseFailed {
        byte[] payload = new byte[126];  // Max is 125
        protocol.ping(payload);
    }

    @Test
    public void testCloseFrame() throws ParseFailed {
        byte[] frame = protocol.close(1000, "normal");

        assertNotNull(frame);

        // Check opcode (8 = close)
        byte b0 = frame[0];
        assertEquals(8, b0 & 0x0F);

        // Check payload includes code and reason
        byte b1 = frame[1];
        int payloadLen = b1 & 0x7F;
        assertTrue(payloadLen >= 2);  // At least 2 bytes for code
    }

    @Test
    public void testCloseNoCode() throws ParseFailed {
        byte[] frame = protocol.close(0, null);

        assertNotNull(frame);
        // With masking: minimum is 2 bytes header + 4 bytes mask = 6 bytes for empty close
        // Some implementations may add extra bytes
        assertTrue("Frame should be at least 6 bytes", frame.length >= 6);

        byte b1 = frame[1];
        assertEquals(0x80, b1 & 0xF0);  // MASK bit set, zero or minimal length
    }

    @Test
    public void testCloseNoReason() throws ParseFailed {
        byte[] frame = protocol.close(1001, null);

        assertNotNull(frame);

        // Check code is encoded correctly
        byte b1 = frame[1];
        int payloadLen = b1 & 0x7F;
        assertEquals(2, payloadLen);  // Just code, no reason
    }

    @Test
    public void testSendTextFrame() {
        byte[] payload = "Hello, WebSocket!".getBytes();
        byte[] frame = protocol.sendText(payload);

        assertNotNull(frame);

        // Check opcode (1 = text)
        byte b0 = frame[0];
        assertEquals(1, b0 & 0x0F);
        assertTrue((b0 & 0x80) != 0);  // FIN bit

        // Check payload length is correct
        byte b1 = frame[1];
        int payloadLen = b1 & 0x7F;

        if (payloadLen == 126) {
            // Extended length (2 bytes)
            int extendedLen = ((frame[2] & 0xFF) << 8) | (frame[3] & 0xFF);
            assertEquals(payload.length, extendedLen);
        } else {
            assertEquals(payload.length, payloadLen);
        }
    }

    @Test
    public void testSendBinaryFrame() {
        byte[] payload = new byte[]{0x01, 0x02, 0x03, 0x04};
        byte[] frame = protocol.sendBinary(payload);

        assertNotNull(frame);

        // Check opcode (2 = binary)
        byte b0 = frame[0];
        assertEquals(2, b0 & 0x0F);
    }

    @Test
    public void testSendLargeTextFrame() {
        // Create payload > 125 bytes but < 65536 (2-byte extended length)
        byte[] payload = new byte[200];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) ('a' + (i % 26));
        }

        byte[] frame = protocol.sendText(payload);

        // Check extended payload length indicator
        byte b1 = frame[1];
        int payloadLen = b1 & 0x7F;
        assertEquals(126, payloadLen);  // Extended 2-byte length

        // Check extended length
        int extendedLen = ((frame[2] & 0xFF) << 8) | (frame[3] & 0xFF);
        assertEquals(payload.length, extendedLen);
    }

    @Test
    public void testMaskingBit() {
        byte[] payload = "test".getBytes();
        byte[] frame = protocol.sendText(payload);

        // MASK bit should be set
        byte b1 = frame[1];
        assertTrue((b1 & 0x80) != 0);
    }

    @Test
    public void testReservedBits() {
        byte[] payload = "test".getBytes();
        byte[] frame = protocol.sendText(payload);

        // RSV bits should be 0
        byte b0 = frame[0];
        assertEquals(0, (b0 & 0x70));
    }
}
