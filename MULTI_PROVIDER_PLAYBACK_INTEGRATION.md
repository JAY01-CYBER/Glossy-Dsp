# Multi-provider playback integration

## Resolution order

`MusicService.createDataSourceFactory()` now attempts the following source providers before
falling back to the existing YouTube Music resolver:

1. Deezer (FLAC requested)
2. Qobuz (lossless quality requested)
3. TIDAL (FLAC requested)
4. JioSaavn (high quality)
5. SoundCloud (AAC 160 kbps)
6. Existing YouTube Music playback

A provider is considered successful only when it returns a non-empty media URI. Each attempt
is logged with a provider-specific failure message when resolution fails. The service caches
the resolved URI until its reported expiry and records codec, MIME type, bitrate and sample
rate in the existing format table.

## Both audio-engine modes

Glossy's current `GLOSSY_NATIVE` mode does not own a separate decoder: ExoPlayer owns the
media source, buffering, decoding and audio sink in both modes, while the native engine
processes decoded PCM in the audio processor chain. The provider URI is therefore resolved
in the shared ExoPlayer data-source path and is available to both modes.

## Important limitations

- Provider priority is currently hard-coded; a settings UI for selecting/reordering providers
  has not been added in this change.
- The provider chain falls back when resolution fails. A successful resolver response can
  still return a stream that later fails to open; this requires device-level testing and
  follow-up stream-error handling.
- Endpoint availability, catalog matching, country/account restrictions and actual lossless
  delivery cannot be guaranteed without live service access.
- Spotify is not a direct full-track audio source through its public developer API.
- A successful Android Gradle build could not be verified because `services.gradle.org` was
  unreachable in this environment.
