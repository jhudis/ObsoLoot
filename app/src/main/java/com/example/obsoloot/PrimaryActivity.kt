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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.obsoloot.ui.theme.ObsoLootTheme
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.flow.map

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

        var phones: List<Phone> by remember { mutableStateOf(emptyList()) }
        var selfNickname by remember { mutableStateOf("") }
        var newNickname by remember { mutableStateOf("") }
        var notifiable by remember { mutableStateOf(false) }
        var looting by remember { mutableStateOf(false) }
        var reloadable by remember { mutableStateOf(true) }
        var editing by remember { mutableStateOf(false) }
        var confirmed by remember { mutableStateOf(false) }

        LaunchedEffect(ownerId, reloadable) {
            if (ownerId == 0 || !reloadable) return@LaunchedEffect
            val phonesResponse: HttpResponse = httpClient.get(HUB_URL) {
                url {
                    appendPathSegments("phones")
                    parameters.append("ownerId", ownerId.toString())
                }
            }
            phones = phonesResponse.body()
            selfNickname = phones.find { phone -> phone.id == phoneId }?.nickname ?: ""
            editing = false
            newNickname = ""
            reloadable = false
        }

        ObsoLootTheme {
            Surface(Modifier.fillMaxSize()) {
                Column(
                    Modifier.padding(16.dp, 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            selfNickname,
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
                        "All Owned Phones",
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
                                        "Phone icon"
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
                        TextButton({ confirmed = true }) {
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

        LaunchedEffect(confirmed) {
            if (!confirmed) return@LaunchedEffect
            httpClient.get(HUB_URL) {
                url {
                    appendPathSegments("nickname")
                    parameters.append("phoneId", phoneId.toString())
                    parameters.append("nickname", newNickname)
                }
            }
            reloadable = true
            confirmed = false
        }
    }
}