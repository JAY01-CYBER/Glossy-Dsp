package com.jay.glossy.providers

/**
 * Shared ISRC helper for provider matching. This port keeps caller-provided
 * ISRCs and validates their shape; catalog lookup can be wired to Glossy's
 * metadata layer separately.
 */
object IsrcResolver {
    fun normalize(value: String?): String? {
        val cleaned = value?.trim()?.uppercase()?.replace("-", "") ?: return null
        return cleaned.takeIf { it.matches(Regex("[A-Z]{2}[A-Z0-9]{3}[0-9]{7}")) }
    }
    suspend fun resolve(
        context: android.content.Context? = null,
        title: String = "",
        artists: List<String> = emptyList(),
        durationMs: Long? = null,
        candidateIsrc: String? = null,
    ): String? = normalize(candidateIsrc)
    fun publish(title: String, artist: String, isrc: String, durationSeconds: Long? = null) {}
    fun publish(isrc: String) {}
}
