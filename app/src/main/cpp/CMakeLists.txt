cmake_minimum_required(VERSION 3.22.1)
project("glossy_audio_engine")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(oboe REQUIRED CONFIG)

# FFmpeg 7.x shared libraries supplied under app/src/main/jniLibs/<ANDROID_ABI>.
set(FFMPEG_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}")
set(FFMPEG_INCLUDE "${CMAKE_CURRENT_SOURCE_DIR}/third_party/ffmpeg/include")

foreach(FFMPEG_LIB avutil swresample avcodec avformat avfilter)
    add_library(ffmpeg_${FFMPEG_LIB} SHARED IMPORTED)
    set_target_properties(ffmpeg_${FFMPEG_LIB} PROPERTIES
        IMPORTED_LOCATION "${FFMPEG_ROOT}/lib${FFMPEG_LIB}.so")
endforeach()

add_library(glossy_engine SHARED
    glossy_engine.cpp
    GlossyNativePlayer.cpp
)

target_include_directories(glossy_engine PRIVATE
    "${CMAKE_CURRENT_SOURCE_DIR}"
    "${FFMPEG_INCLUDE}"
)

target_link_libraries(glossy_engine
    oboe::oboe
    ffmpeg_avfilter
    ffmpeg_avformat
    ffmpeg_avcodec
    ffmpeg_swresample
    ffmpeg_avutil
    mediandk
    log
    android
    z
    dl
)

target_link_options(glossy_engine PRIVATE "-Wl,-z,max-page-size=16384")
