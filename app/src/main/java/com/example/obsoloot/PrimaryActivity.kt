package com.example.obsoloot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.obsoloot.ui.theme.ObsoLootTheme
import kotlinx.coroutines.flow.map

class PrimaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Content() }
    }

    @Composable
    fun Content() {
        PreviewableContent(
            dataStore.data.map { preferences -> preferences[OWNER_ID] ?: 0 }.collectAsState(0).value
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewableContent(ownerId: Int = 1) {
        ObsoLootTheme {
            Surface(Modifier.fillMaxSize()) {
                Column(
                    Modifier.padding(16.dp, 48.dp),
                    Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    Alignment.CenterHorizontally
                ) {
                    Text(
                        ownerId.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}