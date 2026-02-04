package io.crossbar.autobahn.wamp.types;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

public class RegistrationTest {

    @Test
    public void testRegistrationCreation() {
        long registrationId = 123456L;
        String procedure = "com.example.add2";
        Object endpoint = "handler";

        Registration registration = new Registration(
                registrationId, procedure, endpoint, null
        );

        assertNotNull(registration);
        assertEquals(registrationId, registration.registration);
        assertEquals(procedure, registration.procedure);
        assertEquals(endpoint, registration.endpoint);
        assertNull(registration.session);
    }

    @Test
    public void testRegistrationIsActiveByDefault() {
        Registration registration = new Registration(
                1L, "proc", "handler", null
        );

        assertTrue("Registration should be active by default", registration.isActive());
    }

    @Test
    public void testRegistrationSetInactive() {
        Registration registration = new Registration(
                1L, "proc", "handler", null
        );

        assertTrue(registration.isActive());
        
        registration.setInactive();
        
        assertFalse("Registration should be inactive after setInactive()", 
                registration.isActive());
    }

    @Test
    public void testRegistrationSetInactiveTwiceThrowsException() {
        Registration registration = new Registration(
                1L, "proc", "handler", null
        );

        registration.setInactive();
        
        assertThatThrownBy(() -> registration.setInactive())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already invactive");
    }

    @Test
    public void testRegistrationWithDifferentEndpointTypes() {
        // Test with String endpoint
        Registration reg1 = new Registration(1L, "proc1", "handler1", null);
        assertEquals("handler1", reg1.endpoint);
        
        // Test with different object type
        Integer intEndpoint = 42;
        Registration reg2 = new Registration(2L, "proc2", intEndpoint, null);
        assertEquals(intEndpoint, reg2.endpoint);
    }

    @Test
    public void testRegistrationFieldsArePublic() {
        Registration registration = new Registration(
                999L, "test.procedure", "endpoint", null
        );

        // Verify all fields are accessible without getters
        assertEquals(999L, registration.registration);
        assertEquals("test.procedure", registration.procedure);
        assertNotNull(registration.endpoint);
    }
}
