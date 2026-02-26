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

public class GoodbyeMessageTest {

    @Test
    public void testGoodbyeMarshalBasic() {
        Goodbye goodbye = new Goodbye("wamp.close.normal", null);

        List<Object> marshaled = goodbye.marshal();

        assertEquals(3, marshaled.size());
        assertEquals(6, marshaled.get(0));  // GOODBYE message type

        Map<?, ?> details = (Map<?, ?>) marshaled.get(1);
        assertTrue(details.isEmpty());  // No message

        assertEquals("wamp.close.normal", marshaled.get(2));
    }

    @Test
    public void testGoodbyeMarshalWithMessage() {
        Goodbye goodbye = new Goodbye("wamp.close.normal", "Session closed normally");

        List<Object> marshaled = goodbye.marshal();

        Map<?, ?> details = (Map<?, ?>) marshaled.get(1);
        assertEquals("Session closed normally", details.get("message"));

        assertEquals("wamp.close.normal", marshaled.get(2));
    }

    @Test
    public void testGoodbyeParseBasic() {
        Map<String, Object> details = new HashMap<>();

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(6);  // GOODBYE
        wmsg.add(details);
        wmsg.add("wamp.close.normal");

        Goodbye goodbye = Goodbye.parse(wmsg);

        assertEquals("wamp.close.normal", goodbye.reason);
        assertNull(goodbye.message);
    }

    @Test
    public void testGoodbyeParseWithMessage() {
        Map<String, Object> details = new HashMap<>();
        details.put("message", "Going away");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(6);
        wmsg.add(details);
        wmsg.add("wamp.close.goodbye_and_out");

        Goodbye goodbye = Goodbye.parse(wmsg);

        assertEquals("wamp.close.goodbye_and_out", goodbye.reason);
        assertEquals("Going away", goodbye.message);
    }

    @Test
    public void testGoodbyeStandardReasons() {
        String[] standardReasons = {
            "wamp.close.normal",
            "wamp.close.goodbye_and_out",
            "wamp.close.system_shutdown",
            "wamp.close.close_realm",
            "wamp.close.killed",
            "wamp.close.realm_closing",
            "wamp.close.error"
        };

        for (String reason : standardReasons) {
            Goodbye goodbye = new Goodbye(reason, null);
            assertEquals(reason, goodbye.reason);

            List<Object> marshaled = goodbye.marshal();
            assertEquals(reason, marshaled.get(2));
        }
    }

    @Test
    public void testGoodbyeRoundtrip() {
        Goodbye original = new Goodbye("wamp.close.goodbye_and_out", "See you later");
        List<Object> marshaled = original.marshal();
        Goodbye reparsed = Goodbye.parse(marshaled);
        List<Object> remarshaled = reparsed.marshal();

        assertEquals(original.reason, reparsed.reason);
        assertEquals(original.message, reparsed.message);
    }

    @Test
    public void testGoodbyeFromClient() {
        // Client-initiated goodbye
        Goodbye goodbye = new Goodbye("wamp.close.normal", "Client closing");

        List<Object> marshaled = goodbye.marshal();

        assertEquals(6, marshaled.get(0));
        assertEquals("wamp.close.normal", marshaled.get(2));
    }

    @Test
    public void testGoodbyeFromRouter() {
        // Router-initiated goodbye
        Goodbye goodbye = new Goodbye("wamp.close.system_shutdown", "Router shutting down");

        List<Object> marshaled = goodbye.marshal();

        assertEquals("wamp.close.system_shutdown", marshaled.get(2));
        Map<?, ?> details = (Map<?, ?>) marshaled.get(1);
        assertEquals("Router shutting down", details.get("message"));
    }
}
