package io.crossbar.autobahn.wamp.messages;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class CallTest {

    @Test
    public void testCallMessageCreation() {
        long requestId = 12345;
        String procedure = "com.example.add2";

        Call call = new Call(requestId, procedure, null, null, 0);

        assertNotNull(call);
        assertEquals(requestId, call.request);
        assertEquals(procedure, call.procedure);
        assertEquals(48, call.getMessageType());
        assertEquals(0, call.timeout);
    }

    @Test
    public void testCallWithArguments() {
        long requestId = 999;
        String procedure = "com.example.multiply";
        List<Object> args = Arrays.asList(5, 10);

        Call call = new Call(requestId, procedure, args, null, 5000);

        assertNotNull(call);
        assertThat(call.args).containsExactly(5, 10);
        assertEquals(5000, call.timeout);
    }

    @Test
    public void testCallWithKeywordArguments() {
        long requestId = 777;
        String procedure = "com.example.create_user";
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("username", "alice");
        kwargs.put("email", "alice@example.com");

        Call call = new Call(requestId, procedure, null, kwargs, 0);

        assertThat(call.kwargs)
                .containsEntry("username", "alice")
                .containsEntry("email", "alice@example.com");
    }

    @Test
    public void testCallMessageType() {
        Call call = new Call(1, "proc", null, null, 0);
        assertEquals(48, call.getMessageType());
    }

    @Test
    public void testCallWithTimeout() {
        Call call = new Call(555, "com.example.long_running", null, null, 10000);
        assertEquals(10000, call.timeout);
    }

    @Test
    public void testCallWithBothArgumentTypes() {
        List<Object> args = Arrays.asList("arg1", 42);
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key", "value");

        Call call = new Call(123, "proc", args, kwargs, 0);

        assertThat(call.args).hasSize(2);
        assertThat(call.kwargs).hasSize(1);
    }
}
