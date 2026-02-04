# Testing Guide

This document describes the testing strategy and how to run tests for autobahn-java.

## Test Structure

We have two types of tests:

### 1. Unit Tests
Fast, isolated tests that verify individual components without external dependencies.

**Location**: `autobahn/src/test/java/**/*Test.java`

**What we test**:
- WAMP message creation and serialization
- Authentication mechanisms
- URI pattern matching
- Error handling
- Crypto operations

**How to run**:
```bash
./gradlew :autobahn:test
```

### 2. Integration Tests
End-to-end tests that verify WAMP protocol compliance against a real Crossbar.io router.

**Location**: `autobahn/src/test/java/**/integration/**Test.java`

**What we test**:
- Pub/Sub patterns (publish, subscribe, pattern subscriptions)
- RPC patterns (call, register, progressive results)
- Session lifecycle (connect, join, leave)
- Authentication flows
- Multi-client scenarios
- Error handling and edge cases

**How to run**:
```bash
# Requires Docker
./gradlew :autobahn:test --tests "*IntegrationTest"
```

## Running Tests

### All Tests
```bash
./gradlew test
```

### Unit Tests Only
```bash
./gradlew test --tests "*Test" --tests "!*IntegrationTest"
```

### Integration Tests Only
```bash
# Requires Docker to be running
./gradlew test --tests "*IntegrationTest"
```

### Specific Test Class
```bash
./gradlew test --tests "io.crossbar.autobahn.wamp.integration.PubSubIntegrationTest"
```

### Specific Test Method
```bash
./gradlew test --tests "PubSubIntegrationTest.testPublishSubscribeBasic"
```

## Integration Test Architecture

Integration tests use [Testcontainers](https://testcontainers.com/) to automatically:
1. Start a Crossbar.io router in Docker
2. Run tests against it
3. Clean up automatically

```
┌─────────────────────────────────────┐
│  Test Environment (Testcontainers)  │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ Crossbar.io Router (Docker) │   │
│  │   ws://localhost:XXXX/ws    │   │
│  └────────────┬────────────────┘   │
│               │                     │
│        ┌──────┴──────┐             │
│        │             │             │
│   ┌────▼────┐   ┌───▼─────┐       │
│   │ Client1 │   │ Client2 │       │
│   │(Session)│   │(Session)│       │
│   └─────────┘   └─────────┘       │
│                                     │
└─────────────────────────────────────┘
```

## Requirements

### Unit Tests
- JDK 17+
- No external dependencies

### Integration Tests
- JDK 17+
- Docker installed and running
- Internet connection (to pull Crossbar.io image)

## CI/CD

Tests run automatically on:
- Every push to `master`
- Every pull request

**GitHub Actions workflow**: `.github/workflows/main.yml`

The CI runs:
1. Unit tests (fast)
2. Build verification
3. Integration tests (slower, with Docker)

## Writing New Tests

### Unit Test Template
```java
package io.crossbar.autobahn.wamp;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MyFeatureTest {
    
    @Test
    public void testSomething() {
        // Arrange
        MyClass obj = new MyClass();
        
        // Act
        String result = obj.doSomething();
        
        // Assert
        assertThat(result).isEqualTo("expected");
    }
}
```

### Integration Test Template
```java
package io.crossbar.autobahn.wamp.integration;

import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MyIntegrationTest extends WampIntegrationTestBase {
    
    @Test
    public void testWampFeature() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Session session = new Session();
        session.addOnJoinListener((s, details) -> {
            // Test WAMP operations
            latch.countDown();
        });
        
        Client client = new Client(session, getWampUrl(), getRealm());
        client.connect();
        
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        session.leave();
    }
}
```

## Test Coverage

Current coverage includes:
- ✅ Basic Pub/Sub
- ✅ Basic RPC (call/register)
- ✅ WAMP message types
- ✅ Cryptosign authentication
- ⏳ Pattern-based subscriptions (TODO)
- ⏳ Progressive call results (TODO)
- ⏳ Session management edge cases (TODO)
- ⏳ Authentication methods (ticket, cookie) (TODO)
- ⏳ Large payload handling (TODO)

## Troubleshooting

### Docker not starting
```bash
# Check Docker is running
docker ps

# Pull Crossbar.io image manually
docker pull crossbario/crossbar:latest
```

### Tests timing out
- Increase timeout in test: `latch.await(10, TimeUnit.SECONDS)`
- Check Docker resources (CPU/Memory)
- Check network connectivity

### Port conflicts
Testcontainers uses random ports, but if you see conflicts:
```bash
# Check what's using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

## Best Practices

1. **Keep unit tests fast** - Mock external dependencies
2. **Integration tests should be reliable** - Use proper timeouts and cleanup
3. **Test behavior, not implementation** - Focus on WAMP protocol compliance
4. **Use descriptive test names** - `testPublishWithAcknowledge` not `test1`
5. **Clean up resources** - Always call `session.leave()` in finally blocks
6. **Use AssertJ assertions** - More readable than JUnit assertions

## Resources

- [WAMP Specification](https://wamp-proto.org/spec)
- [Testcontainers Documentation](https://testcontainers.com/)
- [Crossbar.io Documentation](https://crossbar.io/docs/)
