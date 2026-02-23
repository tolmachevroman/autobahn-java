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

package io.crossbar.autobahn.wamp.auth;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import io.crossbar.autobahn.wamp.types.Challenge;
import io.crossbar.autobahn.wamp.types.ChallengeResponse;

import static org.junit.Assert.*;

public class TicketAuthTest {

    private static final String TEST_AUTHID = "user123";
    private static final String TEST_TICKET = "secret_ticket_123";

    @Test
    public void testConstructor() {
        TicketAuth auth = new TicketAuth(TEST_AUTHID, TEST_TICKET);
        
        assertNotNull(auth);
        assertEquals("ticket", auth.getAuthMethod());
        assertEquals(TEST_AUTHID, auth.authid);
    }

    @Test
    public void testOnChallengeReturnsTicket() throws Exception {
        TicketAuth auth = new TicketAuth(TEST_AUTHID, TEST_TICKET);
        
        Map<String, Object> extra = new HashMap<>();
        Challenge challenge = new Challenge("ticket", extra);
        
        ChallengeResponse response = auth.onChallenge(null, challenge).join();
        
        assertNotNull(response);
        assertEquals(TEST_TICKET, response.signature);
    }

    @Test
    public void testOnChallengeIgnoresExtra() throws Exception {
        TicketAuth auth = new TicketAuth(TEST_AUTHID, TEST_TICKET);
        
        Map<String, Object> extra = new HashMap<>();
        extra.put("random", "data");
        extra.put("ignored", 123);
        Challenge challenge = new Challenge("ticket", extra);
        
        ChallengeResponse response = auth.onChallenge(null, challenge).join();
        
        // Should still return the ticket regardless of extra data
        assertEquals(TEST_TICKET, response.signature);
    }

    @Test
    public void testGetAuthMethod() {
        TicketAuth auth = new TicketAuth(TEST_AUTHID, TEST_TICKET);
        assertEquals("ticket", auth.getAuthMethod());
    }

    @Test
    public void testEmptyTicket() throws Exception {
        TicketAuth auth = new TicketAuth(TEST_AUTHID, "");
        
        Map<String, Object> extra = new HashMap<>();
        Challenge challenge = new Challenge("ticket", extra);
        
        ChallengeResponse response = auth.onChallenge(null, challenge).join();
        
        assertEquals("", response.signature);
    }

    @Test
    public void testNullTicketHandled() throws Exception {
        // TicketAuth constructor doesn't allow null, but let's test with empty
        TicketAuth auth = new TicketAuth(TEST_AUTHID, "");
        
        Map<String, Object> extra = new HashMap<>();
        Challenge challenge = new Challenge("ticket", extra);
        
        ChallengeResponse response = auth.onChallenge(null, challenge).join();
        assertNotNull(response);
    }
}
