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

public class WelcomeMessageTest {

    @Test
    public void testWelcomeParse() {
        Map<String, Map<String, Object>> roles = new HashMap<>();
        Map<String, Object> broker = new HashMap<>();
        broker.put("features", new HashMap<>());
        roles.put("broker", broker);

        Map<String, Object> details = new HashMap<>();
        details.put("roles", roles);
        details.put("authid", "user123");
        details.put("authrole", "user");
        details.put("authmethod", "anonymous");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(2);
        wmsg.add(123456789L);
        wmsg.add(details);

        Welcome welcome = Welcome.parse(wmsg);

        assertEquals(123456789L, welcome.session);
        assertEquals("user123", welcome.authid);
        assertEquals("user", welcome.authrole);
        assertEquals("anonymous", welcome.authmethod);
        assertNotNull(welcome.roles);
        assertTrue(welcome.roles.containsKey("broker"));
    }

    @Test
    public void testWelcomeParseMinimal() {
        Map<String, Map<String, Object>> roles = new HashMap<>();
        roles.put("dealer", new HashMap<>());

        Map<String, Object> details = new HashMap<>();
        details.put("roles", roles);

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(2);
        wmsg.add(987654321L);
        wmsg.add(details);

        Welcome welcome = Welcome.parse(wmsg);

        assertEquals(987654321L, welcome.session);
        assertNotNull(welcome.roles);
        assertNull(welcome.authid);
        assertNull(welcome.authrole);
        assertNull(welcome.authmethod);
    }
}
