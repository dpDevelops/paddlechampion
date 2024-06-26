package com.android.paddlechampion

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.SoundPool
import android.media.MediaPlayer
import android.util.Log
import java.io.IOException
import java.lang.Exception
import kotlin.math.max
import kotlin.math.min

private const val TAG = "BeatBox"
private const val MUSIC_FOLDER = "music"
private const val SOUNDS_FOLDER = "sound_effects"
private const val MAX_SOUNDS = 8

class SoundBox(private val assets: AssetManager) {
    val sounds: List<Sound>
    var music: MutableList<Sound> = mutableListOf()
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(MAX_SOUNDS).build()
    init {
        sounds = loadSounds()
    }

    // SOUND POOL STUFF
    fun play(soundIndex: Int) {
        val clampedIndex: Int =  max(1, soundIndex).also { min(it, sounds.size) }
        val sound = sounds[clampedIndex]
        sound.soundId?.let {
            soundPool.play(it, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }
    fun release() {
        soundPool.release()
    }
    private fun loadSounds(): List<Sound> {
        val musicNames: Array<String>
        val soundNames: Array<String>
        // get music
        try {
            musicNames = assets.list(MUSIC_FOLDER)!!
        } catch (e: Exception) {
            Log.e(TAG, "Could not list music assets", e)
            return emptyList()
        }
        musicNames.forEach{filename ->
            val assetPath = "$MUSIC_FOLDER/$filename"
            music.add(Sound(assetPath))
        }
        // get sound effects
        try {
            soundNames = assets.list(SOUNDS_FOLDER)!!
        } catch (e: Exception) {
            Log.e(TAG, "Could not list sound effect assets", e)
            return emptyList()
        }
        // create sounds
        val sounds = mutableListOf<Sound>()
        soundNames.forEach{filename ->
            val assetPath = "$SOUNDS_FOLDER/$filename"
            val sound = Sound(assetPath)
            try {
                load(sound)
                sounds.add(sound)
                Log.d(TAG, "load sound $filename [${sound.soundId}]")
            } catch (ioe: IOException) {
                Log.e(TAG, "Could not load sound $filename", ioe)
            }
        }
        return sounds
    }
    private fun load(sound: Sound) {
        val afd: AssetFileDescriptor = assets.openFd(sound.assetPath)
        val soundId = soundPool.load(afd, 1)
        sound.soundId = soundId
    }
}