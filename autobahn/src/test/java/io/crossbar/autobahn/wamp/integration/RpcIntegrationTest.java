package io.crossbar.autobahn.wamp.integration;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.CallResult;
import io.crossbar.autobahn.wamp.types.Registration;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Integration tests for WAMP RPC (Remote Procedure Call) functionality.
 * Tests call/register patterns against a real Crossbar.io router.
 */
public class RpcIntegrationTest extends WampIntegrationTestBase {

    private static List<Object> add2Handler(List<Integer> args, io.crossbar.autobahn.wamp.types.InvocationDetails details) {
        int a = args.get(0);
        int b = args.get(1);
        return Arrays.asList(a + b);
    }

    private static List<Object> multiplyHandler(List<Integer> args, io.crossbar.autobahn.wamp.types.InvocationDetails details) {
        int a = args.get(0);
        int b = args.get(1);
        int c = args.get(2);
        return Arrays.asList(a * b * c);
    }

    private static List<Object> echoHandler(List<Object> args, io.crossbar.autobahn.wamp.types.InvocationDetails details) {
        return Arrays.asList(args.get(0));
    }

    @Test
    public void testBasicCallRegister() throws Exception {
        String procedure = "com.test.add2";
        CountDownLatch registrationLatch = new CountDownLatch(1);
        CountDownLatch callLatch = new CountDownLatch(1);
        AtomicReference<CallResult> result = new AtomicReference<>();

        // Create callee session (registers procedure)
        Session calleeSession = new Session();
        calleeSession.addOnJoinListener((session, details) -> {
            session.register(procedure, RpcIntegrationTest::add2Handler)
                    .whenComplete((reg, throwable) -> {
                        if (throwable == null) {
                            registrationLatch.countDown();
                        }
                    });
        });

        Client calleeClient = new Client(calleeSession, getWampUrl(), getRealm());
        calleeClient.connect();

        // Wait for registration
        assertTrue("Registration should complete", 
                registrationLatch.await(5, TimeUnit.SECONDS));

        // Create caller session
        Session callerSession = new Session();
        callerSession.addOnJoinListener((session, details) -> {
            session.call(procedure, 5, 3)
                    .whenComplete((callResult, throwable) -> {
                        if (throwable == null) {
                            result.set(callResult);
                            callLatch.countDown();
                        }
                    });
        });

        Client callerClient = new Client(callerSession, getWampUrl(), getRealm());
        callerClient.connect();

        // Wait for call result
        assertTrue("Call should complete", callLatch.await(5, TimeUnit.SECONDS));

        CallResult callResult = result.get();
        assertNotNull("Call result should not be null", callResult);
        assertThat(callResult.results).containsExactly(8);

        // Cleanup
        calleeSession.leave();
        callerSession.leave();
    }

    @Test
    public void testCallWithMultipleArguments() throws Exception {
        String procedure = "com.test.multiply";
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<CallResult> result = new AtomicReference<>();

        // Register procedure
        Session calleeSession = new Session();
        calleeSession.addOnJoinListener((session, details) -> {
            session.register(procedure, RpcIntegrationTest::multiplyHandler);
        });

        Client calleeClient = new Client(calleeSession, getWampUrl(), getRealm());
        calleeClient.connect();
        Thread.sleep(1000);

        // Call procedure
        Session callerSession = new Session();
        callerSession.addOnJoinListener((session, details) -> {
            session.call(procedure, 2, 3, 4)
                    .whenComplete((callResult, throwable) -> {
                        result.set(callResult);
                        latch.countDown();
                    });
        });

        Client callerClient = new Client(callerSession, getWampUrl(), getRealm());
        callerClient.connect();

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertThat(result.get().results).containsExactly(24);

        calleeSession.leave();
        callerSession.leave();
    }

    @Test
    public void testCallNonExistentProcedure() throws Exception {
        String procedure = "com.test.doesnotexist";
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();

        Session session = new Session();
        session.addOnJoinListener((s, details) -> {
            s.call(procedure, "arg")
                    .whenComplete((callResult, throwable) -> {
                        error.set(throwable);
                        latch.countDown();
                    });
        });

        Client client = new Client(session, getWampUrl(), getRealm());
        client.connect();

        assertTrue("Call should complete (with error)", latch.await(5, TimeUnit.SECONDS));
        assertNotNull("Should receive an error", error.get());

        session.leave();
    }

    @Test
    public void testMultipleConcurrentCalls() throws Exception {
        String procedure = "com.test.echo";
        int numCalls = 5;
        CountDownLatch registrationLatch = new CountDownLatch(1);
        CountDownLatch callsLatch = new CountDownLatch(numCalls);

        // Register echo procedure
        Session calleeSession = new Session();
        calleeSession.addOnJoinListener((session, details) -> {
            session.register(procedure, RpcIntegrationTest::echoHandler)
                    .whenComplete((reg, throwable) -> {
                        if (throwable == null) {
                            registrationLatch.countDown();
                        }
                    });
        });

        Client calleeClient = new Client(calleeSession, getWampUrl(), getRealm());
        calleeClient.connect();
        assertTrue(registrationLatch.await(5, TimeUnit.SECONDS));

        // Make multiple concurrent calls
        Session callerSession = new Session();
        callerSession.addOnJoinListener((session, details) -> {
            for (int i = 0; i < numCalls; i++) {
                final int value = i;
                session.call(procedure, value)
                        .whenComplete((callResult, throwable) -> {
                            if (throwable == null) {
                                assertThat(callResult.results).containsExactly(value);
                                callsLatch.countDown();
                            }
                        });
            }
        });

        Client callerClient = new Client(callerSession, getWampUrl(), getRealm());
        callerClient.connect();

        assertTrue("All calls should complete", callsLatch.await(10, TimeUnit.SECONDS));

        calleeSession.leave();
        callerSession.leave();
    }
}
