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

public class InvocationMessageTest {

    @Test
    public void testInvocationParseBasic() {
        Map<String, Object> details = new HashMap<>();

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(68);  // INVOCATION
        wmsg.add(2001L);  // request ID
        wmsg.add(3001L);  // registration ID
        wmsg.add(details);

        Invocation invocation = Invocation.parse(wmsg);

        assertEquals(2001L, invocation.request);
        assertEquals(3001L, invocation.registration);
        assertNotNull(invocation.details);
        assertTrue(invocation.details.isEmpty());
        assertNull(invocation.args);
        assertNull(invocation.kwargs);
    }

    @Test
    public void testInvocationParseWithArgs() {
        Map<String, Object> details = new HashMap<>();

        List<Object> args = new ArrayList<>();
        args.add("arg1");
        args.add(42);
        args.add(true);

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(68);
        wmsg.add(2001L);
        wmsg.add(3001L);
        wmsg.add(details);
        wmsg.add(args);

        Invocation invocation = Invocation.parse(wmsg);

        assertNotNull(invocation.args);
        assertEquals(3, invocation.args.size());
        assertEquals("arg1", invocation.args.get(0));
        assertEquals(42, invocation.args.get(1));
        assertEquals(true, invocation.args.get(2));
    }

    @Test
    public void testInvocationParseWithKwargs() {
        Map<String, Object> details = new HashMap<>();

        List<Object> args = new ArrayList<>();
        args.add("data");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("param1", "value1");
        kwargs.put("param2", 100);

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(68);
        wmsg.add(2001L);
        wmsg.add(3001L);
        wmsg.add(details);
        wmsg.add(args);
        wmsg.add(kwargs);

        Invocation invocation = Invocation.parse(wmsg);

        assertNotNull(invocation.kwargs);
        assertEquals("value1", invocation.kwargs.get("param1"));
        assertEquals(100, invocation.kwargs.get("param2"));
    }

    @Test
    public void testInvocationParseWithDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("procedure", "com.example.procedure");
        details.put("caller", 12345L);
        details.put("caller_authid", "user123");
        details.put("caller_authrole", "user");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(68);
        wmsg.add(2001L);
        wmsg.add(3001L);
        wmsg.add(details);

        Invocation invocation = Invocation.parse(wmsg);

        assertEquals("com.example.procedure", invocation.details.get("procedure"));
        assertEquals(12345L, invocation.details.get("caller"));
        assertEquals("user123", invocation.details.get("caller_authid"));
        assertEquals("user", invocation.details.get("caller_authrole"));
    }

    @Test
    public void testInvocationMarshalBasic() {
        Invocation invocation = new Invocation(2001L, 3001L, null, null, null);

        List<Object> marshaled = invocation.marshal();

        assertEquals(4, marshaled.size());
        assertEquals(68, marshaled.get(0));
        assertEquals(2001L, marshaled.get(1));
        assertEquals(3001L, marshaled.get(2));

        Map<?, ?> details = (Map<?, ?>) marshaled.get(3);
        assertTrue(details.isEmpty());
    }

    @Test
    public void testInvocationMarshalWithDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("procedure", "com.example.procedure");
        details.put("caller", 12345L);

        Invocation invocation = new Invocation(2001L, 3001L, details, null, null);

        List<Object> marshaled = invocation.marshal();

        Map<?, ?> marshaledDetails = (Map<?, ?>) marshaled.get(3);
        assertEquals("com.example.procedure", marshaledDetails.get("procedure"));
        assertEquals(12345L, marshaledDetails.get("caller"));
    }

    @Test
    public void testInvocationMarshalWithArgs() {
        List<Object> args = new ArrayList<>();
        args.add("Hello");
        args.add(42);

        Invocation invocation = new Invocation(2001L, 3001L, null, args, null);

        List<Object> marshaled = invocation.marshal();

        assertEquals(5, marshaled.size());
        assertEquals(args, marshaled.get(4));
    }

    @Test
    public void testInvocationMarshalWithKwargs() {
        List<Object> args = new ArrayList<>();
        args.add("data");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key", "value");

        Invocation invocation = new Invocation(2001L, 3001L, null, args, kwargs);

        List<Object> marshaled = invocation.marshal();

        assertEquals(6, marshaled.size());
        assertEquals(args, marshaled.get(4));
        assertEquals(kwargs, marshaled.get(5));
    }

    @Test
    public void testInvocationMarshalKwargsOnly() {
        // When only kwargs is present, empty args should be added
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("param", "value");

        Invocation invocation = new Invocation(2001L, 3001L, null, null, kwargs);

        List<Object> marshaled = invocation.marshal();

        assertEquals(6, marshaled.size());
        assertEquals(new ArrayList<>(), marshaled.get(4));  // Empty args
        assertEquals(kwargs, marshaled.get(5));
    }

    @Test(expected = ProtocolError.class)
    public void testInvocationParseBinaryPayloadThrows() {
        Map<String, Object> details = new HashMap<>();

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(68);
        wmsg.add(2001L);
        wmsg.add(3001L);
        wmsg.add(details);
        wmsg.add(new byte[]{0x01, 0x02});  // Binary payload - not allowed

        Invocation.parse(wmsg);
    }

    @Test
    public void testInvocationWithProgressiveResultsDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("receive_progress", true);
        details.put("timeout", 30000);

        List<Object> args = new ArrayList<>();
        args.add("input");

        Invocation invocation = new Invocation(2001L, 3001L, details, args, null);

        List<Object> marshaled = invocation.marshal();
        Invocation reparsed = Invocation.parse(marshaled);

        assertEquals(true, reparsed.details.get("receive_progress"));
        assertEquals(30000, reparsed.details.get("timeout"));
    }
}
