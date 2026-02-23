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

package io.crossbar.autobahn.wamp.messages;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.crossbar.autobahn.wamp.exceptions.ProtocolError;

import static org.junit.Assert.*;

public class ErrorMessageTest {

    @Test
    public void testErrorMarshalBasic() {
        // Error for a CALL message (request type 48)
        Error error = new Error(48, 12345L, "wamp.error.no_such_procedure", null, null);

        List<Object> marshaled = error.marshal();

        assertEquals(5, marshaled.size());
        assertEquals(Error.MESSAGE_TYPE, marshaled.get(0));
        assertEquals(48, marshaled.get(1));  // Request type (CALL)
        assertEquals(12345L, marshaled.get(2));  // Request ID
        assertEquals("wamp.error.no_such_procedure", marshaled.get(4));

        Map<String, Object> details = (Map<String, Object>) marshaled.get(3);
        assertTrue(details.isEmpty());
    }

    @Test
    public void testErrorMarshalWithArgs() {
        List<Object> args = new ArrayList<>();
        args.add("Error detail 1");
        args.add(42);

        Error error = new Error(48, 12345L, "wamp.error.invalid_argument", args, null);

        List<Object> marshaled = error.marshal();

        assertEquals(6, marshaled.size());
        assertEquals(args, marshaled.get(5));
    }

    @Test
    public void testErrorMarshalWithKwargs() {
        List<Object> args = new ArrayList<>();
        args.add("validation failed");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("field", "username");
        kwargs.put("reason", "too short");

        Error error = new Error(48, 12345L, "wamp.error.invalid_argument", args, kwargs);

        List<Object> marshaled = error.marshal();

        assertEquals(7, marshaled.size());
        assertEquals(args, marshaled.get(5));
        assertEquals(kwargs, marshaled.get(6));
    }

    @Test
    public void testErrorParseBasic() {
        List<Object> wmsg = new ArrayList<>();
        wmsg.add(8);  // ERROR message type
        wmsg.add(48);  // Request type (CALL)
        wmsg.add(12345L);  // Request ID
        wmsg.add(new HashMap<String, Object>());  // Details
        wmsg.add("wamp.error.no_such_procedure");  // Error URI

        Error error = Error.parse(wmsg);

        assertEquals(48, error.requestType);
        assertEquals(12345L, error.request);
        assertEquals("wamp.error.no_such_procedure", error.error);
        assertNull(error.args);
        assertNull(error.kwargs);
    }

    @Test
    public void testErrorParseWithArgs() {
        List<Object> args = new ArrayList<>();
        args.add("Argument validation failed");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(8);
        wmsg.add(48);
        wmsg.add(12345L);
        wmsg.add(new HashMap<String, Object>());
        wmsg.add("wamp.error.invalid_argument");
        wmsg.add(args);

        Error error = Error.parse(wmsg);

        assertEquals("wamp.error.invalid_argument", error.error);
        assertNotNull(error.args);
        assertEquals(1, error.args.size());
        assertEquals("Argument validation failed", error.args.get(0));
    }

    @Test
    public void testErrorParseWithKwargs() {
        List<Object> args = new ArrayList<>();
        args.add("validation failed");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("field", "email");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(8);
        wmsg.add(48);
        wmsg.add(12345L);
        wmsg.add(new HashMap<String, Object>());
        wmsg.add("wamp.error.invalid_argument");
        wmsg.add(args);
        wmsg.add(kwargs);

        Error error = Error.parse(wmsg);

        assertNotNull(error.kwargs);
        assertEquals("email", error.kwargs.get("field"));
    }

    @Test
    public void testErrorParseDifferentRequestTypes() {
        // Error for SUBSCRIBE (32)
        List<Object> wmsg = new ArrayList<>();
        wmsg.add(8);
        wmsg.add(32);  // SUBSCRIBE
        wmsg.add(1001L);
        wmsg.add(new HashMap<String, Object>());
        wmsg.add("wamp.error.no_such_topic");

        Error error = Error.parse(wmsg);
        assertEquals(32, error.requestType);

        // Error for REGISTER (64)
        wmsg.set(1, 64);  // REGISTER
        wmsg.set(4, "wamp.error.procedure_already_exists");

        error = Error.parse(wmsg);
        assertEquals(64, error.requestType);
        assertEquals("wamp.error.procedure_already_exists", error.error);
    }

    @Test(expected = ProtocolError.class)
    public void testErrorParseBinaryPayloadThrows() {
        List<Object> wmsg = new ArrayList<>();
        wmsg.add(8);
        wmsg.add(48);
        wmsg.add(12345L);
        wmsg.add(new HashMap<String, Object>());
        wmsg.add("wamp.error.invalid_argument");
        wmsg.add(new byte[]{0x01, 0x02});  // Binary payload - not allowed

        Error.parse(wmsg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorParseInvalidMessageType() {
        List<Object> wmsg = new ArrayList<>();
        wmsg.add(99);  // Wrong message type
        wmsg.add(48);
        wmsg.add(12345L);
        wmsg.add(new HashMap<String, Object>());
        wmsg.add("wamp.error.invalid");

        Error.parse(wmsg);
    }

    @Test(expected = ProtocolError.class)
    public void testErrorParseTooShort() {
        List<Object> wmsg = new ArrayList<>();
        wmsg.add(8);
        wmsg.add(48);
        wmsg.add(12345L);
        wmsg.add(new HashMap<String, Object>());
        // Missing error URI

        Error.parse(wmsg);
    }

    @Test
    public void testStandardErrorURIs() {
        String[] standardErrors = {
            "wamp.error.no_such_procedure",
            "wamp.error.procedure_already_exists",
            "wamp.error.no_such_registration",
            "wamp.error.no_such_subscription",
            "wamp.error.no_such_topic",
            "wamp.error.invalid_argument",
            "wamp.error.not_authorized",
            "wamp.error.callee_unreachable",
            "wamp.error.timeout"
        };

        for (String errorUri : standardErrors) {
            Error error = new Error(48, 12345L, errorUri, null, null);
            assertEquals(errorUri, error.error);
        }
    }
}
