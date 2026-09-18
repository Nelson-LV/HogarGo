package com.hogargo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hogargo.app.data.AppViewModel
import com.hogargo.app.ui.navigation.HogarGoApp
import com.hogargo.app.ui.theme.HogarGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HogarGoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val appViewModel: AppViewModel = viewModel()
                    HogarGoApp(appViewModel = appViewModel)
                }
            }
        }
    }
}

