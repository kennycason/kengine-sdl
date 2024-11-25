package com.kengine.sound

import com.kengine.context.Context
import com.kengine.log.Logger
import kotlinx.cinterop.toKString
import platform.posix.exit
import sdl2.SDL_GetError
import sdl2.SDL_INIT_AUDIO
import sdl2.SDL_Init
import sdl2.mixer.MIX_DEFAULT_FORMAT
import sdl2.mixer.Mix_CloseAudio
import sdl2.mixer.Mix_OpenAudio

class SoundContext private constructor(
    val manager: SoundManager
) : Context() {

    companion object {
        private var currentContext: SoundContext? = null

        fun get(): SoundContext {
            if (currentContext == null) {
                currentContext = SoundContext(
                    manager = SoundManager()
                )

                // Initialize SDL_mixer
                if (SDL_Init(SDL_INIT_AUDIO) != 0) {
                    Logger.error("Error initializing SDL Audio: ${SDL_GetError()?.toKString()}")
                    exit(1)
                }

                require(Mix_OpenAudio(44100, MIX_DEFAULT_FORMAT.toUShort(), 2, 2048) == 0) {
                    "Failed to initialize SDL_mixer: ${SDL_GetError()}"
                }
            }
            return currentContext ?: throw IllegalStateException("Failed to create SoundContext")
        }
    }

    override fun cleanup() {
        manager.cleanup()
        Mix_CloseAudio()
    }
}