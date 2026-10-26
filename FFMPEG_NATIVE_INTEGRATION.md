# Glossy native FFmpeg integration

## Scope
- The Media3/ExoPlayer playback path is intentionally not modified.
- Glossy Audio Engine's native `NativePlayer` path now uses FFmpeg `libavformat` + `libavcodec`
  for demuxing/decoding and `libswresample` to convert decoded audio to interleaved Float32 stereo.
- Decoded PCM is passed through the existing Glossy DSP API and Oboe output/ring buffer.
- FFmpeg shared libraries are packaged for `arm64-v8a`, `armeabi-v7a`, and `x86_64`.
- FFmpeg headers are in `app/src/main/cpp/third_party/ffmpeg/include`.

## Libraries
`libavcodec.so`, `libavformat.so`, `libavutil.so`, `libswresample.so`, and `libavfilter.so` are bundled.
The current decode path directly uses avformat/avcodec/swresample. `libavfilter` is packaged and linked,
but no filter graph is enabled by default; Glossy's own DSP remains the active effects chain.
OpenSSL static archives were not linked because the supplied FFmpeg shared objects do not list OpenSSL
as a dynamic dependency. Do not add those archives unless a specific TLS/custom-protocol build requires them.

## Important validation status
This source integration has not been compiled with the project's Android SDK/NDK or tested on a physical
device in this environment. It must be treated as an integration candidate until it passes:
1. `./gradlew :app:externalNativeBuildDebug`
2. `./gradlew :app:assembleDebug`
3. Device tests for HTTP(S) stream open, pause/resume, seek, end-of-stream, DSP, background playback,
   all shipped ABIs, and fallback behavior.

FFmpeg build configuration can omit codecs or protocols even when the libraries exist. Verify supported
formats with actual files/streams. Do not ship until the Gradle/NDK build and device tests pass.
