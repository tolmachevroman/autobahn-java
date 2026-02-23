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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.crossbar.autobahn.demogallery.android.ui.theme.AutobahnTheme
import io.crossbar.autobahn.websocket.WebSocketConnection
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler
import io.crossbar.autobahn.websocket.exceptions.WebSocketException
import io.crossbar.autobahn.websocket.types.WebSocketOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val PREFS_NAME = "AutobahnAndroidTestsuiteClient"
private const val DEFAULT_WS_URI = "ws://192.168.1.3:9001"
private const val DEFAULT_AGENT = "AutobahnAndroid"

class TestSuiteClientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutobahnTheme {
                TestSuiteClientScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestSuiteClientScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    val scope = rememberCoroutineScope()

    var wsUri by remember { mutableStateOf(prefs.getString("wsuri", DEFAULT_WS_URI) ?: DEFAULT_WS_URI) }
    var agent by remember { mutableStateOf(prefs.getString("agent", DEFAULT_AGENT) ?: DEFAULT_AGENT) }
    var statusText by remember { mutableStateOf("Ready to start tests") }
    var currentCase by remember { mutableIntStateOf(0) }
    var lastCase by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    val options = remember {
        WebSocketOptions().apply {
            setReceiveTextMessagesRaw(true)
            setMaxMessagePayloadSize(16 * 1024 * 1024)
            setMaxFramePayloadSize(16 * 1024 * 1024)
            setAutoPingInterval(0)
        }
    }

    fun savePrefs() {
        prefs.edit()
            .putString("wsuri", wsUri)
            .putString("agent", agent)
            .apply()
    }

    suspend fun queryCaseCount() = withContext(Dispatchers.IO) {
        val webSocket = WebSocketConnection()
        try {
            webSocket.connect("$wsUri/getCaseCount", object : WebSocketConnectionHandler() {
                override fun onOpen() {
                    savePrefs()
                }

                override fun onMessage(payload: String) {
                    lastCase = payload.toIntOrNull() ?: 0
                }

                override fun onClose(code: Int, reason: String?) {
                    statusText = "Will run $lastCase test cases"
                    currentCase = 1
                }
            })

            // Wait for connection to close
            while (webSocket.isConnected) {
                kotlinx.coroutines.delay(100)
            }
        } catch (e: WebSocketException) {
            Log.e("TestSuite", "Failed to query case count", e)
            statusText = "Failed to query case count"
            isRunning = false
        }
    }

    suspend fun runTest() = withContext(Dispatchers.IO) {
        val webSocket = WebSocketConnection()
        try {
            webSocket.connect(
                "$wsUri/runCase?case=$currentCase&agent=$agent",
                object : WebSocketConnectionHandler() {
                    override fun onMessage(payload: String) {
                        webSocket.sendMessage(payload)
                    }

                    override fun onMessage(payload: ByteArray, isBinary: Boolean) {
                        webSocket.sendMessage(payload, isBinary)
                    }

                    override fun onOpen() {
                        statusText = "Test case $currentCase / $lastCase started"
                    }

                    override fun onClose(code: Int, reason: String?) {
                        statusText = "Test case $currentCase / $lastCase finished"
                        currentCase += 1
                    }
                },
                options
            )

            // Wait for connection to close
            while (webSocket.isConnected) {
                kotlinx.coroutines.delay(100)
            }
        } catch (e: WebSocketException) {
            Log.e("TestSuite", "Test case $currentCase failed", e)
            statusText = "Test case $currentCase failed"
            currentCase += 1
        }
    }

    suspend fun updateReport() = withContext(Dispatchers.IO) {
        val webSocket = WebSocketConnection()
        try {
            webSocket.connect(
                "$wsUri/updateReports?agent=$agent",
                object : WebSocketConnectionHandler() {
                    override fun onOpen() {
                        statusText = "Updating test reports..."
                    }

                    override fun onClose(code: Int, reason: String?) {
                        statusText = "Test reports updated. Finished."
                        isRunning = false
                    }
                }
            )

            // Wait for connection to close
            while (webSocket.isConnected) {
                kotlinx.coroutines.delay(100)
            }
        } catch (e: WebSocketException) {
            Log.e("TestSuite", "Failed to update report", e)
            statusText = "Failed to update report"
            isRunning = false
        }
    }

    suspend fun processNext() {
        when {
            currentCase == 0 -> queryCaseCount()
            currentCase <= lastCase -> runTest()
            else -> updateReport()
        }

        // Continue processing if there are more cases
        if (currentCase in 1..lastCase) {
            processNext()
        }
    }

    fun startTests() {
        isRunning = true
        currentCase = 0
        lastCase = 0
        scope.launch {
            processNext()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test Suite Client") },
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
                value = wsUri,
                onValueChange = { wsUri = it },
                label = { Text("WebSocket URI") },
                enabled = !isRunning,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = agent,
                onValueChange = { agent = it },
                label = { Text("Agent Name") },
                enabled = !isRunning,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isRunning && lastCase > 0) {
                LinearProgressIndicator(
                    progress = { if (lastCase > 0) currentCase.toFloat() / lastCase else 0f },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$currentCase / $lastCase",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { startTests() },
                enabled = !isRunning,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text(if (isRunning) "Running..." else "Start Tests")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestSuiteClientScreenPreview() {
    AutobahnTheme {
        TestSuiteClientScreen()
    }
}
