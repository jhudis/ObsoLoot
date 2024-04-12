@file:Suppress("SpellCheckingInspection")

package com.example.obsoloot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.obsoloot.ui.theme.ObsoLootTheme
import kotlinx.coroutines.delay

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Content() }
    }

    @Preview(showBackground = true)
    @Composable
    fun Content() {
        LaunchedEffect(Unit) {
            delay(2000)
            Intent(applicationContext, LoginActivity::class.java).also { startActivity(it) }
        }
        ObsoLootTheme {
            Surface(Modifier.fillMaxSize()) {
                Column(
                    Modifier.padding(16.dp, 48.dp),
                    Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    Alignment.CenterHorizontally
                ) {
                    Text(
                        "Welcome to ObsoLoot!",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Image(
                        painterResource(id = R.drawable.logo),
                        "logo",
                        Modifier.size(200.dp)
                    )
                }
            }
        }
    }
}