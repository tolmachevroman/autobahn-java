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
        String topic = "com.example.topic1";

        Publish publish = new Publish(requestId, topic, null, null, false, true, false);

        assertNotNull(publish);
        assertEquals(requestId, publish.request);
        assertEquals(topic, publish.topic);
        assertEquals(Publish.MESSAGE_TYPE, publish.getMessageType());
        assertFalse(publish.acknowledge);
        assertTrue(publish.excludeMe);
        assertFalse(publish.retain);
    }

    @Test
    public void testPublishWithArguments() {
        long requestId = 789;
        String topic = "com.example.oncounter";
        List<Object> args = Arrays.asList(42, "test", true);
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key1", "value1");

        Publish publish = new Publish(requestId, topic, args, kwargs, true, false, false);

        assertNotNull(publish);
        assertEquals(requestId, publish.request);
        assertEquals(topic, publish.topic);
        assertThat(publish.args).containsExactly(42, "test", true);
        assertThat(publish.kwargs).containsEntry("key1", "value1");
        assertTrue(publish.acknowledge);
        assertFalse(publish.excludeMe);
    }

    @Test
    public void testPublishMessageType() {
        Publish publish = new Publish(1, "topic", null, null, false, true, false);
        assertEquals(Publish.MESSAGE_TYPE, publish.getMessageType());
        assertEquals(16, Publish.MESSAGE_TYPE);
    }

    @Test
    public void testPublishWithAcknowledge() {
        Publish publish = new Publish(999, "com.test.topic", null, null, true, false, false);

        assertTrue("Should have acknowledge=true", publish.acknowledge);
        assertFalse("Should have excludeMe=false", publish.excludeMe);
        assertFalse("Should have retain=false", publish.retain);
    }

    @Test
    public void testPublishWithRetain() {
        Publish publish = new Publish(111, "com.test.retained", null, null, false, true, true);

        assertTrue("Should have retain=true", publish.retain);
    }
}
