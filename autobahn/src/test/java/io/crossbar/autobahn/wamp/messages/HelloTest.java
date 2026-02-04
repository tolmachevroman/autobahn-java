package io.crossbar.autobahn.wamp.messages;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HelloTest {

    @Test
    public void testHelloMessageCreation() {
        String realm = "realm1";
        Map<String, Map> roles = new HashMap<>();
        roles.put("publisher", new HashMap<>());
        roles.put("subscriber", new HashMap<>());
        roles.put("caller", new HashMap<>());
        roles.put("callee", new HashMap<>());

        Hello hello = new Hello(realm, roles);

        assertNotNull(hello);
        assertEquals(realm, hello.realm);
        assertEquals(roles, hello.roles);
        assertEquals(Hello.MESSAGE_TYPE, hello.getMessageType());
    }

    @Test
    public void testHelloMessageType() {
        Map<String, Map> roles = new HashMap<>();
        Hello hello = new Hello("realm1", roles);
        assertEquals(Hello.MESSAGE_TYPE, hello.getMessageType());
        assertEquals(1, Hello.MESSAGE_TYPE);
    }

    @Test
    public void testHelloWithAuthId() {
        Map<String, Map> roles = new HashMap<>();
        roles.put("publisher", new HashMap<>());
        
        Hello hello = new Hello("realm1", roles, Arrays.asList("ticket", "cryptosign"), 
                "user123", "backend", null);

        assertThat(hello.authID).isEqualTo("user123");
        assertThat(hello.authMethods).containsExactly("ticket", "cryptosign");
        assertThat(hello.authRole).isEqualTo("backend");
    }

    @Test
    public void testHelloWithAuthextra() {
        Map<String, Map> roles = new HashMap<>();
        Map<String, Object> authextra = new HashMap<>();
        authextra.put("key1", "value1");
        authextra.put("key2", 42);

        Hello hello = new Hello("realm1", roles, null, null, null, authextra);

        assertThat(hello.authextra).containsEntry("key1", "value1");
        assertThat(hello.authextra).containsEntry("key2", 42);
    }
}
