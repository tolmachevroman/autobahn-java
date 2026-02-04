package io.crossbar.autobahn.wamp.messages;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class PublishTest {

    @Test
    public void testPublishMessageCreation() {
        long requestId = 123456;
        Map<String, Object> options = new HashMap<>();
        String topic = "com.example.topic1";

        Publish publish = new Publish(requestId, options, topic);

        assertNotNull(publish);
        assertEquals(requestId, publish.request);
        assertEquals(options, publish.options);
        assertEquals(topic, publish.topic);
    }

    @Test
    public void testPublishWithArguments() {
        long requestId = 789;
        Map<String, Object> options = new HashMap<>();
        options.put("acknowledge", true);
        String topic = "com.example.oncounter";
        List<Object> args = Arrays.asList(42, "test", true);
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key1", "value1");

        Publish publish = new Publish(requestId, options, topic, args, kwargs);

        assertNotNull(publish);
        assertEquals(requestId, publish.request);
        assertEquals(topic, publish.topic);
        assertThat(publish.arguments).containsExactly(42, "test", true);
        assertThat(publish.argumentsKw).containsEntry("key1", "value1");
    }

    @Test
    public void testPublishMessageType() {
        Publish publish = new Publish(1, new HashMap<>(), "topic");
        assertEquals(Publish.MESSAGE_TYPE, publish.getMessageType());
    }

    @Test
    public void testPublishWithAcknowledge() {
        Map<String, Object> options = new HashMap<>();
        options.put("acknowledge", true);
        options.put("exclude_me", false);

        Publish publish = new Publish(999, options, "com.test.topic");

        assertThat(publish.options)
                .containsEntry("acknowledge", true)
                .containsEntry("exclude_me", false);
    }
}
