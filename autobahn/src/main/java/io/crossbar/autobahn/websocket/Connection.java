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

import java.io.UnsupportedEncodingException;

import io.crossbar.autobahn.websocket.exceptions.ParseFailed;
import io.crossbar.autobahn.websocket.messages.BinaryMessage;
import io.crossbar.autobahn.websocket.messages.ClientHandshake;
import io.crossbar.autobahn.websocket.messages.Close;
import io.crossbar.autobahn.websocket.messages.Message;
import io.crossbar.autobahn.websocket.messages.Ping;
import io.crossbar.autobahn.websocket.messages.Pong;
import io.crossbar.autobahn.websocket.messages.RawTextMessage;
import io.crossbar.autobahn.websocket.messages.TextMessage;
import io.crossbar.autobahn.websocket.types.WebSocketOptions;

public class Connection {

    private FrameProtocol protocol;
    private WebSocketOptions options;

    public Connection(WebSocketOptions options) {
        this.options = options;
        this.protocol = new FrameProtocol();
    }

    private byte[] sendText(String payload) throws ParseFailed {
        byte[] payloadBytes;
        try {
            payloadBytes = payload.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ParseFailed("payload is an invalid utf-8 string");
        }
        return sendText(payloadBytes);
    }

    private byte[] sendText(byte[] payload) throws ParseFailed {
        if (payload.length > options.getMaxMessagePayloadSize()) {
            throw new ParseFailed("message payload exceeds payload limit");
        }
        return protocol.sendText(payload);
    }

    public byte[] send(Message msg) throws ParseFailed {
        if (msg instanceof TextMessage) {
            return sendText(((TextMessage) msg).payload);
        } else if (msg instanceof RawTextMessage) {
            return sendText(((RawTextMessage) msg).payload);
        } else if (msg instanceof BinaryMessage) {
            if (((BinaryMessage) msg).payload.length > options.getMaxMessagePayloadSize()) {
                throw new ParseFailed("message payload exceeds payload limit");
            }
            return protocol.sendBinary(((BinaryMessage) msg).payload);
        } else if (msg instanceof Ping) {
            return protocol.ping(((Ping) msg).payload);
        } else if (msg instanceof Pong) {
            return protocol.pong(((Pong) msg).payload);
        } else if (msg instanceof Close) {
            return protocol.close(((Close) msg).code, ((Close) msg).reason);
        } else if (msg instanceof ClientHandshake) {
            return Handshake.handshake((ClientHandshake) msg);
        } else {
            throw new ParseFailed("unknown message received by WebSocketWriter");
        }
    }
}
