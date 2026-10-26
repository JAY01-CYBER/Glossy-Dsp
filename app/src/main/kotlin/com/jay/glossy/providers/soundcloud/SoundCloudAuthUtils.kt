package com.jay.glossy.providers.soundcloud

fun normalizeSoundCloudAuthInput(input: String): String =
    input.trim().removePrefix("OAuth ").removePrefix("oauth ")
