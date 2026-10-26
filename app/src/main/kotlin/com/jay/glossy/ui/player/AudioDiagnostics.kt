package com.jay.glossy.ui.player

import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList

/** Small in-app diagnostic buffer. It only stores events explicitly emitted by Glossy. */
object AudioDiagnostics {
    data class Entry(val time: Long, val level: String, val tag: String, val message: String)
    private const val LIMIT = 500
    private val entries = CopyOnWriteArrayList<Entry>()
    @Volatile private var listener: (() -> Unit)? = null

    fun log(level: String = "INFO", tag: String = "Audio", message: String) {
        entries.add(Entry(System.currentTimeMillis(), level, tag, message.take(1200)))
        while (entries.size > LIMIT) entries.removeAt(0)
        listener?.invoke()
    }
    fun snapshot(): List<Entry> = entries.toList()
    fun clear() { entries.clear(); log(message = "Log buffer cleared") }
    fun setListener(value: (() -> Unit)?) { listener = value }
    fun exportText(): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        return buildString {
            appendLine("Glossy Audio Diagnostics")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}; Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            appendLine("Note: This file contains Glossy app-emitted diagnostic events, not the full Android system Logcat.")
            appendLine()
            snapshot().forEach { appendLine("${fmt.format(Date(it.time))} ${it.level}/${it.tag}: ${it.message}") }
        }
    }
}
