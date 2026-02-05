# Autobahn|Java

A high-performance Java client library for WebSocket and WAMP (Web Application Messaging Protocol) supporting both Java 8+ and Android API 23+.

[![Maven Central](https://img.shields.io/maven-central/v/io.crossbar.autobahn/autobahn-android.svg)](https://search.maven.org/artifact/io.crossbar.autobahn/autobahn-android)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Javadoc](https://javadoc.io/badge/io.crossbar.autobahn/autobahn-android.svg)](https://javadoc.io/doc/io.crossbar.autobahn/autobahn-android)

## Overview

Autobahn|Java provides client implementations for:

- **[WebSocket Protocol (RFC 6455)](https://tools.ietf.org/html/rfc6455)** - Full-featured WebSocket client for Android
- **[Web Application Messaging Protocol (WAMP)](https://wamp-proto.org/)** - Complete WAMP v2 client implementation

### Key Features

- **Dual Platform Support**: Runs on Android (API 23+) and Java 8+ (Netty-based)
- **Modern Async API**: Built on `CompletableFuture` for WAMP operations
- **Thread-Safe**: Network operations don't block the Android main/UI thread
- **Multiple Authentication Methods**: Ticket, Challenge-Response, and Cryptosign authentication
- **Type-Safe**: Support for POJOs with Jackson serialization
- **Production-Ready**: MIT licensed, actively maintained, and battle-tested

### Architecture

- **WebSocket Layer**: Callback-based API optimized for Android threading model
- **WAMP Layer**: 
  - `CompletableFuture`-based API for asynchronous operations (call, register, publish, subscribe)
  - Observer pattern for lifecycle events (session, subscription, registration)

## Installation

### Gradle

For Android projects:
```groovy
dependencies {
    implementation 'io.crossbar.autobahn:autobahn-android:21.4.1'
}
```

For Java/Netty projects:
```groovy
dependencies {
    implementation 'io.crossbar.autobahn:autobahn-java:21.4.1'
}
```

### Maven

For Android:
```xml
<dependency>
    <groupId>io.crossbar.autobahn</groupId>
    <artifactId>autobahn-android</artifactId>
    <version>21.4.1</version>
</dependency>
```

For Java/Netty:
```xml
<dependency>
    <groupId>io.crossbar.autobahn</groupId>
    <artifactId>autobahn-java</artifactId>
    <version>21.4.1</version>
</dependency>
```

## Quick Start

### Running the Demo

The project includes demo clients that showcase all library features. Prerequisites: `make` and `docker`.

```bash
# Start Crossbar.io router
make crossbar

# Start Python WAMP components (provides test procedures)
make python

# Run Java/Netty demo client
make java
```

### Basic Usage

#### Creating a Session

```java
// Create and configure session
Session session = new Session();

// Add lifecycle listeners
session.addOnJoinListener(this::onSessionJoined);
session.addOnLeaveListener(this::onSessionLeft);

// Connect to WAMP router
Client client = new Client(session, "ws://localhost:8080/ws", "realm1");
CompletableFuture<ExitInfo> exitFuture = client.connect();
```

## WAMP Operations

### Subscribe to Topics

```java
public void onSessionJoined(Session session, SessionDetails details) {
    CompletableFuture<Subscription> future = session.subscribe(
        "com.myapp.events", 
        this::handleEvent
    );
    
    future.whenComplete((subscription, error) -> {
        if (error == null) {
            System.out.println("Subscribed to " + subscription.topic);
        } else {
            error.printStackTrace();
        }
    });
}

private void handleEvent(List<Object> args, Map<String, Object> kwargs, EventDetails details) {
    System.out.println("Event received: " + args.get(0));
}
```

### Publish to Topics

```java
// Simple publish
session.publish("com.myapp.events", "Hello World!")
    .thenAccept(pub -> System.out.println("Published"))
    .exceptionally(error -> {
        error.printStackTrace();
        return null;
    });

// Publish with multiple arguments
List<Object> args = Arrays.asList("message", 42, true);
session.publish("com.myapp.events", args);
```

### Register Procedures

```java
public void registerProcedures(Session session, SessionDetails details) {
    CompletableFuture<Registration> future = session.register(
        "com.myapp.add", 
        this::add
    );
    
    future.thenAccept(reg -> 
        System.out.println("Registered: " + reg.procedure)
    );
}

// Full signature
private CompletableFuture<InvocationResult> add(
        List<Object> args, 
        Map<String, Object> kwargs, 
        InvocationDetails details) {
    int sum = (int) args.get(0) + (int) args.get(1);
    return CompletableFuture.completedFuture(
        new InvocationResult(Arrays.asList(sum))
    );
}

// Simplified signature
private List<Object> add(List<Integer> args, InvocationDetails details) {
    int sum = args.get(0) + args.get(1);
    return Arrays.asList(sum);
}
```

### Call Procedures

```java
// Simple call
CompletableFuture<CallResult> future = session.call("com.myapp.add", 10, 20);
future.thenAccept(result -> 
    System.out.println("Result: " + result.results.get(0))
);

// Call with mixed types
byte[] data = new byte[20];
String message = "test";
int value = 99;
session.call("com.myapp.process", Arrays.asList(data, message, value))
    .thenAccept(result -> processResult(result));
```

## Authentication

Autobahn|Java supports multiple authentication methods:

### Ticket Authentication

```java
IAuthenticator auth = new TicketAuth("user@example.com", "secret-ticket");
Client client = new Client(session, url, realm, auth);
client.connect();
```

### Challenge-Response Authentication

```java
IAuthenticator auth = new ChallengeResponseAuth("user@example.com", "secret");
Client client = new Client(session, url, realm, auth);
client.connect();
```

### Cryptosign Authentication

```java
IAuthenticator auth = new CryptosignAuth(authid, privateKey, publicKey);
Client client = new Client(session, url, realm, auth);
client.connect();
```

### Multiple Authentication Methods

```java
List<IAuthenticator> authenticators = Arrays.asList(
    new TicketAuth(authid, ticket),
    new CryptosignAuth(authid, privateKey, publicKey)
);
Client client = new Client(session, url, realm, authenticators);
client.connect();
```

## Working with POJOs

Autobahn|Java supports type-safe operations with Plain Old Java Objects:

### Calling with Type Safety

```java
// Call returning a single POJO
CompletableFuture<Person> future = session.call("com.example.get_person", 1);
future.thenAccept(person -> {
    System.out.println(person.getName());
});

// Call returning a list of POJOs
CompletableFuture<List<Person>> future = session.call(
    "com.example.get_persons",
    Collections.singletonList("department-7"),
    new TypeReference<List<Person>>() {}
);

future.thenAccept(persons -> {
    persons.forEach(p -> System.out.println(p.getName()));
});
```

### Registering POJO-returning Procedures

```java
private Person getPerson(List<Object> args, InvocationDetails details) {
    String id = (String) args.get(0);
    return new Person("John", "Doe", id);
}

session.register("com.example.get_person", this::getPerson)
    .thenAccept(reg -> System.out.println("Registered: " + reg.procedure));
```

## WebSocket API (Android)

For applications needing direct WebSocket access without WAMP:

```java
WebSocketConnection connection = new WebSocketConnection();

connection.connect("wss://echo.websocket.org", new WebSocketConnectionHandler() {
    @Override
    public void onConnect(ConnectionResponse response) {
        System.out.println("Connected");
    }

    @Override
    public void onOpen() {
        connection.sendMessage("Hello WebSocket");
    }

    @Override
    public void onMessage(String payload) {
        System.out.println("Received: " + payload);
    }

    @Override
    public void onClose(int code, String reason) {
        System.out.println("Connection closed: " + reason);
    }
});
```

## Building from Source

### Android

1. Open the project in Android Studio
2. Install any missing dependencies when prompted
3. Build via `Build > Rebuild Project`
4. Output: `autobahn/build/outputs/aar/`

### Java/Netty

Prerequisites: Docker and Make

```bash
make build_autobahn
```

Output: `autobahn/build/libs/`

## Documentation

- [Testing Guide](docs/testing/README.md) - Running tests and integration tests
- [Publishing Guide](docs/publishing/README.md) - Building and publishing artifacts
- [Migration to Kotlin](docs/migration-to-kotlin/README.md) - Kotlin migration guide

## Requirements

### Android
- Minimum SDK: 23 (Android 6.0)
- Target SDK: 36
- Compile SDK: 35

### Java
- Java 8 or higher
- Netty 4.1.115+

## Dependencies

Core dependencies include:
- Jackson 2.18.2 (JSON/CBOR/MessagePack serialization)
- Netty 4.1.115 (for Java/Netty builds)
- Web3j 4.12.3 (Ethereum integration)
- Bouncy Castle (cryptography)

## Contributing

Contributions are welcome! This is a community-maintained fork of the original Crossbar.io project.

**Original Repository**: [crossbario/autobahn-java](https://github.com/crossbario/autobahn-java)

## Community

Join the discussion:
- [Crossbar.io Forum](https://crossbar.discourse.group/)

## License

Licensed under the [MIT License](LICENSE).

## Version History

See [CHANGELOG.md](CHANGELOG.md) for release history.

### Version 1 (Legacy)

The original version 1 of this library is available on the [version-1 branch](https://github.com/crossbario/autobahn-java/tree/version-1) but is no longer maintained. Version 1 had the following limitations:
- Only supported non-secure WebSocket on Android
- Only supported WAMP v1

The current version addresses both of these limitations and is recommended for all new projects.

## Acknowledgments

This library is part of the [Autobahn project](http://crossbar.io/autobahn/) family, which provides WebSocket and WAMP implementations across multiple programming languages and platforms.
