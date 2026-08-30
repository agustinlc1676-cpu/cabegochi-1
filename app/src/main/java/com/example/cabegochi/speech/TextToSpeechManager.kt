package com.example.cabegochi.speech

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class DeviceVoice(
    val name: String,
    val locale: Locale,
    val isNetworkConnectionRequired: Boolean,
    val quality: Int,
    val latency: Int
) {
    val displayLabel: String
        get() {
            val lang = locale.displayLanguage.ifBlank { "Desconocido" }
            val country = if (locale.displayCountry.isNotBlank()) " (${locale.displayCountry})" else ""
            val cleanName = name.substringAfterLast("#").substringAfterLast("_").take(12)
            return "$lang$country - $cleanName"
        }
}

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    companion object {
        private const val TAG = "TextToSpeechManager"
        private const val UTTERANCE_ID_SPEAK = "UTTERANCE_SPEAK"
        private const val UTTERANCE_ID_TEST = "UTTERANCE_TEST"
    }

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _availableVoices = MutableStateFlow<List<DeviceVoice>>(emptyList())
    val availableVoices: StateFlow<List<DeviceVoice>> = _availableVoices.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale("es", "MX")

            // Register utterance listener
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            })

            refreshVoices()
        } else {
            Log.e(TAG, "Failed to initialize TextToSpeech engine. Status: $status")
        }
    }

    fun refreshVoices() {
        if (!isInitialized) return
        try {
            val rawVoices: Set<Voice>? = tts?.voices
            if (!rawVoices.isNullOrEmpty()) {
                val list = rawVoices.map { v ->
                    DeviceVoice(
                        name = v.name,
                        locale = v.locale ?: Locale.getDefault(),
                        isNetworkConnectionRequired = v.isNetworkConnectionRequired,
                        quality = v.quality,
                        latency = v.latency
                    )
                }.sortedWith(
                    compareByDescending<DeviceVoice> { it.locale.language.startsWith("es") }
                        .thenBy { it.displayLabel }
                )
                _availableVoices.value = list
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not fetch device voices: ${e.message}")
        }
    }

    fun speak(
        text: String,
        pitch: Float = 1.0f,
        rate: Float = 1.0f,
        voiceName: String? = null
    ) {
        if (!isInitialized) return
        stop()

        tts?.setPitch(pitch.coerceIn(0.5f, 2.0f))
        tts?.setSpeechRate(rate.coerceIn(0.5f, 2.0f))

        if (!voiceName.isNullOrBlank()) {
            val matchingVoice = tts?.voices?.firstOrNull { it.name == voiceName }
            if (matchingVoice != null) {
                tts?.voice = matchingVoice
            }
        }

        val params = Bundle()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, UTTERANCE_ID_SPEAK)
    }

    fun speakTest(
        sampleText: String = "¡Qué onda carnal! Soy tu Cabegochi de bolsillo.",
        pitch: Float = 1.0f,
        rate: Float = 1.0f,
        voiceName: String? = null
    ) {
        speak(sampleText, pitch, rate, voiceName)
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
            _isSpeaking.value = false
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        _isSpeaking.value = false
    }
}
