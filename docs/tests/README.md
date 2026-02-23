# Autobahn Java Test Coverage

This document tracks the test coverage for the Autobahn Java WAMP library and identifies areas needing additional testing.

**Last Updated:** February 23, 2026  
**Current Status:** 225 tests passing across 26 test files (100% pass rate)

---

## Running Tests

### Run all tests:
```bash
./gradlew :autobahn:test
```

### Run with coverage (JaCoCo):
```bash
./gradlew :autobahn:test :autobahn:jacocoTestReport
```

### View coverage report:
- HTML: `autobahn/build/reports/jacoco/jacocoTestReport/html/index.html`
- XML: `autobahn/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

---

## Current Coverage Summary

| Component | Test Files | Tests | Status |
|-----------|-----------|-------|--------|
| **WAMP Authentication** | 3 | 18 | ✅ Good |
| **WAMP Messages** | 13 | 102 | ✅ Excellent |
| **WAMP Types** | 5 | 29 | ✅ Good |
| **WAMP Utilities** | 2 | 17 | ✅ Good |
| **WebSocket Protocol** | 3 | 34 | ✅ Good |
| **WebSocket Types** | 1 | 16 | ✅ Good |
| **TOTAL** | **26** | **225** | **-** |

---

## Detailed Coverage by Component

### ✅ WAMP Authentication (18 tests)

| Class | Tests | Coverage |
|-------|-------|----------|
| `AnonymousAuth` | 4 | Constructor, onChallenge (throws), getAuthMethod |
| `TicketAuth` | 6 | Constructor, onChallenge returns ticket, getAuthMethod |
| `CryptosignAuth` | 8 | Key pair generation, public key derivation, challenge signing |

**Missing:**
- [ ] Challenge response edge cases
- [ ] Invalid key handling

---

### ⚠️ WAMP Messages (22 tests - PARTIAL)

| Message | Tests | Marshal | Parse | Edge Cases |
|---------|-------|---------|-------|------------|
| `Hello` | 4 | ✅ | ✅ | Basic + auth options |
| `Call` | 6 | ✅ | ✅ | Args, kwargs, timeout |
| `Publish` | 9 | ✅ | ✅ | Options, acknowledge, retain |
| `Welcome` | 2 | ❌ | ✅ | Server-side only |

**Missing (HIGH PRIORITY):**

| Message | Priority | Notes |
|---------|----------|-------|
| `Subscribe` / `Subscribed` | 🔴 High | Core pub/sub flow |
| `Unsubscribe` / `Unsubscribed` | 🔴 High | Cleanup flow |
| `Event` | 🔴 High | Event delivery handling |
| `Register` / `Registered` | 🔴 High | Core RPC flow |
| `Unregister` / `Unregistered` | 🟡 Medium | Cleanup flow |
| `Invocation` | 🔴 High | RPC request handling |
| `Yield` | 🟡 Medium | RPC response |
| `Result` | 🟡 Medium | Already tested via Call, needs dedicated tests |
| `Error` | 🔴 High | Error handling for all message types |
| `Goodbye` / `Abort` | 🟡 Medium | Session termination |
| `Challenge` / `Authenticate` | 🟡 Medium | Auth flow (partially covered in auth tests) |
| `Published` | 🟢 Low | Acknowledgment |
| `Cancel` / `Interrupt` | 🟡 Medium | Call cancellation |

---

### ✅ WAMP Types (29 tests)

| Class | Tests | Coverage |
|-------|-------|----------|
| `Subscription` | 6 | Creation, active state, setInactive |
| `Registration` | 6 | Similar to Subscription |
| `SessionDetails` | 4 | Session info tracking |
| `PublishOptions` | 6 | Options builder pattern |
| `CallResult` | 7 | Result handling with args/kwargs |

---

### ✅ WAMP Utilities (17 tests)

| Class | Tests | Coverage |
|-------|-------|----------|
| `MessageUtil` | 11 | Message validation, parseLong |
| `IDGenerator` | 6 | ID generation, wrap-around, uniqueness |

---

### ✅ WebSocket Protocol (34 tests)

| Class | Tests | Coverage |
|-------|-------|----------|
| `FrameProtocol` | 12 | Ping, pong, close, text, binary frames |
| `Handshake` | 9 | HTTP handshake generation with options |
| `Utf8Validator` | 13 | Validation, state machine, reset |

---

### ✅ WebSocket Types (16 tests)

| Class | Tests | Coverage |
|-------|-------|----------|
| `WebSocketOptions` | 16 | All getters/setters, copy constructor, synchronization |

---

## Missing Coverage Areas (Priority Order)

### 🔴 HIGH PRIORITY

1. **Error Message Handling**
   - Error message parsing for all WAMP operations
   - Error URI extraction
   - Error details handling
   - ApplicationError exception mapping

2. **Core WAMP Message Pairs**
   - `Subscribe` / `Subscribed` flow
   - `Register` / `Registered` flow
   - `Event` message handling
   - `Invocation` / `Yield` flow

3. **Session Lifecycle**
   - Session state transitions
   - Connection recovery
   - Goodbye handshake
   - Transport failure handling

### 🟡 MEDIUM PRIORITY

4. **Advanced RPC Features**
   - Progressive results
   - Call cancellation (`Cancel` / `Interrupt`)
   - Call timeouts
   - Advanced invoke modes (roundrobin, random, etc.)

5. **Advanced Pub/Sub**
   - Subscription matching options
   - Event details extraction
   - Publication acknowledgment flow

6. **Serializers**
   - `CBORSerializer` serialization/deserialization
   - `MessagePackSerializer` serialization/deserialization
   - Cross-serializer compatibility

### 🟢 LOW PRIORITY

7. **Reflection Roles**
   - Dynamic proxy generation tests
   - Method invocation handler
   - Argument unpacking

8. **Transport Layer**
   - `NettyWebSocket` integration tests (requires Netty)
   - `AndroidWebSocket` integration tests (requires Android)
   - `WebSocketConnection` integration flow

9. **Integration Tests**
   - Full WAMP session flow
   - Crossbar.io compatibility
   - Performance benchmarks

---

## Test File Organization

```
autobahn/src/test/java/io/crossbar/autobahn/
├── wamp/
│   ├── auth/
│   │   ├── AnonymousAuthTest.java ✅
│   │   ├── CryptosignAuthTests.java ✅
│   │   └── TicketAuthTest.java ✅
│   ├── messages/
│   │   ├── CallMessageTest.java ✅
│   │   ├── HelloMessageTest.java ✅
│   │   ├── PublishMessageTest.java ✅
│   │   └── WelcomeMessageTest.java ✅
│   │   # MISSING: SubscribeTest, RegisterTest, EventTest, ErrorTest, etc.
│   ├── types/
│   │   ├── CallResultTest.java ✅
│   │   ├── PublishOptionsTest.java ✅
│   │   ├── RegistrationTest.java ✅
│   │   ├── SessionDetailsTest.java ✅
│   │   └── SubscriptionTest.java ✅
│   └── utils/
│       ├── IDGeneratorTest.java ✅
│       └── MessageUtilTest.java ✅
├── websocket/
│   ├── FrameProtocolTest.java ✅
│   ├── HandshakeTest.java ✅
│   └── types/
│       └── WebSocketOptionsTest.java ✅
└── websocket/utils/
    └── Utf8ValidatorTest.java ✅
