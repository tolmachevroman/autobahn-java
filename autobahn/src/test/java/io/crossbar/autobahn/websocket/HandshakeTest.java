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

package io.crossbar.autobahn.websocket;

import org.junit.Test;

import io.crossbar.autobahn.websocket.exceptions.ParseFailed;
import io.crossbar.autobahn.websocket.messages.ClientHandshake;

import static org.junit.Assert.*;

public class HandshakeTest {

    @Test
    public void testHandshakeBasic() throws ParseFailed {
        ClientHandshake handshake = new ClientHandshake("example.com");
        handshake.path = "/ws";

        byte[] data = Handshake.handshake(handshake);
        String request = new String(data);

        assertTrue(request.startsWith("GET /ws HTTP/1.1"));
        assertTrue(request.contains("Host: example.com"));
        assertTrue(request.contains("Upgrade: WebSocket"));
        assertTrue(request.contains("Connection: Upgrade"));
        assertTrue(request.contains("Sec-WebSocket-Key:"));
        assertTrue(request.contains("Sec-WebSocket-Version: 13"));
    }

    @Test
    public void testHandshakeWithQuery() throws ParseFailed {
        ClientHandshake handshake = new ClientHandshake("example.com");
        handshake.path = "/ws";
        handshake.query = "token=abc123";

        byte[] data = Handshake.handshake(handshake);
        String request = new String(data);

        assertTrue(request.startsWith("GET /ws?token=abc123 HTTP/1.1"));
    }

    @Test
    public void testHandshakeWithOrigin() throws ParseFailed {
        ClientHandshake handshake = new ClientHandshake("example.com");
        handshake.path = "/";
        handshake.origin = "http://localhost:8080";

        byte[] data = Handshake.handshake(handshake);
        String request = new String(data);

        assertTrue(request.contains("Origin: http://localhost:8080"));
    }

    @Test
    public void testHandshakeWithSubprotocols() throws ParseFailed {
        ClientHandshake handshake = new ClientHandshake("example.com");
        handshake.path = "/";
        handshake.subprotocols = new String[]{"wamp.2.json", "wamp.2.msgpack"};

        byte[] data = Handshake.handshake(handshake);
        String request = new String(data);

        assertTrue(request.contains("Sec-WebSocket-Protocol: wamp.2.json, wamp.2.msgpack"));
    }

    @Test
    public void testHandshakeSingleSubprotocol() throws ParseFailed {
        ClientHandshake handshake = new ClientHandshake("example.com");
        handshake.path = "/";
        handshake.subprotocols = new String[]{"wamp.2.json"};

        byte[] data = Handshake.handshake(handshake);
        String request = new String(data);

        assertTrue(request.contains("Sec-WebSocket-Protocol: wamp.2.json"));
    }

    @Test
    public void testHandshakeWithHeaderList() throws ParseFailed {
        ClientHandshake handshake = new ClientHandshake("example.com");
        handshake.path = "/";
        handshake.headerList = new java.util.HashMap<>();
        handshake.headerList.put("X-Custom-Header", "custom-value");
        handshake.headerList.put("Authorization", "Bearer token123");

        byte[] data = Handshake.handshake(handshake);
        String request = new String(data);

        assertTrue(request.contains("X-Custom-Header:custom-value"));
        assertTrue(request.contains("Authorization:Bearer token123"));
    }

    @Test
    public void testHandshakeEndsWithCRLFCRLF() throws ParseFailed {
        ClientHandshake handshake = new ClientHandshake("example.com");
        handshake.path = "/";

        byte[] data = Handshake.handshake(handshake);
        String request = new String(data);

        assertTrue(request.endsWith("\r\n\r\n"));
    }

    @Test
    public void testHandshakeWithAllOptions() throws ParseFailed {
        ClientHandshake handshake = new ClientHandshake("ws.example.com");
        handshake.path = "/wamp";
        handshake.query = "realm=default";
        handshake.origin = "https://app.example.com";
        handshake.subprotocols = new String[]{"wamp.2.json"};
        handshake.headerList = new java.util.HashMap<>();
        handshake.headerList.put("X-Auth-Token", "secret123");

        byte[] data = Handshake.handshake(handshake);
        String request = new String(data);

        assertTrue(request.startsWith("GET /wamp?realm=default HTTP/1.1"));
        assertTrue(request.contains("Host: ws.example.com"));
        assertTrue(request.contains("Origin: https://app.example.com"));
        assertTrue(request.contains("Sec-WebSocket-Protocol: wamp.2.json"));
        assertTrue(request.contains("X-Auth-Token:secret123"));
    }

    @Test
    public void testSecWebSocketKeyIsPresent() throws ParseFailed {
        ClientHandshake handshake = new ClientHandshake("example.com");
        handshake.path = "/";

        byte[] data = Handshake.handshake(handshake);
        String request = new String(data);

        // Key should be 24 characters (16 bytes base64 encoded)
        int keyIndex = request.indexOf("Sec-WebSocket-Key: ");
        assertTrue(keyIndex > 0);
        String key = request.substring(keyIndex + 19, keyIndex + 43);
        assertEquals(24, key.length());
    }
}
