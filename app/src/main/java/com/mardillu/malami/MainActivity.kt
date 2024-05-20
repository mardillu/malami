package com.mardillu.malami

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.mardillu.malami.data.PreferencesManager
import com.mardillu.malami.ui.navigation.MalamiNavHost
import com.mardillu.malami.ui.theme.MalamiTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefManager: PreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MalamiTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MalamiNavHost(isLoggedIn = prefManager.isLoggedIn,)
                }
            }
        }
    }
}

