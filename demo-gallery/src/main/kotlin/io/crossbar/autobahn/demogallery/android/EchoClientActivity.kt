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

package io.crossbar.autobahn.demogallery.android

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.crossbar.autobahn.demogallery.android.ui.theme.AutobahnTheme
import io.crossbar.autobahn.websocket.WebSocketConnection
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler
import io.crossbar.autobahn.websocket.exceptions.WebSocketException
import io.crossbar.autobahn.websocket.interfaces.IWebSocket
import io.crossbar.autobahn.websocket.types.WebSocketOptions

private const val TAG = "io.crossbar.autobahn.echo"
private const val PREFS_NAME = "AutobahnAndroidEcho"
private const val DEFAULT_HOSTNAME = "192.168.1.3"
private const val DEFAULT_PORT = "9000"

class EchoClientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutobahnTheme {
                EchoClientScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoClientScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var hostname by remember { mutableStateOf(prefs.getString("hostname", DEFAULT_HOSTNAME) ?: DEFAULT_HOSTNAME) }
    var port by remember { mutableStateOf(prefs.getString("port", DEFAULT_PORT) ?: DEFAULT_PORT) }
    var statusText by remember { mutableStateOf("Status: Ready") }
    var messageText by remember { mutableStateOf("") }
    var isConnected by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }

    val connection = remember { WebSocketConnection() }

    fun savePrefs() {
        prefs.edit()
            .putString("hostname", hostname)
            .putString("port", port)
            .apply()
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun connect() {
        var wsUri = hostname
        if (!wsUri.startsWith("ws://") && !wsUri.startsWith("wss://")) {
            wsUri = "ws://$wsUri"
        }
        if (port.isNotEmpty()) {
            wsUri = "$wsUri:$port"
        }

        statusText = "Status: Connecting to $wsUri ..."
        isConnecting = true

        val options = WebSocketOptions().apply {
            setReconnectInterval(5000)
        }

        try {
            connection.connect(wsUri, object : WebSocketConnectionHandler() {
                override fun onOpen() {
                    statusText = "Status: Connected to $wsUri"
                    savePrefs()
                    isConnected = true
                    isConnecting = false
                }

                override fun onMessage(payload: String) {
                    showToast("Got echo: $payload")
                }

                override fun onClose(code: Int, reason: String?) {
                    showToast("Connection lost.")
                    statusText = "Status: Ready"
                    isConnected = false
                    isConnecting = false
                }
            }, options)
        } catch (e: WebSocketException) {
            Log.d(TAG, e.toString())
            statusText = "Status: Connection failed"
            isConnecting = false
        }
    }

    fun disconnect() {
        connection.sendClose()
    }

    fun sendMessage() {
        connection.sendMessage(messageText)
        messageText = ""
    }

    // Handle lifecycle for cleanup
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (connection.isConnected) {
                    connection.sendClose()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WebSocket Echo Client") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = hostname,
                onValueChange = { hostname = it },
                label = { Text("Hostname") },
                enabled = !isConnected && !isConnecting,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = port,
                onValueChange = { port = it },
                label = { Text("Port") },
                enabled = !isConnected && !isConnecting,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { if (isConnected) disconnect() else connect() },
                enabled = !isConnecting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    when {
                        isConnecting -> "Connecting..."
                        isConnected -> "Disconnect"
                        else -> "Connect"
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                label = { Text("Message") },
                enabled = isConnected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { sendMessage() },
                enabled = isConnected && messageText.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Message")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EchoClientScreenPreview() {
    AutobahnTheme {
        EchoClientScreen()
    }
}
