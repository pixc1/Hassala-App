package com.example.util

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.AppLanguage
import com.example.model.CircleMember
import com.example.model.MemberPaymentStatus
import com.example.model.MoneyCircle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) {

  private var tts: TextToSpeech? = null
  private var isInitialized = false

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _isArabicSupported = MutableStateFlow(true)
  val isArabicSupported: StateFlow<Boolean> = _isArabicSupported.asStateFlow()

  init {
    initializeTts()
  }

  private fun initializeTts() {
    try {
      tts = TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
          isInitialized = true
          // Try Arabic first, fallback to device default or English
          val arabicLocale = Locale("ar", "SA")
          val result = tts?.setLanguage(arabicLocale)
          if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            val generalArabic = Locale("ar")
            val generalResult = tts?.setLanguage(generalArabic)
            if (generalResult == TextToSpeech.LANG_MISSING_DATA || generalResult == TextToSpeech.LANG_NOT_SUPPORTED) {
              Log.w("TtsManager", "Arabic TTS not natively installed, falling back to default")
              tts?.setLanguage(Locale.getDefault())
              _isArabicSupported.value = false
            } else {
              _isArabicSupported.value = true
            }
          } else {
            _isArabicSupported.value = true
          }

          tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
              _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
              _isSpeaking.value = false
            }

            override fun onError(utteranceId: String?) {
              _isSpeaking.value = false
            }
          })
        } else {
          Log.e("TtsManager", "Failed to initialize TextToSpeech: status=$status")
          isInitialized = false
        }
      }
    } catch (e: Exception) {
      Log.e("TtsManager", "Error initializing TTS", e)
      isInitialized = false
    }
  }

  fun speak(text: String, language: AppLanguage = AppLanguage.ARABIC, speechRate: Float = 1.0f) {
    if (!isInitialized || tts == null) {
      initializeTts()
    }

    try {
      tts?.let { engine ->
        val targetLocale = if (language == AppLanguage.ARABIC) {
          Locale("ar")
        } else {
          Locale.US
        }
        engine.language = targetLocale
        engine.setSpeechRate(speechRate.coerceIn(0.5f, 2.0f))
        engine.setPitch(1.0f)

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "hassala_speech_${System.currentTimeMillis()}")
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, params, "hassala_speech_${System.currentTimeMillis()}")
        _isSpeaking.value = true
      }
    } catch (e: Exception) {
      Log.e("TtsManager", "Error speaking text", e)
      _isSpeaking.value = false
    }
  }

  fun stop() {
    try {
      tts?.stop()
      _isSpeaking.value = false
    } catch (e: Exception) {
      Log.e("TtsManager", "Error stopping TTS", e)
    }
  }

  fun speakCircleSummary(
    circle: MoneyCircle,
    members: List<CircleMember>,
    language: AppLanguage,
    speechRate: Float = 1.0f
  ) {
    val totalPaid = members.count { it.status == MemberPaymentStatus.PAID }
    val totalCount = members.size
    val recipient = members.firstOrNull { it.turnNumber == circle.currentCycleMonth } ?: members.firstOrNull()

    val textToRead = if (language == AppLanguage.ARABIC) {
      val circleName = circle.titleAr
      val recipientName = recipient?.nameAr ?: "لم يُحدد بعد"
      val currency = circle.currencySymbol
      val collected = totalPaid * circle.monthlyContribution
      val targetPool = if (members.isNotEmpty()) members.sumOf { it.monthlyContribution } else circle.monthlyContribution * circle.totalMonths
      val progressPercent = if (totalCount > 0) (totalPaid * 100) / totalCount else 0

      "ملخص صوتي لجمعية $circleName. " +
          "الجولة الحالية هي الجولة رقم ${circle.currentCycleMonth} من أصل ${circle.totalMonths} أشهر. " +
          "المستلم المؤهل لهذا الشهر هو: $recipientName. " +
          "إجمالي المبلغ المجموع حتى الآن هو: $collected $currency من أصل $targetPool $currency. " +
          "سدد $totalPaid أعضاء من إجمالي $totalCount أعضاء، بنسبة تحصيل بلغت $progressPercent بالمئة. " +
          "المبلغ الشهري لكل عضو هو: ${circle.monthlyContribution} $currency."
    } else {
      val circleName = circle.titleEn
      val recipientName = recipient?.nameEn ?: "Not assigned"
      val currency = circle.currencySymbol
      val collected = totalPaid * circle.monthlyContribution
      val targetPool = if (members.isNotEmpty()) members.sumOf { it.monthlyContribution } else circle.monthlyContribution * circle.totalMonths
      val progressPercent = if (totalCount > 0) (totalPaid * 100) / totalCount else 0

      "Audio summary for $circleName. " +
          "Current cycle is round ${circle.currentCycleMonth} of ${circle.totalMonths} months. " +
          "The payout recipient this month is: $recipientName. " +
          "Total collected so far is $collected $currency out of $targetPool $currency. " +
          "$totalPaid out of $totalCount members have paid, which is $progressPercent percent collection rate. " +
          "Monthly contribution per member is ${circle.monthlyContribution} $currency."
    }

    speak(textToRead, language, speechRate)
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
      tts = null
      isInitialized = false
    } catch (e: Exception) {
      Log.e("TtsManager", "Error shutting down TTS", e)
    }
  }
}
