package io.crossbar.autobahn.wamp.messages;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HelloTest {

    @Test
    public void testHelloMessageCreation() {
        String realm = "realm1";
        Map<String, Object> details = new HashMap<>();
        details.put("roles", new HashMap<String, Object>() {{
            put("publisher", new HashMap<>());
            put("subscriber", new HashMap<>());
            put("caller", new HashMap<>());
            put("callee", new HashMap<>());
        }});

        Hello hello = new Hello(realm, details);

        assertNotNull(hello);
        assertEquals(realm, hello.realm);
        assertEquals(details, hello.details);
    }

    @Test
    public void testHelloMessageType() {
        Hello hello = new Hello("realm1", new HashMap<>());
        assertEquals(Hello.MESSAGE_TYPE, hello.getMessageType());
    }

    @Test
    public void testHelloWithAuthId() {
        Map<String, Object> details = new HashMap<>();
        details.put("authid", "user123");
        details.put("authmethods", new String[]{"ticket", "cryptosign"});

        Hello hello = new Hello("realm1", details);

        assertThat(details).containsEntry("authid", "user123");
        assertThat(details).containsKey("authmethods");
    }
}
