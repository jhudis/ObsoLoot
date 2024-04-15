package com.example.obsoloot

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
import com.example.obsoloot.ui.theme.ObsoLootTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

private const val HUB_URL = "https://berrysmart.games/"

class LoginActivity : ComponentActivity() {
    private var client = HttpClient(CIO)

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
                        { username = it },
                        placeholder = { Text("Username") }
                    )
                    TextField(
                        password,
                        { password = it },
                        placeholder = { Text("Password") }
                    )
                    Button(onClick = { submitted = true }) {
                        Text("Submit")
                    }
                }
            }
        }
        LaunchedEffect(submitted) {
            if (!submitted) return@LaunchedEffect
            val response: HttpResponse = client.get("${HUB_URL}login?username=${username}&password=${password}")
            println(response.status.value)
            println(response.bodyAsText())
        }
    }

}