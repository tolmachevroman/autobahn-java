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

public class YieldMessageTest {

    @Test
    public void testYieldMarshalBasic() {
        Yield yield = new Yield(2001L, null, null);

        List<Object> marshaled = yield.marshal();

        assertEquals(3, marshaled.size());
        assertEquals(70, marshaled.get(0));  // YIELD message type
        assertEquals(2001L, marshaled.get(1));

        Map<?, ?> options = (Map<?, ?>) marshaled.get(2);
        assertTrue(options.isEmpty());
    }

    @Test
    public void testYieldMarshalWithArgs() {
        List<Object> args = new ArrayList<>();
        args.add("result1");
        args.add(42);
        args.add(true);

        Yield yield = new Yield(2001L, args, null);

        List<Object> marshaled = yield.marshal();

        assertEquals(4, marshaled.size());
        assertEquals(args, marshaled.get(3));
    }

    @Test
    public void testYieldMarshalWithKwargs() {
        List<Object> args = new ArrayList<>();
        args.add("data");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("status", "success");
        kwargs.put("code", 200);

        Yield yield = new Yield(2001L, args, kwargs);

        List<Object> marshaled = yield.marshal();

        assertEquals(5, marshaled.size());
        assertEquals(args, marshaled.get(3));
        assertEquals(kwargs, marshaled.get(4));
    }

    @Test
    public void testYieldMarshalKwargsOnly() {
        // When only kwargs is present, empty args should be added
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key", "value");

        Yield yield = new Yield(2001L, null, kwargs);

        List<Object> marshaled = yield.marshal();

        assertEquals(5, marshaled.size());
        assertEquals(new ArrayList<>(), marshaled.get(3));  // Empty args
        assertEquals(kwargs, marshaled.get(4));
    }

    @Test
    public void testYieldParseBasic() {
        Map<String, Object> options = new HashMap<>();

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(70);  // YIELD
        wmsg.add(2001L);  // request ID (INVOCATION request we're responding to)
        wmsg.add(options);

        Yield yield = Yield.parse(wmsg);

        assertEquals(2001L, yield.request);
        assertNull(yield.args);
        assertNull(yield.kwargs);
    }

    @Test
    public void testYieldParseWithArgs() {
        Map<String, Object> options = new HashMap<>();

        List<Object> args = new ArrayList<>();
        args.add("result");
        args.add(42);

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(70);
        wmsg.add(2001L);
        wmsg.add(options);
        wmsg.add(args);

        Yield yield = Yield.parse(wmsg);

        assertNotNull(yield.args);
        assertEquals(2, yield.args.size());
        assertEquals("result", yield.args.get(0));
        assertEquals(42, yield.args.get(1));
    }

    @Test
    public void testYieldParseWithKwargs() {
        Map<String, Object> options = new HashMap<>();

        List<Object> args = new ArrayList<>();
        args.add("data");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("status", "ok");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(70);
        wmsg.add(2001L);
        wmsg.add(options);
        wmsg.add(args);
        wmsg.add(kwargs);

        Yield yield = Yield.parse(wmsg);

        assertNotNull(yield.kwargs);
        assertEquals("ok", yield.kwargs.get("status"));
    }

    @Test
    public void testYieldRoundtrip() {
        // Test marshal -> parse -> marshal consistency
        List<Object> args = new ArrayList<>();
        args.add("arg1");

        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key", "value");

        Yield original = new Yield(2001L, args, kwargs);
        List<Object> marshaled = original.marshal();
        Yield reparsed = Yield.parse(marshaled);
        List<Object> remarshaled = reparsed.marshal();

        assertEquals(marshaled, remarshaled);
    }

    @Test(expected = ProtocolError.class)
    public void testYieldParseBinaryPayloadThrows() {
        Map<String, Object> options = new HashMap<>();

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(70);
        wmsg.add(2001L);
        wmsg.add(options);
        wmsg.add(new byte[]{0x01, 0x02});  // Binary payload - not allowed

        Yield.parse(wmsg);
    }

    @Test
    public void testYieldProgressiveResult() {
        // Yield can include progressive result options (though options are empty by default)
        Map<String, Object> options = new HashMap<>();

        List<Object> args = new ArrayList<>();
        args.add("progress");
        args.add(50);  // 50% complete

        Yield yield = new Yield(2001L, args, null);

        List<Object> marshaled = yield.marshal();

        assertEquals(4, marshaled.size());
        List<?> resultArgs = (List<?>) marshaled.get(3);
        assertEquals(50, resultArgs.get(1));
    }
}
