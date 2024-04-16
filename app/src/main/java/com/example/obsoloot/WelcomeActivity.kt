package com.example.obsoloot

import android.content.Context
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.obsoloot.ui.theme.ObsoLootTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")
val OWNER_ID = intPreferencesKey("owner_id")
val PHONE_ID = intPreferencesKey("phone_id")

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
            val activity = if (dataStore.data.first()[OWNER_ID] == null) {
                LoginActivity::class
            } else {
                PrimaryActivity::class
            }
            Intent(applicationContext, activity.java).also { startActivity(it) }
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