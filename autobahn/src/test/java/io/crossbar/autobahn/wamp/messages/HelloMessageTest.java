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

public class HelloMessageTest {

    @Test
    public void testHelloMarshalBasic() {
        Map<String, Map> roles = new HashMap<>();
        roles.put("publisher", new HashMap<>());
        roles.put("subscriber", new HashMap<>());

        Hello hello = new Hello("com.example", roles);

        List<Object> marshaled = hello.marshal();

        assertEquals(3, marshaled.size());
        assertEquals(Hello.MESSAGE_TYPE, marshaled.get(0));
        assertEquals("com.example", marshaled.get(1));

        Map<String, Object> details = (Map<String, Object>) marshaled.get(2);
        assertNotNull(details.get("roles"));
        assertNull(details.get("authmethods"));
        assertNull(details.get("authid"));
        assertNull(details.get("authrole"));
        assertNull(details.get("authextra"));
    }

    @Test
    public void testHelloMarshalWithAuth() {
        Map<String, Map> roles = new HashMap<>();
        roles.put("publisher", new HashMap<>());

        List<String> authMethods = new ArrayList<>();
        authMethods.add("anonymous");
        authMethods.add("ticket");

        Map<String, Object> authextra = new HashMap<>();
        authextra.put("nonce", "123456");

        Hello hello = new Hello("com.example.secure", roles, authMethods, "user123", "admin", authextra);

        List<Object> marshaled = hello.marshal();
        Map<String, Object> details = (Map<String, Object>) marshaled.get(2);

        assertEquals(authMethods, details.get("authmethods"));
        assertEquals("user123", details.get("authid"));
        assertEquals("admin", details.get("authrole"));
        assertEquals(authextra, details.get("authextra"));
    }

    @Test
    public void testHelloParse() {
        Map<String, Map<String, Object>> roles = new HashMap<>();
        roles.put("caller", new HashMap<>());
        roles.put("callee", new HashMap<>());

        Map<String, Object> details = new HashMap<>();
        details.put("roles", roles);

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(1);
        wmsg.add("com.example");
        wmsg.add(details);

        Hello hello = Hello.parse(wmsg);

        assertEquals("com.example", hello.realm);
        assertEquals(2, hello.roles.size());
        assertNull(hello.authMethods);
        assertNull(hello.authID);
    }

    @Test
    public void testHelloParseWithAuth() {
        Map<String, Map<String, Object>> roles = new HashMap<>();
        roles.put("caller", new HashMap<>());

        List<String> authMethods = new ArrayList<>();
        authMethods.add("cryptosign");

        Map<String, Object> details = new HashMap<>();
        details.put("roles", roles);
        details.put("authmethods", authMethods);
        details.put("authid", "device001");
        details.put("authrole", "device");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(1);
        wmsg.add("com.example.auth");
        wmsg.add(details);

        Hello hello = Hello.parse(wmsg);

        assertEquals("com.example.auth", hello.realm);
        assertEquals(authMethods, hello.authMethods);
        assertEquals("device001", hello.authID);
        assertEquals("device", hello.authRole);
    }
}
