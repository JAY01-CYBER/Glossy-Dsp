# MetroFuse provider port notices

The provider implementation source files under `app/src/main/kotlin/com/jay/glossy/providers/`
were adapted from MetroFuse, licensed under GPL-3.0. Preserve the upstream license and
attribution when redistributing. See `LICENSE.METROFUSE` and the upstream repository.

## Scope of this source port

Provider implementation source files included:
- Deezer (`DeezerAudioProvider`, proxy helper)
- Qobuz (`QobuzAudioProvider`)
- TIDAL (`TidalAudioProvider`)
- JioSaavn (`JioSaavnAudioProvider`)
- SoundCloud (`SoundCloudAudioProvider`)
- Provider quality enums and ISRC compatibility helper

The provider chain is now wired into Glossy's `MusicService` stream-resolution path. For
each track, the service tries Deezer FLAC, Qobuz lossless, TIDAL FLAC, JioSaavn high
quality, and SoundCloud AAC before requesting a YouTube Music stream. It caches the resolved
provider URL until its expiry and persists the returned MIME/codec/bitrate/sample-rate
metadata. Media3 extractors for FLAC, MP3, Ogg, ADTS/AAC and WAV were added.

Glossy Native mode currently uses the same ExoPlayer decoder/source pipeline and routes
decoded PCM through Glossy's native audio processor; this source change therefore affects
both engine modes without introducing a second decoder.

## Verification status and limitations

A Gradle compile could not be completed in the current environment because the Gradle
distribution host (`services.gradle.org`) was unreachable. Provider endpoints and real
playback were not live-tested here. Resolver availability, regional catalog access,
subscription/account requirements, and returned stream compatibility can vary.

Spotify is not included as a direct audio-stream provider: its public developer API does not
generally provide full-track audio streams. Spotify metadata can only be used for matching
unless the user has a separately authorized playback route. This archive must not be
described as a verified production build until it compiles and actual provider streams are
tested on-device.
