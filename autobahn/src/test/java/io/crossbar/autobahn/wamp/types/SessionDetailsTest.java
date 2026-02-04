package io.crossbar.autobahn.wamp.types;

import org.junit.Test;

import static org.junit.Assert.*;

public class SessionDetailsTest {

    @Test
    public void testSessionDetailsCreation() {
        String realm = "realm1";
        long sessionID = 123456789L;
        String authid = "user@example.com";
        String authrole = "backend";
        String authmethod = "ticket";

        SessionDetails details = new SessionDetails(
                realm, sessionID, authid, authrole, authmethod
        );

        assertNotNull(details);
        assertEquals(realm, details.realm);
        assertEquals(sessionID, details.sessionID);
        assertEquals(authid, details.authid);
        assertEquals(authrole, details.authrole);
        assertEquals(authmethod, details.authmethod);
    }

    @Test
    public void testSessionDetailsWithNullValues() {
        String realm = "test_realm";
        long sessionID = 999L;

        SessionDetails details = new SessionDetails(
                realm, sessionID, null, null, null
        );

        assertNotNull(details);
        assertEquals(realm, details.realm);
        assertEquals(sessionID, details.sessionID);
        assertNull(details.authid);
        assertNull(details.authrole);
        assertNull(details.authmethod);
    }

    @Test
    public void testSessionDetailsFieldsArePublic() {
        SessionDetails details = new SessionDetails(
                "realm", 1L, "user", "admin", "cryptosign"
        );

        // Verify all fields are accessible
        assertEquals("realm", details.realm);
        assertEquals(1L, details.sessionID);
        assertEquals("user", details.authid);
        assertEquals("admin", details.authrole);
        assertEquals("cryptosign", details.authmethod);
    }

    @Test
    public void testSessionDetailsWithLargeSessionID() {
        long largeID = Long.MAX_VALUE;
        
        SessionDetails details = new SessionDetails(
                "realm", largeID, "user", "role", "method"
        );

        assertEquals(largeID, details.sessionID);
    }
}
