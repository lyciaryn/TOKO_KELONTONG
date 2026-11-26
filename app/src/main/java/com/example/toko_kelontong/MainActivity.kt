package com.example.toko_kelontong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.toko_kelontong.ui.TokoApp
import com.example.toko_kelontong.ui.theme.TokoKelontongTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as TokoApplication).container
        setContent {
            TokoKelontongTheme {
                TokoApp(container)
            }
        }
    }
}
