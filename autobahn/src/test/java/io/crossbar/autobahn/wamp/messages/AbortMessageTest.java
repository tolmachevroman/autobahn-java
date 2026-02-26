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

import static org.junit.Assert.*;

public class AbortMessageTest {

    @Test
    public void testAbortMarshalBasic() {
        Abort abort = new Abort("wamp.error.no_such_realm", null);

        List<Object> marshaled = abort.marshal();

        assertEquals(3, marshaled.size());
        assertEquals(3, marshaled.get(0));  // ABORT message type

        Map<?, ?> details = (Map<?, ?>) marshaled.get(1);
        assertTrue(details.isEmpty());  // No message

        assertEquals("wamp.error.no_such_realm", marshaled.get(2));
    }

    @Test
    public void testAbortMarshalWithMessage() {
        Abort abort = new Abort("wamp.error.no_such_realm", "Realm does not exist");

        List<Object> marshaled = abort.marshal();

        Map<?, ?> details = (Map<?, ?>) marshaled.get(1);
        assertEquals("Realm does not exist", details.get("message"));

        assertEquals("wamp.error.no_such_realm", marshaled.get(2));
    }

    @Test
    public void testAbortParseBasic() {
        Map<String, Object> details = new HashMap<>();

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(3);  // ABORT
        wmsg.add(details);
        wmsg.add("wamp.error.no_such_realm");

        Abort abort = Abort.parse(wmsg);

        assertEquals("wamp.error.no_such_realm", abort.reason);
        assertNull(abort.message);
    }

    @Test
    public void testAbortParseWithMessage() {
        Map<String, Object> details = new HashMap<>();
        details.put("message", "Authentication failed");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(3);
        wmsg.add(details);
        wmsg.add("wamp.error.not_authorized");

        Abort abort = Abort.parse(wmsg);

        assertEquals("wamp.error.not_authorized", abort.reason);
        // getOrDefault returns Object, but message field is String?
        // It should still work since we put a String
        assertNotNull(abort.message);
        assertTrue(abort.message.contains("Authentication"));
    }

    @Test
    public void testAbortStandardErrors() {
        String[] standardErrors = {
            "wamp.error.no_such_realm",
            "wamp.error.not_authorized",
            "wamp.error.authorization_failed",
            "wamp.error.no_such_role",
            "wamp.error.protocol_violation"
        };

        for (String error : standardErrors) {
            Abort abort = new Abort(error, null);
            assertEquals(error, abort.reason);

            List<Object> marshaled = abort.marshal();
            assertEquals(error, marshaled.get(2));
        }
    }

    @Test
    public void testAbortRoundtrip() {
        Abort original = new Abort("wamp.error.not_authorized", "Invalid credentials");
        List<Object> marshaled = original.marshal();
        Abort reparsed = Abort.parse(marshaled);
        List<Object> remarshaled = reparsed.marshal();

        assertEquals(original.reason, reparsed.reason);
        assertEquals(original.message, reparsed.message);
    }

    @Test
    public void testAbortDuringHandshake() {
        // Abort during HELLO/CHALLENGE flow
        Abort abort = new Abort("wamp.error.not_authorized", "Invalid signature");

        List<Object> marshaled = abort.marshal();

        assertEquals(3, marshaled.get(0));
        assertEquals("wamp.error.not_authorized", marshaled.get(2));
    }

    @Test
    public void testAbortWithLongMessage() {
        String longMessage = "This is a detailed error message explaining exactly what went wrong during the session establishment process.";
        Abort abort = new Abort("wamp.error.protocol_violation", longMessage);

        List<Object> marshaled = abort.marshal();
        Abort reparsed = Abort.parse(marshaled);

        assertEquals(longMessage, reparsed.message);
    }
}
