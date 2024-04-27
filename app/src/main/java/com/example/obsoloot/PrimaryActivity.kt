package com.example.obsoloot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.obsoloot.ui.theme.ObsoLootTheme
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.map
import java.util.Date
import kotlin.concurrent.fixedRateTimer

class PrimaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Content() }
    }

    @Composable
    fun Content() {
        PreviewableContent(
            dataStore.data.map { preferences -> preferences[OWNER_ID] ?: 0 }.collectAsState(0).value,
            dataStore.data.map { preferences -> preferences[PHONE_ID] ?: 0 }.collectAsState(0).value
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewableContent(ownerId: Int = 1, phoneId: Int = 1) {

        var reloadable by remember { mutableStateOf(true) }
        var phones: List<Phone> by remember { mutableStateOf(emptyList()) }
        var nickname by remember { mutableStateOf("") }
        var newNickname by remember { mutableStateOf("") }
        var renamed by remember { mutableStateOf(false) }
        var notifiable by remember { mutableStateOf(false) }
        var looting by remember { mutableStateOf(false) }
        var editing by remember { mutableStateOf(false) }
        var session by remember { mutableStateOf<DefaultClientWebSocketSession?>(null) }
        var tasks by remember { mutableStateOf<Map<String, Task>>(emptyMap()) }

        LaunchedEffect(Unit) {
            fixedRateTimer("reload", true, Date(System.currentTimeMillis() + 2000), 1000) { reloadable = true }
        }

        LaunchedEffect(ownerId, reloadable) {
            if (ownerId == 0 || !reloadable) return@LaunchedEffect
            try {
                val phonesResponse = webClient.get {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = SERVER_HOST
                        appendPathSegments("phones")
                        parameter("ownerId", ownerId.toString())
                    }
                }
                phones = phonesResponse.body()
                nickname = phones.find { phone -> phone.id == phoneId }?.nickname ?: ""
            } catch (_: ConnectTimeoutException) {
                // Request timed out, try again later
            } catch (_: NoTransformationFoundException) {
                // Hub is probably down, try again later
            } finally {
                reloadable = false
            }
        }

        ObsoLootTheme {
            Surface(Modifier.fillMaxSize()) {
                Column(
                    Modifier.padding(16.dp, 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            nickname,
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(onClick = { editing = true }) {
                            Icon(
                                Icons.Rounded.Edit,
                                contentDescription = "Edit icon"
                            )
                        }
                    }
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedCard {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 4.dp),
                            Arrangement.SpaceBetween,
                            Alignment.CenterVertically
                        ) {
                            Text("Toggle notifications")
                            Switch(
                                checked = notifiable,
                                onCheckedChange = { notifiable = it }
                            )
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 4.dp),
                            Arrangement.SpaceBetween,
                            Alignment.CenterVertically
                        ) {
                            Text("Toggle looting")
                            Switch(
                                checked = looting,
                                onCheckedChange = { looting = it }
                            )
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                    Text(
                        "Owned Phones",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(phones) { phone ->
                            OutlinedCard {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    Arrangement.spacedBy(8.dp, Alignment.Start)
                                ) {
                                    Image(
                                        painterResource(id = R.drawable.ic_phone),
                                        "Phone icon",
                                        colorFilter = ColorFilter.tint(when(phone.status) {
                                            "ACTIVE" -> Color.Green
                                            "IDLE" -> Color.Yellow
                                            "ERROR" -> Color.Red
                                            else -> Color.White
                                        })
                                    )
                                    Text(phone.nickname)
                                }
                            }
                        }
                    }
                }
            }
            if (editing) {
                AlertDialog(
                    title = {
                        Text("Edit Nickname")
                    },
                    text = {
                        TextField(
                            newNickname,
                            { newNickname = it },
                            placeholder = { Text("Nickname") }
                        )
                    },
                    confirmButton = {
                        TextButton({ editing = false; renamed = true }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton({ editing = false; newNickname = "" }) {
                            Text("Cancel")
                        }
                    },
                    onDismissRequest = { editing = false; newNickname = "" }
                )
            }
        }

        LaunchedEffect(renamed) {
            if (!renamed) return@LaunchedEffect
            webClient.put {
                url {
                    protocol = URLProtocol.HTTPS
                    host = SERVER_HOST
                    appendPathSegments("nickname")
                    parameter("phoneId", phoneId.toString())
                    parameter("nickname", newNickname)
                }
            }
            newNickname = ""
            renamed = false
        }

        LaunchedEffect(looting) {
            if (looting) {
                session = webClient.webSocketSession {
                    url {
                        host = SERVER_HOST
                        appendPathSegments("loot")
                        parameter("phoneId", phoneId)
                    }
                }
                while (true) {
                    try {
                        val taskName = (session!!.incoming.receive() as Frame.Text).readText()
                        val taskArgs = (session!!.incoming.receive() as Frame.Text).readText()
                        if (taskName !in tasks) {
                            val taskResponse = webClient.get {
                                url {
                                    host = SERVER_HOST
                                    appendPathSegments("task")
                                    parameter("name", taskName)
                                }
                            }
                            val task: Task = taskResponse.body()
                            tasks += taskName to task
                            interpreter.eval(task.code)
                        }
                        val task = tasks[taskName]
                        val result = interpreter.eval("${task?.method}($taskArgs);").toString()
                        session!!.send(Frame.Text(result))
                    } catch (_: ClosedReceiveChannelException) {
                        // TODO: Alert user that the phone lost connection to the hub
                    }
                }
            } else {
                session?.close()
                session = null
            }
        }
    }
}