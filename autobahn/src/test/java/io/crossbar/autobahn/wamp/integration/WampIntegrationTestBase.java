package io.crossbar.autobahn.wamp.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * Base class for WAMP integration tests using Testcontainers.
 * Automatically spins up a Crossbar.io router for testing.
 */
public abstract class WampIntegrationTestBase {

    protected static final int CROSSBAR_PORT = 8080;

    @ClassRule
    public static GenericContainer<?> crossbar = new GenericContainer<>("crossbario/crossbar:latest")
            .withExposedPorts(CROSSBAR_PORT)
            .waitingFor(Wait.forHttp("/")
                    .forPort(CROSSBAR_PORT)
                    .forStatusCode(200));

    protected String getWampUrl() {
        return String.format("ws://%s:%d/ws",
                crossbar.getHost(),
                crossbar.getMappedPort(CROSSBAR_PORT));
    }

    protected String getRealm() {
        return "realm1";
    }

    @Before
    public void setUp() {
        // Override in subclasses if needed
    }

    @After
    public void tearDown() {
        // Override in subclasses if needed
    }
}
