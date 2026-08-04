package com.example.noteapp.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.Locale
import javax.inject.Inject

class TtsManager @Inject constructor(
    private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val faLocale = Locale("fa", "IR")
                val result = tts?.setLanguage(faLocale)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.US)
                    Toast.makeText(context, "Persian not supported, using English", Toast.LENGTH_SHORT).show()
                }

                tts?.setSpeechRate(1.0f)
                tts?.setPitch(1.0f)
                isReady = true
            } else {
                isReady = false
                Toast.makeText(context, "Text-to-Speech not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun speak(text: String) {
        if (!isReady || text.isBlank()) {
            Toast.makeText(context, "TTS not ready or text empty", Toast.LENGTH_SHORT).show()
            return
        }
        val locale = if (hasPersianChars(text)) {
            Locale("fa", "IR")
        } else {
            Locale.US
        }

        val langResult = tts?.isLanguageAvailable(locale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale.US)
            Toast.makeText(context, "Persian language not supported, reading in English", Toast.LENGTH_SHORT).show()
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            tts?.setLanguage(locale)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
        isReady = false
    }

    private fun hasPersianChars(text: String): Boolean {
        val persianPattern = Regex("[\\u0600-\\u06FF\\uFB8A-\\uFBFE]")
        return persianPattern.containsMatchIn(text)
    }
}