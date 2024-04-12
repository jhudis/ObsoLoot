@file:Suppress("SpellCheckingInspection")

package com.example.obsoloot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.obsoloot.ui.theme.ObsoLootTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Greeting()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting() {
    ObsoLootTheme {
        Surface(
            Modifier.fillMaxSize()
        ) {
            Column(
                Modifier.padding(16.dp, 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Text(
                    "Welcome to ObsoLoot!",
                    fontSize = 24.sp
                )
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo",
                    Modifier.size(200.dp)
                )
            }
        }
    }
}