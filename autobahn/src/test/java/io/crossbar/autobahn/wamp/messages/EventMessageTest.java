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

public class EventMessageTest {

    @Test
    public void testEventParseBasic() {
        Map<String, Object> details = new HashMap<>();

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(36);  // EVENT
        wmsg.add(2001L);  // subscription ID
        wmsg.add(3001L);  // publication ID
        wmsg.add(details);

        Event event = Event.parse(wmsg);

        assertEquals(2001L, event.subscription);
        assertEquals(3001L, event.publication);
        assertNull(event.topic);
        assertFalse(event.retained);
        assertNull(event.args);
        assertNull(event.kwargs);
    }

    @Test
    public void testEventParseWithTopic() {
        Map<String, Object> details = new HashMap<>();
        details.put("topic", "com.example.topic");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(36);
        wmsg.add(2001L);
        wmsg.add(3001L);
        wmsg.add(details);

        Event event = Event.parse(wmsg);

        assertEquals("com.example.topic", event.topic);
    }

    @Test
    public void testEventParseRetained() {
        Map<String, Object> details = new HashMap<>();
        details.put("retained", true);

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(36);
        wmsg.add(2001L);
        wmsg.add(3001L);
        wmsg.add(details);

        Event event = Event.parse(wmsg);

        assertTrue(event.retained);
    }

    @Test
    public void testEventParseWithArgs() {
        Map<String, Object> details = new HashMap<>();

        List<Object> args = new ArrayList<>();
        args.add("Hello, World!");
        args.add(42);
        args.add(true);

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(36);
        wmsg.add(2001L);
        wmsg.add(3001L);
        wmsg.add(details);
        wmsg.add(args);

        Event event = Event.parse(wmsg);

        assertNotNull(event.args);
        assertEquals(3, event.args.size());
        assertEquals("Hello, World!", event.args.get(0));
        assertEquals(42, event.args.get(1));
        assertEquals(true, event.args.get(2));
    }

    @Test
    public void testEventParseWithKwargs() {
        Map<String, Object> details = new HashMap<>();

        List<Object> args = new ArrayList<>();
        args.add("data");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("source", "sensor1");
        kwargs.put("value", 23.5);
        kwargs.put("timestamp", 1234567890L);

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(36);
        wmsg.add(2001L);
        wmsg.add(3001L);
        wmsg.add(details);
        wmsg.add(args);
        wmsg.add(kwargs);

        Event event = Event.parse(wmsg);

        assertNotNull(event.kwargs);
        assertEquals("sensor1", event.kwargs.get("source"));
        assertEquals(23.5, event.kwargs.get("value"));
        assertEquals(1234567890L, event.kwargs.get("timestamp"));
    }

    @Test
    public void testEventMarshalBasic() {
        Event event = new Event(2001L, 3001L, null, false, null, null);

        List<Object> marshaled = event.marshal();

        assertEquals(4, marshaled.size());
        assertEquals(36, marshaled.get(0));
        assertEquals(2001L, marshaled.get(1));
        assertEquals(3001L, marshaled.get(2));

        Map<?, ?> details = (Map<?, ?>) marshaled.get(3);
        assertTrue(details.isEmpty());
    }

    @Test
    public void testEventMarshalWithTopic() {
        Event event = new Event(2001L, 3001L, "com.example.topic", false, null, null);

        List<Object> marshaled = event.marshal();

        Map<?, ?> details = (Map<?, ?>) marshaled.get(3);
        assertEquals("com.example.topic", details.get("topic"));
    }

    @Test
    public void testEventMarshalRetained() {
        Event event = new Event(2001L, 3001L, null, true, null, null);

        List<Object> marshaled = event.marshal();

        Map<?, ?> details = (Map<?, ?>) marshaled.get(3);
        assertEquals(true, details.get("retained"));
    }

    @Test
    public void testEventMarshalWithArgs() {
        List<Object> args = new ArrayList<>();
        args.add("Hello");
        args.add(42);

        Event event = new Event(2001L, 3001L, null, false, args, null);

        List<Object> marshaled = event.marshal();

        assertEquals(5, marshaled.size());
        assertEquals(args, marshaled.get(4));
    }

    @Test
    public void testEventMarshalWithKwargs() {
        List<Object> args = new ArrayList<>();
        args.add("data");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key", "value");

        Event event = new Event(2001L, 3001L, null, false, args, kwargs);

        List<Object> marshaled = event.marshal();

        assertEquals(6, marshaled.size());
        assertEquals(args, marshaled.get(4));
        assertEquals(kwargs, marshaled.get(5));
    }

    @Test
    public void testEventMarshalKwargsOnly() {
        // When only kwargs is present, empty args should be added
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key", "value");

        Event event = new Event(2001L, 3001L, null, false, null, kwargs);

        List<Object> marshaled = event.marshal();

        assertEquals(6, marshaled.size());
        assertEquals(new ArrayList<>(), marshaled.get(4));  // Empty args
        assertEquals(kwargs, marshaled.get(5));
    }

    @Test(expected = ProtocolError.class)
    public void testEventParseBinaryPayloadThrows() {
        Map<String, Object> details = new HashMap<>();

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(36);
        wmsg.add(2001L);
        wmsg.add(3001L);
        wmsg.add(details);
        wmsg.add(new byte[]{0x01, 0x02});  // Binary payload - not allowed

        Event.parse(wmsg);
    }
}
