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
        Map<String, Object> options = new HashMap<>();
        String procedure = "com.example.add2";

        Call call = new Call(requestId, options, procedure);

        assertNotNull(call);
        assertEquals(requestId, call.request);
        assertEquals(options, call.options);
        assertEquals(procedure, call.procedure);
    }

    @Test
    public void testCallWithArguments() {
        long requestId = 999;
        Map<String, Object> options = new HashMap<>();
        options.put("timeout", 5000);
        String procedure = "com.example.multiply";
        List<Object> args = Arrays.asList(5, 10);

        Call call = new Call(requestId, options, procedure, args, null);

        assertNotNull(call);
        assertThat(call.arguments).containsExactly(5, 10);
    }

    @Test
    public void testCallWithKeywordArguments() {
        long requestId = 777;
        Map<String, Object> options = new HashMap<>();
        String procedure = "com.example.create_user";
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("username", "alice");
        kwargs.put("email", "alice@example.com");

        Call call = new Call(requestId, options, procedure, null, kwargs);

        assertThat(call.argumentsKw)
                .containsEntry("username", "alice")
                .containsEntry("email", "alice@example.com");
    }

    @Test
    public void testCallMessageType() {
        Call call = new Call(1, new HashMap<>(), "proc");
        assertEquals(Call.MESSAGE_TYPE, call.getMessageType());
    }

    @Test
    public void testCallWithProgressiveResults() {
        Map<String, Object> options = new HashMap<>();
        options.put("receive_progress", true);

        Call call = new Call(555, options, "com.example.long_running");

        assertThat(call.options).containsEntry("receive_progress", true);
    }
}