```

---

## Recommended Next Steps

### Immediate (Next Sprint)
1. Add `ErrorMessageTest` - covers error handling for all operations
2. Add `SubscribeMessageTest` and `EventMessageTest` - core pub/sub
3. Add `RegisterMessageTest` and `InvocationMessageTest` - core RPC

### Short-term (Next 2-4 Weeks)
4. Add session lifecycle tests with mock transport
5. Add `YieldMessageTest` and `ResultMessageTest`
6. Add `GoodbyeMessageTest` and `AbortMessageTest`
7. Add CBOR and MessagePack serializer tests

### Long-term
8. Integration tests with testcontainers
9. Performance benchmarks
10. Stress tests for reconnection scenarios

---

## Testing Guidelines

### For New Tests:
1. **Test both marshal and parse** for message classes
2. **Test edge cases**: null values, empty collections, boundary values
3. **Test error scenarios**: invalid message formats, protocol violations
4. **Use descriptive test names**: `test<Method><Scenario>`
5. **Group related tests** in same test class

### Test Template for WAMP Messages:
```java
@Test
public void test<Message>MarshalBasic() {
    // Test basic marshal with required fields
}

@Test
public void test<Message>MarshalWithOptions() {
    // Test marshal with optional fields
}

@Test
public void test<Message>Parse() {
    // Test basic parse
}

@Test
public void test<Message>ParseFull() {
    // Test parse with all fields
}

@Test(expected = ProtocolError.class)
public void test<Message>ParseInvalidLength() {
    // Test error handling
}
```

---

## Notes

- Current tests use JUnit 4 with AssertJ for assertions
- Tests run on Android library module (requires Android SDK)
- Some tests require JVM-only environment (see autobahn-kotlin-ext for pure Kotlin tests)
- Test coverage tool: JaCoCo (can be integrated with Gradle)

