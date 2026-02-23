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

package io.crossbar.autobahn.demogallery;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java8.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.CallResult;
import io.crossbar.autobahn.wamp.types.CloseDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.InvocationDetails;
import io.crossbar.autobahn.wamp.types.PublishOptions;
import io.crossbar.autobahn.wamp.types.SessionDetails;

/**
 * Integration test for WAMP functionality.
 * Exit codes: 0 = success, 1 = test failed, 2 = error
 */
public class IntegrationTest {

    private static final Logger LOGGER = Logger.getLogger(IntegrationTest.class.getName());
    private static final String PROC_ADD2 = "com.test.add2";
    private static final String TOPIC_COUNTER = "com.test.oncounter";
    
    private final AtomicBoolean testsPassed = new AtomicBoolean(true);
    private final AtomicInteger testsCompleted = new AtomicInteger(0);
    private final AtomicBoolean leaving = new AtomicBoolean(false);
    private static final int TOTAL_TESTS = 2;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: IntegrationTest <websocketURL> <realm>");
            System.exit(2);
        }
        
        try {
            IntegrationTest test = new IntegrationTest();
            ExitInfo result = test.run(args[0], args[1]).get(25, TimeUnit.SECONDS);
            System.exit(result != null ? result.code : 1);
        } catch (Exception e) {
            LOGGER.severe("Test failed: " + e.getMessage());
            System.exit(1);
        }
    }

    public CompletableFuture<ExitInfo> run(String websocketURL, String realm) {
        Session session = new Session();
        session.addOnConnectListener(this::onConnectCallback);
        session.addOnJoinListener(this::onJoinCallback);
        session.addOnLeaveListener(this::onLeaveCallback);
        session.addOnDisconnectListener(this::onDisconnectCallback);

        Client client = new Client(session, websocketURL, realm);
        return client.connect();
    }

    private void onConnectCallback(Session session) {
        LOGGER.info("Session connected, ID=" + session.getID());
    }

    private void onJoinCallback(Session session, SessionDetails details) {
        LOGGER.info("Joined realm: " + details.realm);

        // Safety timeout to prevent hanging forever
        scheduler.schedule(() -> {
            if (testsCompleted.get() < TOTAL_TESTS) {
                LOGGER.severe("Integration test timeout - forcing shutdown");
                testsPassed.set(false);
                testsCompleted.set(TOTAL_TESTS);
                checkDone(session);
            }
        }, 20, TimeUnit.SECONDS);

        // Test 1: Register and call RPC
        testRPC(session);
        
        // Test 2: Pub/Sub
        testPubSub(session);
    }

    private void testRPC(Session session) {
        LOGGER.info("Test 1: RPC - Register procedure " + PROC_ADD2);
        
        session.register(PROC_ADD2, this::add2).whenComplete((reg, throwable) -> {
            if (throwable != null) {
                LOGGER.severe("RPC Register FAILED: " + throwable.getMessage());
                testsPassed.set(false);
                testsCompleted.incrementAndGet();
                checkDone(session);
                return;
            }
            
            LOGGER.info("Registered procedure, now calling it...");
            
            // Call the procedure we just registered
            session.call(PROC_ADD2, 2, 3).whenComplete((result, callThrowable) -> {
                if (callThrowable != null) {
                    LOGGER.severe("RPC Call FAILED: " + callThrowable.getMessage());
                    testsPassed.set(false);
                } else {
                    int expected = 5;
                    int actual = (int) result.results.get(0);
                    if (actual == expected) {
                        LOGGER.info("RPC Test PASSED: " + actual + " = " + expected);
                    } else {
                        LOGGER.severe("RPC Test FAILED: " + actual + " != " + expected);
                        testsPassed.set(false);
                    }
                }
                testsCompleted.incrementAndGet();
                checkDone(session);
            });
        });
    }

    private void testPubSub(Session session) {
        LOGGER.info("Test 2: Pub/Sub - Subscribe to " + TOPIC_COUNTER);
        
        AtomicBoolean received = new AtomicBoolean(false);
        
        session.subscribe(TOPIC_COUNTER, args -> {
            LOGGER.info("Received event on " + TOPIC_COUNTER);
            received.set(true);
        }).whenComplete((sub, throwable) -> {
            if (throwable != null) {
                LOGGER.severe("Subscribe FAILED: " + throwable.getMessage());
                testsPassed.set(false);
                testsCompleted.incrementAndGet();
                checkDone(session);
                return;
            }
            
            LOGGER.info("Subscribed, now publishing...");
            
            // Publish a message (excludeMe=false so we receive our own event)
            PublishOptions opts = new PublishOptions(false, false);
            session.publish(TOPIC_COUNTER, opts, 42, "test").whenComplete((pub, pubThrowable) -> {
                if (pubThrowable != null) {
                    LOGGER.severe("Publish FAILED: " + pubThrowable.getMessage());
                    testsPassed.set(false);
                } else {
                    LOGGER.info("Published message");
                }
                
                // Wait a bit then check if we received it
                scheduler.schedule(() -> {
                    if (received.get()) {
                        LOGGER.info("Pub/Sub Test PASSED");
                    } else {
                        LOGGER.severe("Pub/Sub Test FAILED: No event received");
                        testsPassed.set(false);
                    }
                    testsCompleted.incrementAndGet();
                    checkDone(session);
                }, 2, TimeUnit.SECONDS);
            });
        });
    }

    private void checkDone(Session session) {
        if (testsCompleted.get() >= TOTAL_TESTS && leaving.compareAndSet(false, true)) {
            if (testsPassed.get()) {
                LOGGER.info("All tests PASSED!");
            } else {
                LOGGER.severe("Some tests FAILED!");
            }
            // Leave after short delay to allow logs to flush
            scheduler.schedule(() -> {
                session.leave();
                scheduler.shutdown();
            }, 1, TimeUnit.SECONDS);
        }
    }

    private List<Object> add2(List<Integer> args, InvocationDetails details) {
        int res = args.get(0) + args.get(1);
        return Arrays.asList(res);
    }

    private void onLeaveCallback(Session session, CloseDetails detail) {
        LOGGER.info("Left session: " + detail.reason);
    }

    private void onDisconnectCallback(Session session, boolean wasClean) {
        LOGGER.info("Session disconnected.");
    }
}
