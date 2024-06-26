package com.example.obsoloot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.example.obsoloot.ui.theme.ObsoLootTheme
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.flow.first

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Content() }
    }

    @Preview(showBackground = true)
    @Composable
    fun Content() {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var submitted by remember { mutableStateOf(false) }
        var errorText by remember { mutableStateOf("") }
        ObsoLootTheme {
            Surface(Modifier.fillMaxSize()) {
                Column(
                    Modifier.padding(16.dp, 48.dp),
                    Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    Alignment.CenterHorizontally
                ) {
                    Text(
                        "Log In or Sign Up",
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextField(
                        username,
                        { username = it; errorText = "" },
                        placeholder = { Text("Username") }
                    )
                    TextField(
                        password,
                        { password = it; errorText = "" },
                        placeholder = { Text("Password") },
                        supportingText = @Composable {
                            Text(
                                text = errorText,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                    Button({ submitted = true }) {
                        Text("Submit")
                    }
                }
            }
        }
        LaunchedEffect(submitted) {
            if (!submitted) return@LaunchedEffect
            val loginResponse = webClient.post {
                url {
                    protocol = URLProtocol.HTTPS
                    host = SERVER_HOST
                    appendPathSegments("login")
                    parameter("username", username)
                    parameter("password", password)
                }
            }
            if (loginResponse.status.value == 200) {
                val ownerId: Int = loginResponse.body()
                dataStore.edit { preferences -> preferences[OWNER_ID] = ownerId }
                if (dataStore.data.first()[PHONE_ID] == null) {
                    val registerResponse = webClient.post {
                        url {
                            protocol = URLProtocol.HTTPS
                            host = SERVER_HOST
                            appendPathSegments("register")
                            parameter("ownerId", ownerId.toString())
                            parameter("nickname", android.os.Build.MODEL)
                        }
                    }
                    dataStore.edit { preferences -> preferences[PHONE_ID] = registerResponse.body() }
                }
                Intent(applicationContext, PrimaryActivity::class.java).also { startActivity(it) }
            } else {
                errorText = loginResponse.body()
                submitted = false
            }
        }
    }

}