package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import java.util.UUID

data class TtsState(
    val isInitialized: Boolean = false,
    val isHebrewSupported: Boolean = true,
    val activeText: String? = null,
    val noticeEventId: Long = 0L,
    val noticeText: String? = null,
)

class HebrewTtsManager(context: Context) {

    private val appContext = context.applicationContext
    private val _state = MutableStateFlow(TtsState())
    val state: StateFlow<TtsState> = _state.asStateFlow()

    private var tts: TextToSpeech? = null
    private var lastRequestedText: String? = null

    init {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val engine = tts
                val hebrewLocale = Locale.forLanguageTag("he-IL")
                val legacyHebrewLocale = Locale("iw", "IL")

                val primaryResult = engine?.setLanguage(hebrewLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED
                val supported = if (primaryResult == TextToSpeech.LANG_MISSING_DATA ||
                    primaryResult == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    val fallbackResult = engine?.setLanguage(legacyHebrewLocale)
                        ?: TextToSpeech.LANG_NOT_SUPPORTED
                    fallbackResult != TextToSpeech.LANG_MISSING_DATA &&
                        fallbackResult != TextToSpeech.LANG_NOT_SUPPORTED
                } else {
                    true
                }

                engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _state.update { it.copy(activeText = lastRequestedText) }
                    }

                    override fun onDone(utteranceId: String?) {
                        _state.update { it.copy(activeText = null) }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _state.update { it.copy(activeText = null) }
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _state.update { it.copy(activeText = null) }
                    }
                })

                _state.update {
                    it.copy(
                        isInitialized = true,
                        isHebrewSupported = supported,
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isInitialized = true,
                        isHebrewSupported = false,
                    )
                }
            }
        }
    }

    fun speak(text: String, speechRate: Float = 0.85f) {
        val cleanText = text.trim()
        if (cleanText.isEmpty()) return

        val engine = tts
        val currentState = _state.value
        if (engine == null || !currentState.isInitialized) {
            _state.update {
                it.copy(
                    noticeEventId = it.noticeEventId + 1,
                    noticeText = "Синтезатор мовлення ще ініціалізується…",
                )
            }
            return
        }

        if (!currentState.isHebrewSupported) {
            _state.update {
                it.copy(
                    noticeEventId = it.noticeEventId + 1,
                    noticeText = "Івритський голос TTS недоступний на пристрої. Перевірте налаштування мовлення (he-IL).",
                )
            }
            return
        }

        lastRequestedText = cleanText
        engine.setSpeechRate(speechRate.coerceIn(0.6f, 1.25f))
        val utteranceId = UUID.randomUUID().toString()
        val params = Bundle()
        _state.update { it.copy(activeText = cleanText) }
        val result = engine.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        if (result == TextToSpeech.ERROR) {
            _state.update {
                it.copy(
                    activeText = null,
                    noticeEventId = it.noticeEventId + 1,
                    noticeText = "Не вдалося відтворити озвучення.",
                )
            }
        }
    }

    fun stop() {
        tts?.stop()
        _state.update { it.copy(activeText = null) }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
