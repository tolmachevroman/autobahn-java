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

public class PublishMessageTest {

    @Test
    public void testPublishMarshalBasic() {
        Publish publish = new Publish(1001L, "com.example.topic", null, null, false, true, false);

        List<Object> marshaled = publish.marshal();

        assertEquals(4, marshaled.size());
        assertEquals(16, marshaled.get(0));
        assertEquals(1001L, marshaled.get(1));
        assertEquals("com.example.topic", marshaled.get(3));

        Map<String, Object> options = (Map<String, Object>) marshaled.get(2);
        assertTrue(options.isEmpty());  // No options since defaults are used
    }

    @Test
    public void testPublishMarshalWithAcknowledge() {
        Publish publish = new Publish(1002L, "com.example.topic", null, null, true, true, false);

        List<Object> marshaled = publish.marshal();

        Map<String, Object> options = (Map<String, Object>) marshaled.get(2);
        assertTrue((Boolean) options.get("acknowledge"));
    }

    @Test
    public void testPublishMarshalWithExcludeMeFalse() {
        Publish publish = new Publish(1003L, "com.example.topic", null, null, false, false, false);

        List<Object> marshaled = publish.marshal();

        Map<String, Object> options = (Map<String, Object>) marshaled.get(2);
        assertFalse((Boolean) options.get("exclude_me"));
    }

    @Test
    public void testPublishMarshalWithRetain() {
        Publish publish = new Publish(1004L, "com.example.topic", null, null, false, true, true);

        List<Object> marshaled = publish.marshal();

        Map<String, Object> options = (Map<String, Object>) marshaled.get(2);
        assertTrue((Boolean) options.get("retain"));
    }

    @Test
    public void testPublishMarshalWithArgs() {
        List<Object> args = new ArrayList<>();
        args.add("Hello, World!");
        args.add(42);

        Publish publish = new Publish(1005L, "com.example.news", args, null, false, true, false);

        List<Object> marshaled = publish.marshal();

        assertEquals(5, marshaled.size());
        assertEquals(args, marshaled.get(4));
    }

    @Test
    public void testPublishMarshalWithKwargs() {
        List<Object> args = new ArrayList<>();
        args.add("data");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("source", "sensor1");
        kwargs.put("value", 23.5);

        Publish publish = new Publish(1006L, "com.example.sensor", args, kwargs, true, false, true);

        List<Object> marshaled = publish.marshal();

        assertEquals(6, marshaled.size());
        assertEquals(args, marshaled.get(4));
        assertEquals(kwargs, marshaled.get(5));

        Map<String, Object> options = (Map<String, Object>) marshaled.get(2);
        assertTrue((Boolean) options.get("acknowledge"));
        assertFalse((Boolean) options.get("exclude_me"));
        assertTrue((Boolean) options.get("retain"));
    }

    @Test
    public void testPublishParse() {
        List<Object> wmsg = new ArrayList<>();
        wmsg.add(16);
        wmsg.add(1001L);
        wmsg.add(new HashMap<>());
        wmsg.add("com.example.topic");

        Publish publish = Publish.parse(wmsg);

        assertEquals(1001L, publish.request);
        assertEquals("com.example.topic", publish.topic);
        assertNull(publish.args);
        assertNull(publish.kwargs);
        assertFalse(publish.acknowledge);
        assertTrue(publish.excludeMe);  // default
        assertFalse(publish.retain);
    }

    @Test
    public void testPublishParseWithOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("acknowledge", true);
        options.put("exclude_me", false);
        options.put("retain", true);

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(16);
        wmsg.add(1002L);
        wmsg.add(options);
        wmsg.add("com.example.topic");

        Publish publish = Publish.parse(wmsg);

        assertTrue(publish.acknowledge);
        assertFalse(publish.excludeMe);
        assertTrue(publish.retain);
    }

    @Test
    public void testPublishParseWithPayload() {
        List<Object> args = new ArrayList<>();
        args.add("temperature");
        args.add(22.5);

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("unit", "celsius");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(16);
        wmsg.add(1003L);
        wmsg.add(new HashMap<>());
        wmsg.add("com.example.sensor");
        wmsg.add(args);
        wmsg.add(kwargs);

        Publish publish = Publish.parse(wmsg);

        assertEquals(1003L, publish.request);
        assertEquals("com.example.sensor", publish.topic);
        assertEquals(2, publish.args.size());
        assertEquals("temperature", publish.args.get(0));
        assertEquals(22.5, publish.args.get(1));
        assertEquals("celsius", publish.kwargs.get("unit"));
    }
}
