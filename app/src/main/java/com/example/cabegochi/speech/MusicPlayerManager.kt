package com.example.cabegochi.speech

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import java.io.File

/**
 * Minimal local music player wrapper using android.media.MediaPlayer.
 * Plays files under app external files dir (no extra READ_EXTERNAL_STORAGE permission needed
 * if files are placed in the app's external files directory).
 */
class MusicPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPath: String? = null

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    fun play(filePath: String) {
        stop()
        try {
            val f = File(filePath)
            if (!f.exists()) return
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.fromFile(f))
                setOnPreparedListener { it.start() }
                setOnCompletionListener { stop() }
                prepareAsync()
            }
            currentPath = filePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.pause()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resume() {
        try {
            mediaPlayer?.let {
                if (!it.isPlaying) it.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        } finally {
            mediaPlayer = null
            currentPath = null
        }
    }

    fun currentFilePath(): String? = currentPath
}
