package com.example.toko_kelontong.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.toko_kelontong.data.local.Session

/**
 * Beranda sementara. Ganti dengan dashboard (Pemilik) / layar kasir (Kasir)
 * sesuai urutan pengerjaan di PRD.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    session: Session,
    onOpenProducts: () -> Unit,
    onLogout: () -> Unit,
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Toko Kelontong") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Halo, ${session.nama ?: "-"}", style = MaterialTheme.typography.titleLarge)
            Text("Peran: ${session.role ?: "-"}")

            Button(onClick = onOpenProducts, modifier = Modifier.fillMaxWidth()) {
                Text("Daftar Produk")
            }
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Keluar")
            }
        }
    }
}
