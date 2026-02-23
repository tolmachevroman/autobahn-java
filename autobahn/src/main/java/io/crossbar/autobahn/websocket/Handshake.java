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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import io.crossbar.autobahn.utils.AuthUtil;
import io.crossbar.autobahn.websocket.exceptions.ParseFailed;
import io.crossbar.autobahn.websocket.messages.ClientHandshake;

public class Handshake {

    private final static String CRLF = "\r\n";

    private static byte[] bytes(String text) throws IOException {
        return text.getBytes("UTF-8");
    }

    private static String newHandshakeKey() throws Exception {
        final byte[] ba = new byte[16];
        new Random().nextBytes(ba);
        return AuthUtil.encodeToString(ba);
    }

    public static byte[] handshake(ClientHandshake message) throws ParseFailed {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        // write HTTP header with handshake
        String path;
        if (message.query != null) {
            path = message.path + "?" + message.query;
        } else {
            path = message.path;
        }
        try {
            buffer.write(bytes(String.format("GET %s HTTP/1.1", path)));
            buffer.write(bytes(CRLF));
            buffer.write(bytes("Host: " + message.host));
            buffer.write(bytes(CRLF));
            buffer.write(bytes("Upgrade: WebSocket"));
            buffer.write(bytes(CRLF));
            buffer.write(bytes("Connection: Upgrade"));
            buffer.write(bytes(CRLF));
            buffer.write(bytes("Sec-WebSocket-Key: " + newHandshakeKey()));
            buffer.write(bytes(CRLF));
            if (message.origin != null && !message.origin.equals("")) {
                buffer.write(bytes("Origin: " + message.origin));
                buffer.write(bytes(CRLF));
            }

            if (message.subprotocols != null && message.subprotocols.length > 0) {
                buffer.write(bytes("Sec-WebSocket-Protocol: "));
                for (int i = 0; i < message.subprotocols.length; ++i) {
                    buffer.write(bytes(message.subprotocols[i]));
                    if (i != message.subprotocols.length - 1) {
                        buffer.write(bytes(", "));
                    }
                }
                buffer.write(bytes(CRLF));
            }

            buffer.write(bytes("Sec-WebSocket-Version: 13"));
            buffer.write(bytes(CRLF));

            // Header injection
            if (message.headerList != null) {
                for (String key : message.headerList.keySet()) {
                    buffer.write(bytes(key + ":" + message.headerList.get(key)));
                    buffer.write(bytes(CRLF));
                }
            }
            buffer.write(bytes(CRLF));
        } catch (Exception e) {
            throw new ParseFailed(e.getMessage());
        }
        return buffer.toByteArray();
    }
}
