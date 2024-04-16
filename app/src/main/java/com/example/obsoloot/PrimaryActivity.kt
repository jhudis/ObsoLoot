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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
        LaunchedEffect(ownerId) {
            if (ownerId == 0) return@LaunchedEffect
            val phonesResponse: HttpResponse = httpClient.get(HUB_URL) {
                url {
                    appendPathSegments("phones")
                    parameters.append("ownerId", ownerId.toString())
                }
            }
            phones = phonesResponse.body()
            selfNickname = phones.find { phone -> phone.id == phoneId }?.nickname ?: ""
        }
        ObsoLootTheme {
            Surface(Modifier.fillMaxSize()) {
                Column(
                    Modifier.padding(16.dp, 48.dp),
                    Arrangement.spacedBy(8.dp),
                    Alignment.CenterHorizontally
                ) {
                    Text(
                        selfNickname,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "All Owned Phones",
                        style = MaterialTheme.typography.titleLarge
                    )
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
        }
    }
}