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

public class AnonymousAuthTest {

    @Test
    public void testConstructor() {
        AnonymousAuth auth = new AnonymousAuth();
        
        assertNotNull(auth);
        assertEquals("anonymous", auth.getAuthMethod());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOnChallengeThrowsException() throws Exception {
        AnonymousAuth auth = new AnonymousAuth();
        
        Map<String, Object> extra = new HashMap<>();
        Challenge challenge = new Challenge("anonymous", extra);
        
        // Anonymous auth does not support challenges
        auth.onChallenge(null, challenge).join();
    }

    @Test
    public void testGetAuthMethod() {
        AnonymousAuth auth = new AnonymousAuth();
        assertEquals("anonymous", auth.getAuthMethod());
    }

    @Test
    public void testMultipleInstances() {
        AnonymousAuth auth1 = new AnonymousAuth();
        AnonymousAuth auth2 = new AnonymousAuth();
        
        assertEquals(auth1.getAuthMethod(), auth2.getAuthMethod());
    }
}
