package io.crossbar.autobahn.wamp.types;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

public class SubscriptionTest {

    @Test
    public void testSubscriptionCreation() {
        long subscriptionId = 789L;
        String topic = "com.example.ontopic";
        TypeReference typeRef = new TypeReference<String>() {};
        Class typeClass = String.class;
        Object handler = "handlerObj";

        Subscription subscription = new Subscription(
                subscriptionId, topic, typeRef, typeClass, handler, null
        );

        assertNotNull(subscription);
        assertEquals(subscriptionId, subscription.subscription);
        assertEquals(topic, subscription.topic);
        assertEquals(typeRef, subscription.resultTypeRef);
        assertEquals(typeClass, subscription.resultTypeClass);
        assertEquals(handler, subscription.handler);
        assertNull(subscription.session);
    }

    @Test
    public void testSubscriptionIsActiveByDefault() {
        Subscription subscription = new Subscription(
                1L, "topic", null, null, "handler", null
        );

        assertTrue("Subscription should be active by default", subscription.isActive());
    }

    @Test
    public void testSubscriptionSetInactive() {
        Subscription subscription = new Subscription(
                1L, "topic", null, null, "handler", null
        );

        assertTrue(subscription.isActive());
        
        subscription.setInactive();
        
        assertFalse("Subscription should be inactive after setInactive()", 
                subscription.isActive());
    }

    @Test
    public void testSubscriptionSetInactiveTwiceThrowsException() {
        Subscription subscription = new Subscription(
                1L, "topic", null, null, "handler", null
        );

        subscription.setInactive();
        
        assertThatThrownBy(() -> subscription.setInactive())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already invactive");
    }

    @Test
    public void testSubscriptionWithNullTypeReferences() {
        Subscription subscription = new Subscription(
                100L, "test.topic", null, null, "handler", null
        );

        assertNull(subscription.resultTypeRef);
        assertNull(subscription.resultTypeClass);
        assertTrue(subscription.isActive());
    }

    @Test
    public void testSubscriptionFieldsArePublic() {
        Subscription subscription = new Subscription(
                555L, "public.topic", null, String.class, "h", null
        );

        // Verify all fields are accessible without getters
        assertEquals(555L, subscription.subscription);
        assertEquals("public.topic", subscription.topic);
        assertEquals(String.class, subscription.resultTypeClass);
        assertNotNull(subscription.handler);
    }
}
