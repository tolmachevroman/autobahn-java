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
import io.crossbar.autobahn.wamp.types.SubscribeOptions;

import static org.junit.Assert.*;

public class SubscribeMessageTest {

    @Test
    public void testSubscribeMarshalBasic() {
        SubscribeOptions options = new SubscribeOptions();
        Subscribe subscribe = new Subscribe(1001L, options, "com.example.topic");

        List<Object> marshaled = subscribe.marshal();

        assertEquals(4, marshaled.size());
        assertEquals(32, marshaled.get(0));  // SUBSCRIBE message type
        assertEquals(1001L, marshaled.get(1));
        assertEquals("com.example.topic", marshaled.get(3));

        // Check options don't include "exact" (default is removed)
        Map<?, ?> optionsMap = (Map<?, ?>) marshaled.get(2);
        assertNull(optionsMap.get("match"));
    }

    @Test
    public void testSubscribeMarshalWithPrefixMatch() {
        SubscribeOptions options = new SubscribeOptions();
        options.putMatch("prefix");
        Subscribe subscribe = new Subscribe(1002L, options, "com.example.");

        List<Object> marshaled = subscribe.marshal();

        Map<?, ?> optionsMap = (Map<?, ?>) marshaled.get(2);
        assertEquals("prefix", optionsMap.get("match"));
    }

    @Test
    public void testSubscribeMarshalWithWildcardMatch() {
        SubscribeOptions options = new SubscribeOptions();
        options.putMatch("wildcard");
        Subscribe subscribe = new Subscribe(1003L, options, "com..topic");

        List<Object> marshaled = subscribe.marshal();

        Map<?, ?> optionsMap = (Map<?, ?>) marshaled.get(2);
        assertEquals("wildcard", optionsMap.get("match"));
    }

    @Test
    public void testSubscribeMarshalWithGetRetained() {
        SubscribeOptions options = new SubscribeOptions();
        options.putGetRetained(true);
        Subscribe subscribe = new Subscribe(1004L, options, "com.example.topic");

        List<Object> marshaled = subscribe.marshal();

        Map<?, ?> optionsMap = (Map<?, ?>) marshaled.get(2);
        assertEquals(true, optionsMap.get("get_retained"));
    }

    @Test
    public void testSubscribeParse() {
        Map<String, Object> options = new HashMap<>();
        List<Object> wmsg = new ArrayList<>();
        wmsg.add(32);  // SUBSCRIBE
        wmsg.add(1001L);
        wmsg.add(options);
        wmsg.add("com.example.topic");

        Subscribe subscribe = Subscribe.parse(wmsg);

        assertEquals(1001L, subscribe.request);
        assertEquals("com.example.topic", subscribe.topic);
        assertNull(subscribe.options.getMatch());  // Default (exact) returns null
    }

    @Test
    public void testSubscribeParseWithPrefixMatch() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "prefix");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(32);
        wmsg.add(1002L);
        wmsg.add(options);
        wmsg.add("com.example.");

        Subscribe subscribe = Subscribe.parse(wmsg);

        assertEquals("prefix", subscribe.options.getMatch());
    }

    @Test
    public void testSubscribeParseWithWildcardMatch() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "wildcard");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(32);
        wmsg.add(1003L);
        wmsg.add(options);
        wmsg.add("com..topic");

        Subscribe subscribe = Subscribe.parse(wmsg);

        assertEquals("wildcard", subscribe.options.getMatch());
    }

    @Test(expected = ProtocolError.class)
    public void testSubscribeParseInvalidMatch() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "invalid");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(32);
        wmsg.add(1004L);
        wmsg.add(options);
        wmsg.add("com.example.topic");

        Subscribe.parse(wmsg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubscribeMarshalInvalidMatch() {
        SubscribeOptions options = new SubscribeOptions();
        options.putMatch("invalid");
        new Subscribe(1005L, options, "com.example.topic");
    }

    @Test
    public void testSubscribeParseInvalidMatchType() {
        // SubscribeOptions HashMap allows any type, but getMatch() returns null for non-strings
        SubscribeOptions options = new SubscribeOptions();
        options.put("match", 123);  // Invalid type - integer
        
        // getMatch returns null for non-string values, so it passes validation
        assertNull(options.getMatch());
        
        // Can still create Subscribe, but match will be null (default to exact)
        Subscribe subscribe = new Subscribe(1006L, options, "com.example.topic");
        assertNull(subscribe.options.getMatch());
    }
}
