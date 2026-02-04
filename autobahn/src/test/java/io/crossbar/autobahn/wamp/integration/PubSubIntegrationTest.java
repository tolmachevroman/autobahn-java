package io.crossbar.autobahn.wamp.integration;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.Publication;
import io.crossbar.autobahn.wamp.types.PublishOptions;
import io.crossbar.autobahn.wamp.types.Subscription;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for WAMP PubSub functionality.
 * These tests run against a real Crossbar.io router in Docker.
 */
public class PubSubIntegrationTest extends WampIntegrationTestBase {

    @Test
    public void testPublishSubscribeBasic() throws Exception {
        String topic = "com.test.topic1";
        CountDownLatch latch = new CountDownLatch(1);
        List<Object> receivedArgs = new ArrayList<>();

        // Create subscriber session
        Session subscriberSession = new Session();
        subscriberSession.addOnJoinListener((session, details) -> {
            session.subscribe(topic, (args, kwargs, subDetails) -> {
                receivedArgs.addAll(args);
                latch.countDown();
            }).whenComplete((sub, throwable) -> {
                if (throwable != null) {
                    System.err.println("Subscription failed: " + throwable.getMessage());
                }
            });
        });

        Client subscriberClient = new Client(subscriberSession, getWampUrl(), getRealm());
        subscriberClient.connect();

        // Wait for subscription to be established
        Thread.sleep(1000);

        // Create publisher session
        Session publisherSession = new Session();
        publisherSession.addOnJoinListener((session, details) -> {
            PublishOptions options = new PublishOptions(true, false);
            session.publish(topic, options, 42, "test message")
                    .whenComplete((pub, throwable) -> {
                        if (throwable != null) {
                            System.err.println("Publish failed: " + throwable.getMessage());
                        }
                    });
        });

        Client publisherClient = new Client(publisherSession, getWampUrl(), getRealm());
        publisherClient.connect();

        // Wait for message to be received
        boolean received = latch.await(5, TimeUnit.SECONDS);

        assertTrue("Message should be received within 5 seconds", received);
        assertThat(receivedArgs).containsExactly(42, "test message");

        // Cleanup
        subscriberSession.leave();
        publisherSession.leave();
    }

    @Test
    public void testMultipleSubscribers() throws Exception {
        String topic = "com.test.broadcast";
        int numSubscribers = 3;
        CountDownLatch latch = new CountDownLatch(numSubscribers);
        AtomicInteger messageCount = new AtomicInteger(0);

        List<Session> subscriberSessions = new ArrayList<>();

        // Create multiple subscribers
        for (int i = 0; i < numSubscribers; i++) {
            Session session = new Session();
            session.addOnJoinListener((s, details) -> {
                s.subscribe(topic, (args, kwargs, subDetails) -> {
                    messageCount.incrementAndGet();
                    latch.countDown();
                });
            });

            Client client = new Client(session, getWampUrl(), getRealm());
            client.connect();
            subscriberSessions.add(session);
        }

        // Wait for all subscriptions
        Thread.sleep(1000);

        // Publish a message
        Session publisherSession = new Session();
        publisherSession.addOnJoinListener((session, details) -> {
            PublishOptions options = new PublishOptions(true, false);
            session.publish(topic, options, "broadcast message");
        });

        Client publisherClient = new Client(publisherSession, getWampUrl(), getRealm());
        publisherClient.connect();

        // Wait for all subscribers to receive
        boolean allReceived = latch.await(5, TimeUnit.SECONDS);

        assertTrue("All subscribers should receive the message", allReceived);
        assertThat(messageCount.get()).isEqualTo(numSubscribers);

        // Cleanup
        subscriberSessions.forEach(Session::leave);
        publisherSession.leave();
    }

    @Test
    public void testPublishWithAcknowledge() throws Exception {
        String topic = "com.test.acknowledged";

        Session session = new Session();
        CountDownLatch latch = new CountDownLatch(1);

        session.addOnJoinListener((s, details) -> {
            PublishOptions options = new PublishOptions(true, false);
            s.publish(topic, options, "test")
                    .whenComplete((publication, throwable) -> {
                        if (throwable == null && publication != null) {
                            latch.countDown();
                        }
                    });
        });

        Client client = new Client(session, getWampUrl(), getRealm());
        client.connect();

        boolean acknowledged = latch.await(5, TimeUnit.SECONDS);
        assertTrue("Publish should be acknowledged", acknowledged);

        session.leave();
    }
}
