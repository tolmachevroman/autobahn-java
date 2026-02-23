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

public class RegisterMessageTest {

    @Test
    public void testRegisterMarshalBasic() {
        Register register = new Register(1001L, "com.example.procedure", null, null);

        List<Object> marshaled = register.marshal();

        assertEquals(4, marshaled.size());
        assertEquals(64, marshaled.get(0));  // REGISTER message type
        assertEquals(1001L, marshaled.get(1));
        assertEquals("com.example.procedure", marshaled.get(3));

        // Options should be empty for default values
        Map<?, ?> options = (Map<?, ?>) marshaled.get(2);
        assertTrue(options.isEmpty());
    }

    @Test
    public void testRegisterMarshalWithPrefixMatch() {
        Register register = new Register(1002L, "com.example.", "prefix", null);

        List<Object> marshaled = register.marshal();

        Map<?, ?> options = (Map<?, ?>) marshaled.get(2);
        assertEquals("prefix", options.get("match"));
    }

    @Test
    public void testRegisterMarshalWithWildcardMatch() {
        Register register = new Register(1003L, "com..procedure", "wildcard", null);

        List<Object> marshaled = register.marshal();

        Map<?, ?> options = (Map<?, ?>) marshaled.get(2);
        assertEquals("wildcard", options.get("match"));
    }

    @Test
    public void testRegisterMarshalWithRoundrobinInvoke() {
        Register register = new Register(1004L, "com.example.procedure", null, "roundrobin");

        List<Object> marshaled = register.marshal();

        Map<?, ?> options = (Map<?, ?>) marshaled.get(2);
        assertEquals("roundrobin", options.get("invoke"));
    }

    @Test
    public void testRegisterMarshalWithRandomInvoke() {
        Register register = new Register(1005L, "com.example.procedure", null, "random");

        List<Object> marshaled = register.marshal();

        Map<?, ?> options = (Map<?, ?>) marshaled.get(2);
        assertEquals("random", options.get("invoke"));
    }

    @Test
    public void testRegisterMarshalWithFirstInvoke() {
        Register register = new Register(1006L, "com.example.procedure", null, "first");

        List<Object> marshaled = register.marshal();

        Map<?, ?> options = (Map<?, ?>) marshaled.get(2);
        assertEquals("first", options.get("invoke"));
    }

    @Test
    public void testRegisterMarshalWithLastInvoke() {
        Register register = new Register(1007L, "com.example.procedure", null, "last");

        List<Object> marshaled = register.marshal();

        Map<?, ?> options = (Map<?, ?>) marshaled.get(2);
        assertEquals("last", options.get("invoke"));
    }

    @Test
    public void testRegisterMarshalWithAllInvoke() {
        Register register = new Register(1008L, "com.example.procedure", null, "all");

        List<Object> marshaled = register.marshal();

        Map<?, ?> options = (Map<?, ?>) marshaled.get(2);
        assertEquals("all", options.get("invoke"));
    }

    @Test
    public void testRegisterMarshalSingleInvokeNotIncluded() {
        // "single" is the default, so it shouldn't be in the marshaled options
        Register register = new Register(1009L, "com.example.procedure", null, "single");

        List<Object> marshaled = register.marshal();

        Map<?, ?> options = (Map<?, ?>) marshaled.get(2);
        assertNull(options.get("invoke"));
    }

    @Test
    public void testRegisterMarshalExactMatchNotIncluded() {
        // "exact" is the default, so it shouldn't be in the marshaled options
        Register register = new Register(1010L, "com.example.procedure", "exact", null);

        List<Object> marshaled = register.marshal();

        Map<?, ?> options = (Map<?, ?>) marshaled.get(2);
        assertNull(options.get("match"));
    }

    @Test
    public void testRegisterParse() {
        Map<String, Object> options = new HashMap<>();
        List<Object> wmsg = new ArrayList<>();
        wmsg.add(64);  // REGISTER
        wmsg.add(1001L);
        wmsg.add(options);
        wmsg.add("com.example.procedure");

        Register register = Register.parse(wmsg);

        assertEquals(1001L, register.request);
        assertEquals("com.example.procedure", register.procedure);
        assertNull(register.match);
        assertNull(register.invoke);
    }

    @Test
    public void testRegisterParseWithPrefixMatch() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "prefix");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(64);
        wmsg.add(1002L);
        wmsg.add(options);
        wmsg.add("com.example.");

        Register register = Register.parse(wmsg);

        assertEquals("prefix", register.match);
    }

    @Test
    public void testRegisterParseWithRoundrobinInvoke() {
        Map<String, Object> options = new HashMap<>();
        options.put("invoke", "roundrobin");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(64);
        wmsg.add(1003L);
        wmsg.add(options);
        wmsg.add("com.example.procedure");

        Register register = Register.parse(wmsg);

        assertEquals("roundrobin", register.invoke);
    }

    @Test
    public void testRegisterParseWithAllInvokeModes() {
        String[] invokeModes = {"single", "first", "last", "roundrobin", "random", "all"};

        for (String mode : invokeModes) {
            Map<String, Object> options = new HashMap<>();
            options.put("invoke", mode);

            List<Object> wmsg = new ArrayList<>();
            wmsg.add(64);
            wmsg.add(1000L);
            wmsg.add(options);
            wmsg.add("com.example.procedure");

            Register register = Register.parse(wmsg);
            assertEquals(mode, register.invoke);
        }
    }

    @Test(expected = ProtocolError.class)
    public void testRegisterParseInvalidMatch() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "invalid");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(64);
        wmsg.add(1004L);
        wmsg.add(options);
        wmsg.add("com.example.procedure");

        Register.parse(wmsg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterParseInvalidInvoke() {
        Map<String, Object> options = new HashMap<>();
        options.put("invoke", "invalid");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(64);
        wmsg.add(1005L);
        wmsg.add(options);
        wmsg.add("com.example.procedure");

        Register.parse(wmsg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterMarshalInvalidMatch() {
        new Register(1006L, "com.example.procedure", "invalid", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterMarshalInvalidInvoke() {
        new Register(1007L, "com.example.procedure", null, "invalid");
    }

    @Test
    public void testRegisterParseWithBothOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("match", "prefix");
        options.put("invoke", "roundrobin");

        List<Object> wmsg = new ArrayList<>();
        wmsg.add(64);
        wmsg.add(1008L);
        wmsg.add(options);
        wmsg.add("com.example.");

        Register register = Register.parse(wmsg);

        assertEquals("prefix", register.match);
        assertEquals("roundrobin", register.invoke);
    }
}
