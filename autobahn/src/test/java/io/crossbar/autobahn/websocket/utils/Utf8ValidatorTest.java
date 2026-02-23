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

package io.crossbar.autobahn.websocket.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class Utf8ValidatorTest {

    @Test
    public void testValidSingleByte() {
        Utf8Validator validator = new Utf8Validator();
        
        assertTrue(validator.validate(new byte[]{0x00}));  // NUL
        assertTrue(validator.isValid());
        
        validator = new Utf8Validator();
        assertTrue(validator.validate(new byte[]{0x41}));  // 'A'
        assertTrue(validator.isValid());
        
        validator = new Utf8Validator();
        assertTrue(validator.validate(new byte[]{0x7F}));  // DEL
        assertTrue(validator.isValid());
    }

    @Test
    public void testValidTwoByteSequence() {
        Utf8Validator validator = new Utf8Validator();
        
        // U+00A2 (¢) = 0xC2 0xA2
        assertTrue(validator.validate(new byte[]{(byte) 0xC2, (byte) 0xA2}));
        assertTrue(validator.isValid());
    }

    @Test
    public void testValidThreeByteSequence() {
        Utf8Validator validator = new Utf8Validator();
        
        // U+20AC (€) = 0xE2 0x82 0xAC
        assertTrue(validator.validate(new byte[]{(byte) 0xE2, (byte) 0x82, (byte) 0xAC}));
        assertTrue(validator.isValid());
    }

    @Test
    public void testValidFourByteSequence() {
        Utf8Validator validator = new Utf8Validator();
        
        // U+1F600 (😀) = 0xF0 0x9F 0x98 0x80
        assertTrue(validator.validate(new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x80}));
        assertTrue(validator.isValid());
    }

    @Test
    public void testInvalidContinuationByte() {
        Utf8Validator validator = new Utf8Validator();
        
        // Standalone continuation byte is invalid
        assertFalse(validator.validate(new byte[]{(byte) 0x80}));
    }

    @Test
    public void testInvalidStartByte() {
        Utf8Validator validator = new Utf8Validator();
        
        // 0xFE and 0xFF are never valid in UTF-8
        assertFalse(validator.validate(new byte[]{(byte) 0xFE}));
        
        validator = new Utf8Validator();  // Reset
        assertFalse(validator.validate(new byte[]{(byte) 0xFF}));
    }

    @Test
    public void testIncompleteTwoByteSequence() {
        Utf8Validator validator = new Utf8Validator();
        
        // Start 2-byte sequence but don't complete - returns true (not rejected)
        assertTrue(validator.validate(new byte[]{(byte) 0xC2}));
        // But isValid() should return false since sequence is incomplete
        assertFalse(validator.isValid());
    }

    @Test
    public void testReset() {
        Utf8Validator validator = new Utf8Validator();
        
        // Start a sequence
        validator.validate(new byte[]{(byte) 0xE2});
        assertFalse(validator.isValid());  // Incomplete
        
        // Reset
        validator.reset();
        assertTrue(validator.isValid());  // Back to valid/initial state
    }

    @Test
    public void testValidAsciiString() {
        Utf8Validator validator = new Utf8Validator();
        String text = "Hello, World!";
        
        assertTrue(validator.validate(text.getBytes()));
        assertTrue(validator.isValid());
    }

    @Test
    public void testMaxCodepoint() {
        Utf8Validator validator = new Utf8Validator();
        
        // U+10FFFF (max valid Unicode) = 0xF4 0x8F 0xBF 0xBF
        assertTrue(validator.validate(new byte[]{(byte) 0xF4, (byte) 0x8F, (byte) 0xBF, (byte) 0xBF}));
        assertTrue(validator.isValid());
    }

    @Test
    public void testPositionTracking() {
        Utf8Validator validator = new Utf8Validator();
        
        byte[] data = new byte[]{(byte) 0xC2, (byte) 0xA2, (byte) 0x41};  // ¢ followed by 'A'
        assertTrue(validator.validate(data));
        assertEquals(3, validator.position());
    }

    @Test
    public void testPositionOnError() {
        Utf8Validator validator = new Utf8Validator();
        
        // Invalid: standalone continuation byte at position 1
        byte[] data = new byte[]{0x41, (byte) 0x80};  // 'A' followed by invalid
        assertFalse(validator.validate(data));
        assertEquals(1, validator.position());
    }

    @Test
    public void testChunkedValidation() {
        Utf8Validator validator = new Utf8Validator();
        
        // Validate in chunks
        assertTrue(validator.validate(new byte[]{(byte) 0xE2}, 0, 1));  // First byte of €
        assertFalse(validator.isValid());  // Incomplete
        
        assertTrue(validator.validate(new byte[]{(byte) 0x82}, 0, 1));  // Second byte
        assertFalse(validator.isValid());  // Still incomplete
        
        assertTrue(validator.validate(new byte[]{(byte) 0xAC}, 0, 1));  // Third byte
        assertTrue(validator.isValid());  // Now complete
    }
}
