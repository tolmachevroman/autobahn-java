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

public class CallMessageTest {

    @Test
    public void testCallMarshalBasic() {
        Call call = new Call(12345L, "com.example.add", null, null, 0);

        List<Object> marshaled = call.marshal();

        assertEquals(4, marshaled.size());
        assertEquals(48, marshaled.get(0));
        assertEquals(12345L, marshaled.get(1));
        assertEquals("com.example.add", marshaled.get(3));

        Map<String, Object> options = (Map<String, Object>) marshaled.get(2);
        assertTrue(options.isEmpty());
    }

    @Test
    public void testCallMarshalWithArgs() {
        List<Object> args = new ArrayList<>();
        args.add(1);
        args.add(2);

        Call call = new Call(12346L, "com.example.add", args, null, 0);

        List<Object> marshaled = call.marshal();

        assertEquals(5, marshaled.size());
        assertEquals(args, marshaled.get(4));
    }

    @Test
    public void testCallMarshalWithKwargs() {
        List<Object> args = new ArrayList<>();
        args.add("value1");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key1", "value1");

        Call call = new Call(12347L, "com.example.action", args, kwargs, 0);

        List<Object> marshaled = call.marshal();

        assertEquals(6, marshaled.size());
        assertEquals(args, marshaled.get(4));
        assertEquals(kwargs, marshaled.get(5));
    }

    @Test
    public void testCallMarshalWithTimeout() {
        Call call = new Call(12348L, "com.example.long", null, null, 30000);

        List<Object> marshaled = call.marshal();

        Map<String, Object> options = (Map<String, Object>) marshaled.get(2);
        assertEquals(30000, options.get("timeout"));
    }

    @Test
    public void testCallParse() {
        List<Object> wmsg = new ArrayList<>();
        wmsg.add(48);
        wmsg.add(12345L);
        wmsg.add(new HashMap<>());
        wmsg.add("com.example.add");

        Call call = Call.parse(wmsg);

        assertEquals(12345L, call.request);
        assertEquals("com.example.add", call.procedure);
        assertNull(call.args);
        assertNull(call.kwargs);
        assertEquals(0, call.timeout);
    }

    @Test
    public void testCallParseWithArgs() {
        List<Object> args = new ArrayList<>();
        args.add(10);
        args.add(20);

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(48);
        wmsg.add(12346L);
        wmsg.add(new HashMap<>());
        wmsg.add("com.example.add");
        wmsg.add(args);

        Call call = Call.parse(wmsg);

        assertEquals(12346L, call.request);
        assertEquals("com.example.add", call.procedure);
        assertEquals(2, call.args.size());
        assertEquals(10, call.args.get(0));
        assertEquals(20, call.args.get(1));
    }

    @Test
    public void testCallParseWithKwargs() {
        List<Object> args = new ArrayList<>();
        args.add("data");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("id", 100);
        kwargs.put("name", "test");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(48);
        wmsg.add(12347L);
        wmsg.add(new HashMap<>());
        wmsg.add("com.example.process");
        wmsg.add(args);
        wmsg.add(kwargs);

        Call call = Call.parse(wmsg);

        assertEquals(args, call.args);
        assertEquals(kwargs, call.kwargs);
    }
}
