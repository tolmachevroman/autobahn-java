package io.crossbar.autobahn.wamp.utils;

import io.crossbar.autobahn.wamp.exceptions.ProtocolError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

public class MessageUtilTest {

    @Test
    public void testParseLongFromInteger() {
        long result = MessageUtil.parseLong(42);
        assertEquals(42L, result);
    }

    @Test
    public void testParseLongFromLong() {
        long result = MessageUtil.parseLong(123456789L);
        assertEquals(123456789L, result);
    }

    @Test
    public void testValidateMessageValidLength() {
        List<Object> message = Arrays.asList(1, "arg1", "arg2");
        
        // Should not throw exception
        MessageUtil.validateMessage(message, 1, "TEST", 3);
    }

    @Test
    public void testValidateMessageInvalidType() {
        List<Object> message = Arrays.asList(2, "arg1", "arg2");
        
        // Wrong message type throws IllegalArgumentException
        assertThatThrownBy(() -> 
            MessageUtil.validateMessage(message, 1, "TEST", 3)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Invalid message");
    }

    @Test
    public void testValidateMessageInvalidLength() {
        List<Object> message = Arrays.asList(1, "arg1");
        
        // Wrong length throws ProtocolError
        assertThatThrownBy(() ->
            MessageUtil.validateMessage(message, 1, "TEST", 3)
        ).isInstanceOf(ProtocolError.class)
         .hasMessageContaining("Invalid message length");
    }

    @Test
    public void testValidateMessageNullList() {
        // Null list throws NullPointerException
        assertThatThrownBy(() ->
            MessageUtil.validateMessage(null, 1, "TEST", 3)
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testValidateMessageEmptyList() {
        List<Object> message = new ArrayList<>();
        
        assertThatThrownBy(() ->
            MessageUtil.validateMessage(message, 1, "TEST", 3)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testValidateMessageWithMinMaxLength() {
        List<Object> message = Arrays.asList(16, new Object(), "topic", "arg1");
        
        // Should not throw - length 4 is between 3 and 6
        MessageUtil.validateMessage(message, 16, "PUBLISH", 3, 6);
    }

    @Test
    public void testValidateMessageWithExactMinLength() {
        List<Object> message = Arrays.asList(16, new Object(), "topic");
        
        // Should not throw - exactly min length
        MessageUtil.validateMessage(message, 16, "PUBLISH", 3, 6);
    }

    @Test
    public void testParseLongFromZero() {
        long result = MessageUtil.parseLong(0);
        assertEquals(0L, result);
    }

    @Test
    public void testParseLongFromNegative() {
        long result = MessageUtil.parseLong(-100);
        assertEquals(-100L, result);
    }
}
