package com.jay.glossy.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jay.glossy.ui.component.IconButton
import com.jay.glossy.ui.player.AudioDiagnostics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@androidx.compose.material3.ExperimentalMaterial3Api
@androidx.compose.runtime.Composable
fun AudioLogsScreen(navController: NavController) {
    val context = LocalContext.current
    var refresh by remember { mutableIntStateOf(0) }
    var filter by remember { mutableStateOf("ALL") }
    DisposableEffect(Unit) {
        AudioDiagnostics.setListener { refresh++ }
        onDispose { AudioDiagnostics.setListener(null) }
    }
    val entries = remember(refresh, filter) {
        AudioDiagnostics.snapshot().filter { filter == "ALL" || it.level == filter }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audio Logs") },
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }, onLongClick = { navController.popBackStack() }) { Text("‹") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Text("Glossy native audio diagnostics", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("Only events emitted by Glossy are shown here. This is not full Android Logcat, and FFmpeg is not confirmed unless a decoder event is explicitly logged.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("ALL", "ERROR", "WARN", "INFO").forEach { level ->
                    FilterChip(selected = filter == level, onClick = { filter = level }, label = { Text(level) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { refresh++ }, modifier = Modifier.weight(1f)) { Text("Refresh") }
                Button(onClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Glossy Audio Diagnostics")
                        putExtra(Intent.EXTRA_TEXT, AudioDiagnostics.exportText())
                    }
                    context.startActivity(Intent.createChooser(send, "Share audio logs"))
                }, modifier = Modifier.weight(1f)) { Text("Share logs") }
                OutlinedButton(onClick = { AudioDiagnostics.clear(); refresh++ }) { Text("Clear") }
            }
            Spacer(Modifier.height(8.dp))
            Text("${entries.size} event(s)", style = MaterialTheme.typography.labelMedium)
            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(entries.reversed(), key = { "${it.time}-${it.tag}-${it.message}" }) { entry ->
                    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        val timestamp = remember(entry.time) { SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(entry.time)) }
                        Text("$timestamp  ${entry.level}/${entry.tag}", style = MaterialTheme.typography.labelMedium, color = when (entry.level) { "ERROR" -> MaterialTheme.colorScheme.error; "WARN" -> MaterialTheme.colorScheme.tertiary; else -> MaterialTheme.colorScheme.primary })
                        Text(entry.message, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
