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
- **Kotlin First**: 80% of the library is now Kotlin with full Java interoperability
- **Coroutine Support**: Optional `autobahn-kotlin-ext` module provides Kotlin coroutines and Flow APIs
- **Jetpack Compose Ready**: Easy integration with Android's modern UI toolkit
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

### GitHub Packages

This fork is published to GitHub Packages. To use it, you need to configure authentication:

#### Setup Credentials

Add to your global `~/.gradle/gradle.properties`:
```properties
GPR_USER=your-github-username
GPR_KEY=ghp_your-classic-github-token
```

Create a classic GitHub token with `read:packages` scope at: [GitHub Settings > Developer settings > Personal access tokens](https://github.com/settings/tokens)

#### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/tolmachevroman/autobahn-java")
        credentials {
            username = project.findProperty("GPR_USER") as? String ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("GPR_KEY") as? String ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    // For Android projects
    implementation("io.crossbar.autobahn:autobahn-android:22.2")
    
    // For Java/Netty projects
    implementation("io.crossbar.autobahn:autobahn-java:22.2")
    
    // Optional: Kotlin coroutines extension
    // For Android:
    implementation("io.crossbar.autobahn:autobahn-kotlin-ext-android:22.2")
    // For Netty/JVM:
    implementation("io.crossbar.autobahn:autobahn-kotlin-ext-netty:22.2")
}
```

#### Gradle (Groovy)

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/tolmachevroman/autobahn-java")
        credentials {
            username = project.findProperty("GPR_USER") ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("GPR_KEY") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    // For Android projects
    implementation 'io.crossbar.autobahn:autobahn-android:22.2'
    
    // For Java/Netty projects
    implementation 'io.crossbar.autobahn:autobahn-java:22.2'
    
    // Optional: Kotlin coroutines extension
    // For Android:
    implementation 'io.crossbar.autobahn:autobahn-kotlin-ext-android:22.2'
    // For Netty/JVM:
    implementation 'io.crossbar.autobahn:autobahn-kotlin-ext-netty:22.2'
}
```

#### Maven

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/romantolmachev/autobahn-java</url>
    </repository>
</repositories>

<dependencies>
    <!-- For Android projects -->
    <dependency>
        <groupId>io.crossbar.autobahn</groupId>
        <artifactId>autobahn-android</artifactId>
        <version>22.2</version>
    </dependency>
    
    <!-- For Java/Netty projects -->
    <dependency>
        <groupId>io.crossbar.autobahn</groupId>
        <artifactId>autobahn-java</artifactId>
        <version>22.2</version>
    </dependency>
    
    <!-- Optional: Kotlin coroutines extension (Android) -->
    <dependency>
        <groupId>io.crossbar.autobahn</groupId>
        <artifactId>autobahn-kotlin-ext-android</artifactId>
        <version>22.2</version>
    </dependency>
    
    <!-- Optional: Kotlin coroutines extension (Netty) -->
    <dependency>
        <groupId>io.crossbar.autobahn</groupId>
        <artifactId>autobahn-kotlin-ext-netty</artifactId>
        <version>22.2</version>
    </dependency>
</dependencies>
```

Add credentials to `~/.m2/settings.xml`:
```xml
<servers>
    <server>
        <id>github</id>
        <username>YOUR_GITHUB_USERNAME</username>
        <password>YOUR_GITHUB_TOKEN</password>
    </server>
</servers>
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

## Kotlin Coroutines API

The optional `autobahn-kotlin-ext` module provides idiomatic Kotlin APIs using coroutines and Flow.

### Setup

Add the Kotlin extension dependency:

```kotlin
dependencies {
    // For Android:
    implementation("io.crossbar.autobahn:autobahn-android:22.2")
    implementation("io.crossbar.autobahn:autobahn-kotlin-ext-android:22.2")
    
    // For Netty/JVM:
    implementation("io.crossbar.autobahn:autobahn-java:22.2")
    implementation("io.crossbar.autobahn:autobahn-kotlin-ext-netty:22.2")
}
```

### Using Suspend Functions

Replace `CompletableFuture` callbacks with clean, sequential code:

```kotlin
import io.crossbar.autobahn.wamp.coroutines.*
import kotlinx.coroutines.*

class MyWampClient {
    private val session = Session()
    
    suspend fun connect(url: String, realm: String) {
        // Connect using the Client class
        val client = Client(session, url, realm)
        
        // Launch connection in background
        launch(Dispatchers.IO) {
            client.connect()
        }
        
        // Join the realm (suspend until joined)
        val sessionDetails = session.joinSuspend(realm)
        println("Joined realm: ${sessionDetails.realm}")
    }
    
    suspend fun subscribeToEvents() {
        // Subscribe and wait for confirmation
        val subscription = session.subscribeSuspend("com.myapp.events") { args ->
            println("Event received: ${args?.firstOrNull()}")
        }
        println("Subscribed to: ${subscription.topic}")
    }
    
    suspend fun callRemoteProcedure() {
        // Call procedure and get result
        val result = session.callSuspend("com.myapp.add", listOf(10, 20))
        println("Sum: ${result.results.firstOrNull()}")
    }
    
    suspend fun publishEvent() {
        // Publish and confirm
        val publication = session.publishSuspend(
            "com.myapp.notifications",
            args = listOf("User logged in", 42)
        )
        println("Published with ID: ${publication.publication}")
    }
}
```

### Using Kotlin Flow

Reactive-style subscriptions with automatic cleanup:

```kotlin
import kotlinx.coroutines.flow.*

class EventViewModel : ViewModel() {
    private val session = Session()
    
    // Convert WAMP events to Flow
    val eventsFlow: Flow<String> = session.subscribeAsFlow("com.myapp.events")
        .map { args -> args?.firstOrNull()?.toString() ?: "Unknown" }
        .catch { error ->
            emit("Error: ${error.message}")
        }
        .flowOn(Dispatchers.IO)
    
    // Collect in ViewModel
    fun startCollecting() {
        viewModelScope.launch {
            eventsFlow.collect { eventData ->
                println("Received: $eventData")
            }
        }
    }
}
```

### Complete Kotlin Example

```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.Client
import io.crossbar.autobahn.wamp.coroutines.*
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    private val session = Session()
    private var subscription: Subscription? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Connect in lifecycle scope
        lifecycleScope.launch {
            try {
                connectToWamp()
            } catch (e: Exception) {
                println("Connection failed: ${e.message}")
            }
        }
        
        setContent {
            MyApp()
        }
    }
    
    private suspend fun connectToWamp() {
        val client = Client(session, "ws://localhost:8080/ws", "realm1")
        
        // Start connection
        withContext(Dispatchers.IO) {
            client.connect()
        }
        
        // Join realm
        val details = session.joinSuspend("realm1")
        println("Joined: ${details.realm}")
        
        // Subscribe to events
        subscription = session.subscribeSuspend("com.myapp.updates") { args ->
            println("Update: ${args?.firstOrNull()}")
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            subscription?.let { session.unsubscribeSuspend(it) }
            session.leave()
        }
    }
}
```

## Jetpack Compose Integration

Combine the power of WAMP with modern Android UI:

```kotlin
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ViewModel with WAMP
class ChatViewModel : ViewModel() {
    private val session = Session()
    private var _isConnected = MutableStateFlow(false)
    private var _messages = MutableStateFlow<List<String>>(emptyList())
    
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    val messages: StateFlow<List<String>> = _messages.asStateFlow()
    
    init {
        connect()
    }
    
    private fun connect() {
        viewModelScope.launch {
            try {
                val client = Client(session, "ws://localhost:8080/ws", "chat")
                
                // Connect and join
                withContext(Dispatchers.IO) {
                    client.connect()
                }
                session.joinSuspend("chat")
                _isConnected.value = true
                
                // Subscribe to messages as Flow
                session.subscribeAsFlow("com.chat.messages")
                    .map { args -> args?.firstOrNull()?.toString() ?: "" }
                    .collect { message ->
                        _messages.value = _messages.value + message
                    }
            } catch (e: Exception) {
                _isConnected.value = false
            }
        }
    }
    
    fun sendMessage(text: String) {
        viewModelScope.launch {
            session.publishSuspend("com.chat.messages", args = listOf(text))
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            session.leave()
        }
    }
}

// Compose UI
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val isConnected by viewModel.isConnected.collectAsState()
    val messages by viewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Connection status
        if (isConnected) {
            Text("Connected", color = MaterialTheme.colorScheme.primary)
        } else {
            Text("Connecting...", color = MaterialTheme.colorScheme.error)
        }
        
        // Messages list
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                Text(message, modifier = Modifier.padding(8.dp))
            }
        }
        
        // Input field
        Row {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    viewModel.sendMessage(inputText)
                    inputText = ""
                },
                enabled = isConnected && inputText.isNotBlank()
            ) {
                Text("Send")
            }
        }
    }
}
```

### Benefits of Kotlin + Compose Integration

1. **Reactive UI**: Flow automatically updates Compose UI when WAMP events arrive
2. **Structured Concurrency**: `viewModelScope` ensures proper cleanup
3. **Type Safety**: Kotlin's type system catches errors at compile time
4. **Clean Code**: Suspend functions eliminate callback hell
5. **Lifecycle Aware**: Automatic handling of Android lifecycle events

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
- Jackson 2.21.0 (JSON/CBOR/MessagePack serialization)
- Netty 4.1.115 (for Java/Netty builds)
- Bouncy Castle 1.83 (cryptography)
- io.xconn:cryptology 1.1.2 (WAMP authentication)

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
