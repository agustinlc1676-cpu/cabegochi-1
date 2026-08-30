package com.example.cabegochi.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class SpeechInputState {
    IDLE,
    LISTENING,
    PROCESSING,
    ERROR
}

class SpeechToTextManager(private val context: Context) {

    companion object {
        private const val TAG = "SpeechToTextManager"
    }

    private var speechRecognizer: SpeechRecognizer? = null

    private val _inputState = MutableStateFlow(SpeechInputState.IDLE)
    val inputState: StateFlow<SpeechInputState> = _inputState.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    val isAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    fun startListening(onResult: (String) -> Unit) {
        if (!isAvailable) {
            Log.w(TAG, "SpeechRecognizer is not available on this device.")
            _inputState.value = SpeechInputState.ERROR
            return
        }

        stopListening()

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _inputState.value = SpeechInputState.LISTENING
                    }

                    override fun onBeginningOfSpeech() {
                        _inputState.value = SpeechInputState.LISTENING
                    }

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _inputState.value = SpeechInputState.PROCESSING
                    }

                    override fun onError(error: Int) {
                        Log.w(TAG, "Speech recognition error code: $error")
                        _inputState.value = SpeechInputState.ERROR
                    }

                    override fun onResults(results: Bundle?) {
                        _inputState.value = SpeechInputState.IDLE
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _recognizedText.value = text
                            onResult(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val partial = matches?.firstOrNull() ?: ""
                        if (partial.isNotBlank()) {
                            _recognizedText.value = partial
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "MX").toString())
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-MX")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
            _inputState.value = SpeechInputState.LISTENING
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start speech recognizer: ${e.message}", e)
            _inputState.value = SpeechInputState.ERROR
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
            _inputState.value = SpeechInputState.IDLE
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping recognizer: ${e.message}")
        }
    }
}
