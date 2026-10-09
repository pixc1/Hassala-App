package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MotionPhotosOff
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.components.liquidGlassButton
import com.example.ui.components.liquidGlassEffect
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.LiquidEmeraldNeon
import com.example.ui.theme.LiquidGoldBright
import com.example.ui.theme.TextCrystalWhite
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextSilver
import com.example.viewmodel.HassalaUiState

@Composable
fun SettingsScreen(
  uiState: HassalaUiState,
  onSpeakCircleSummary: () -> Unit,
  onStopSpeaking: () -> Unit,
  onTestVoice: () -> Unit,
  onSetSpeechRate: (Float) -> Unit,
  onToggleAutoRead: () -> Unit,
  onToggleHighContrast: () -> Unit,
  onSetFontSizeScale: (Float) -> Unit,
  onToggleReduceMotion: () -> Unit,
  onToggleHaptics: () -> Unit,
  onSetGlowIntensity: (Float) -> Unit,
  onConfigureCircleClick: () -> Unit,
  onShareJsonClick: () -> Unit,
  onImportJsonClick: () -> Unit,
  onExportPdfClick: () -> Unit,
  onViewReceiptsClick: () -> Unit,
  onViewSecurityClick: () -> Unit,
  onToggleLanguage: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isArabic = uiState.language == AppLanguage.ARABIC
  val highContrast = uiState.highContrastMode
  val reduceMotion = uiState.reduceMotion
  val fontScale = uiState.fontSizeScale

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header Glass Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .liquidGlassEffect(
          shape = RoundedCornerShape(24.dp),
          highContrast = highContrast,
          borderBrush = Brush.linearGradient(listOf(LiquidEmeraldNeon, LiquidGoldBright))
        )
        .padding(18.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(LiquidEmeraldNeon)
            )
            Text(
              text = if (isArabic) "مركز التحكم وإمكانات الوصول" else "ACCESSIBILITY & CONTROL",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = (11 * fontScale).sp,
              color = LiquidEmeraldNeon,
              letterSpacing = 0.5.sp
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = if (isArabic) "الإعدادات وسهولة الاستخدام" else "Settings & Accessibility",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (19 * fontScale).sp,
            color = TextCrystalWhite
          )

          Text(
            text = if (isArabic)
              "تخصيص القراءة الصوتية، التباين، حجم الخط، وحركات الليكود جلاس"
            else
              "Customize Text-to-Speech, Contrast, Font Sizing & Liquid Glass animations",
            fontFamily = CairoFontFamily,
            fontSize = (12 * fontScale).sp,
            color = TextSilver
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // SECTION 1: 🗣️ القارئ الصوتي وقراءة النصوص (Text-to-Speech Accessibility)
    SettingsSectionHeader(
      title = if (isArabic) "قارئ الشاشة الصوتي (Text-to-Speech)" else "Voice Reader & Speech",
      icon = Icons.Default.VolumeUp,
      accentColor = LiquidEmeraldNeon,
      fontSize = (14 * fontScale).sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .liquidGlassEffect(shape = RoundedCornerShape(22.dp), highContrast = highContrast)
        .padding(18.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Visualizer indicator when speaking
        AnimatedVisibility(
          visible = uiState.isTtsSpeaking,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0x3310B981))
              .border(1.dp, Color(0x6634D399), RoundedCornerShape(14.dp))
              .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              PulsingAudioWaves(reduceMotion = reduceMotion)
              Text(
                text = if (isArabic) "جارٍ قراءة بيانات الجمعية بالصوت..." else "Speaking circle summary aloud...",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (12 * fontScale).sp,
                color = LiquidEmeraldNeon
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFEF4444))
                .clickable { onStopSpeaking() }
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (isArabic) "إيقاف" else "Stop",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.White
              )
            }
          }
        }

        if (uiState.isTtsSpeaking) {
          Spacer(modifier = Modifier.height(12.dp))
        }

        // Primary Action Button: Read Circle Summary Aloud
        Button(
          onClick = {
            if (uiState.isTtsSpeaking) onStopSpeaking() else onSpeakCircleSummary()
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("tts_summary_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (uiState.isTtsSpeaking) Color(0xFFEF4444) else LiquidEmeraldNeon
          )
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
          ) {
            Icon(
              imageVector = if (uiState.isTtsSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
              contentDescription = null,
              tint = if (uiState.isTtsSpeaking) Color.White else Color(0xFF021B10),
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = if (uiState.isTtsSpeaking) {
                if (isArabic) "إيقاف الصوت الآن" else "Stop Reading Aloud"
              } else {
                if (isArabic) "🔊 استمع للملخص الصوتي للجمعية" else "🔊 Listen to Circle Summary"
              },
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = (14 * fontScale).sp,
              color = if (uiState.isTtsSpeaking) Color.White else Color(0xFF021B10)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Speech Rate Controls
        Text(
          text = if (isArabic) "سرعة القراءة الصوتية:" else "Speech Rate:",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = (12 * fontScale).sp,
          color = TextSilver
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          SpeechRateButton(
            label = if (isArabic) "بطيء 0.8x" else "Slow 0.8x",
            selected = uiState.ttsSpeechRate <= 0.85f,
            onClick = { onSetSpeechRate(0.8f) },
            fontScale = fontScale,
            modifier = Modifier.weight(1f)
          )

          SpeechRateButton(
            label = if (isArabic) "عادي 1.0x" else "Normal 1.0x",
            selected = uiState.ttsSpeechRate in 0.9f..1.1f,
            onClick = { onSetSpeechRate(1.0f) },
            fontScale = fontScale,
            modifier = Modifier.weight(1f)
          )

          SpeechRateButton(
            label = if (isArabic) "سريع 1.3x" else "Fast 1.3x",
            selected = uiState.ttsSpeechRate >= 1.2f,
            onClick = { onSetSpeechRate(1.3f) },
            fontScale = fontScale,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Test Voice button
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x1AFFFFFF))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
            .clickable { onTestVoice() }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (isArabic) "تجربة نبرة الصوت والمخارج" else "Test Voice Sample",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (12 * fontScale).sp,
            color = LiquidGoldBright
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // SECTION 2: 👁️ إمكانات الرؤية والتباين (Visual Accessibility)
    SettingsSectionHeader(
      title = if (isArabic) "الرؤية والخطوط (Visual Accessibility)" else "Visual & Typography",
      icon = Icons.Default.Contrast,
      accentColor = LiquidGoldBright,
      fontSize = (14 * fontScale).sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .liquidGlassEffect(shape = RoundedCornerShape(22.dp), highContrast = highContrast)
        .padding(18.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // High Contrast Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (isArabic) "وضع التباين العالي (High Contrast)" else "High Contrast Mode",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = (13 * fontScale).sp,
              color = TextCrystalWhite
            )
            Text(
              text = if (isArabic)
                "يعزز سطوع الحدود وخلفيات البطاقات لسهولة القراءة"
              else
                "Sharpens card borders & backgrounds for easier reading",
              fontFamily = CairoFontFamily,
              fontSize = (11 * fontScale).sp,
              color = TextSilver
            )
          }

          Switch(
            checked = highContrast,
            onCheckedChange = { onToggleHighContrast() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color(0xFF021B10),
              checkedTrackColor = LiquidEmeraldNeon,
              uncheckedThumbColor = TextMutedGray,
              uncheckedTrackColor = Color(0x33FFFFFF)
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Font Size Multiplier
        Text(
          text = if (isArabic) "حجم خط النصوص:" else "Font Size Scale:",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = (12 * fontScale).sp,
          color = TextSilver
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          SpeechRateButton(
            label = if (isArabic) "عادي 100%" else "Normal",
            selected = fontScale <= 1.05f,
            onClick = { onSetFontSizeScale(1.0f) },
            fontScale = fontScale,
            modifier = Modifier.weight(1f)
          )

          SpeechRateButton(
            label = if (isArabic) "كبير 115%" else "Large",
            selected = fontScale in 1.1f..1.25f,
            onClick = { onSetFontSizeScale(1.15f) },
            fontScale = fontScale,
            modifier = Modifier.weight(1f)
          )

          SpeechRateButton(
            label = if (isArabic) "كبير جداً 130%" else "X-Large",
            selected = fontScale >= 1.28f,
            onClick = { onSetFontSizeScale(1.30f) },
            fontScale = fontScale,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // SECTION 3: 💫 الحركة وتأثيرات الليكود جلاس (Motion & Liquid Glass)
    SettingsSectionHeader(
      title = if (isArabic) "الحركة والاهتزاز وتأثير الزجاج" else "Motion, Haptics & Glass",
      icon = Icons.Default.MotionPhotosOff,
      accentColor = Color(0xFF38BDF8),
      fontSize = (14 * fontScale).sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .liquidGlassEffect(shape = RoundedCornerShape(22.dp), highContrast = highContrast)
        .padding(18.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Reduce Motion Switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (isArabic) "تقليل الحركة (Reduce Motion)" else "Reduce Motion",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = (13 * fontScale).sp,
              color = TextCrystalWhite
            )
            Text(
              text = if (isArabic)
                "يقلل الحركات والتحولات للأشخاص الذين يعانون من الحساسية"
              else
                "Disables heavy animations and background pulsing",
              fontFamily = CairoFontFamily,
              fontSize = (11 * fontScale).sp,
              color = TextSilver
            )
          }

          Switch(
            checked = reduceMotion,
            onCheckedChange = { onToggleReduceMotion() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color(0xFF021B10),
              checkedTrackColor = Color(0xFF38BDF8),
              uncheckedThumbColor = TextMutedGray,
              uncheckedTrackColor = Color(0x33FFFFFF)
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Haptic Feedback Switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (isArabic) "الاهتزاز اللمسي التفاعلي (Haptics)" else "Haptic Touch Feedback",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = (13 * fontScale).sp,
              color = TextCrystalWhite
            )
            Text(
              text = if (isArabic)
                "اهتزاز خفيف وذكي عند لمس الأزرار وتأكيد الدفع"
              else
                "Subtle physical vibration on taps and actions",
              fontFamily = CairoFontFamily,
              fontSize = (11 * fontScale).sp,
              color = TextSilver
            )
          }

          Switch(
            checked = uiState.hapticFeedbackEnabled,
            onCheckedChange = { onToggleHaptics() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color(0xFF021B10),
              checkedTrackColor = LiquidEmeraldNeon,
              uncheckedThumbColor = TextMutedGray,
              uncheckedTrackColor = Color(0x33FFFFFF)
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Glow Intensity
        Text(
          text = if (isArabic) "مستوى توهج زجاج الليكود جلاس:" else "Liquid Glass Glow Level:",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = (12 * fontScale).sp,
          color = TextSilver
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          SpeechRateButton(
            label = if (isArabic) "هادئ" else "Subtle",
            selected = uiState.glowIntensity < 1.0f,
            onClick = { onSetGlowIntensity(0.7f) },
            fontScale = fontScale,
            modifier = Modifier.weight(1f)
          )

          SpeechRateButton(
            label = if (isArabic) "متوازن" else "Balanced",
            selected = uiState.glowIntensity in 0.9f..1.1f,
            onClick = { onSetGlowIntensity(1.0f) },
            fontScale = fontScale,
            modifier = Modifier.weight(1f)
          )

          SpeechRateButton(
            label = if (isArabic) "فائق السطوع ✨" else "Vibrant ✨",
            selected = uiState.glowIntensity > 1.15f,
            onClick = { onSetGlowIntensity(1.3f) },
            fontScale = fontScale,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // SECTION 4: ⚙️ أدوات الجمعية والبيانات (Circle & Data Tools)
    SettingsSectionHeader(
      title = if (isArabic) "أدوات الجمعية والتقارير والبيانات" else "Circle Tools & Export",
      icon = Icons.Default.Tune,
      accentColor = LiquidGoldBright,
      fontSize = (14 * fontScale).sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .liquidGlassEffect(shape = RoundedCornerShape(22.dp), highContrast = highContrast)
        .padding(18.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        SettingsActionTile(
          icon = Icons.Default.Tune,
          title = if (isArabic) "تعديل تفاصيل الجمعية والعملة" else "Configure Circle & Currency",
          subtitle = if (isArabic) "تغيير اسم الجمعية، العملة، الأقساط، والمدة" else "Edit title, currency, amount, and months",
          accentColor = LiquidGoldBright,
          onClick = onConfigureCircleClick
        )

        SettingsActionTile(
          icon = Icons.Default.PictureAsPdf,
          title = if (isArabic) "تصدير تقرير PDF مفصل" else "Export Detailed PDF Report",
          subtitle = if (isArabic) "إنشاء مستند ملخص شامل لحفظه أو طباعته" else "Generate printable circle summary document",
          accentColor = Color(0xFFF43F5E),
          onClick = onExportPdfClick
        )

        SettingsActionTile(
          icon = Icons.Default.Share,
          title = if (isArabic) "مشاركة الجمعية كملف JSON" else "Share Circle as JSON",
          subtitle = if (isArabic) "تصدير ملف مشفر لإرساله للآخرين" else "Export circle configuration to send to others",
          accentColor = LiquidEmeraldNeon,
          onClick = onShareJsonClick
        )

        SettingsActionTile(
          icon = Icons.Default.CloudDownload,
          title = if (isArabic) "استيراد جمعية من ملف أو نص JSON" else "Import Circle from JSON",
          subtitle = if (isArabic) "استرجاع أو تحميل جمعية مشتركة" else "Restore or load a shared circle file",
          accentColor = Color(0xFF38BDF8),
          onClick = onImportJsonClick
        )

        SettingsActionTile(
          icon = Icons.Default.Receipt,
          title = if (isArabic) "سجلات وسندات الدفع الرسمية" else "Official Payment Receipts",
          subtitle = if (isArabic) "استعراض السندات الموثقة لجميع العمليات" else "View verified transaction audit trail",
          accentColor = LiquidEmeraldNeon,
          onClick = onViewReceiptsClick
        )

        SettingsActionTile(
          icon = Icons.Default.Security,
          title = if (isArabic) "صندوق الضمان والتوثيق الشرعي" else "Escrow Vault & Sharia Certified",
          subtitle = if (isArabic) "رقم الحفظ الائتماني ومعايير الحماية" else "Security credentials & certification details",
          accentColor = LiquidGoldBright,
          onClick = onViewSecurityClick
        )

        SettingsActionTile(
          icon = Icons.Outlined.Language,
          title = if (isArabic) "تبديل اللغة (English / العربية)" else "Switch Language",
          subtitle = if (isArabic) "التحويل الفوري لواجهة التطبيق" else "Switch app interface language",
          accentColor = Color(0xFFA855F7),
          onClick = onToggleLanguage
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // App Version Glass Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0x14FFFFFF))
        .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(16.dp))
        .padding(14.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "Hassala • حصّالة",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = TextCrystalWhite
        )
        Text(
          text = if (isArabic)
            "الإصدار 2.0 • تصميم Liquid Glass iOS 26 فائق الفخامة"
          else
            "Version 2.0 • iOS 26 Liquid Glass Ultra Edition",
          fontFamily = CairoFontFamily,
          fontSize = 11.sp,
          color = TextSilver
        )
      }
    }

    Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom dock
  }
}

@Composable
private fun SettingsSectionHeader(
  title: String,
  icon: ImageVector,
  accentColor: Color,
  fontSize: androidx.compose.ui.unit.TextUnit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = accentColor,
      modifier = Modifier.size(18.dp)
    )
    Text(
      text = title,
      fontFamily = CairoFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = fontSize,
      color = TextCrystalWhite
    )
  }
}

@Composable
private fun SpeechRateButton(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  fontScale: Float,
  modifier: Modifier = Modifier
) {
  val bgColor by animateColorAsState(
    targetValue = if (selected) Color(0x3D10B981) else Color(0x14FFFFFF),
    label = "speech_btn_bg"
  )
  val borderColor by animateColorAsState(
    targetValue = if (selected) LiquidEmeraldNeon else Color(0x26FFFFFF),
    label = "speech_btn_border"
  )

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .border(1.dp, borderColor, RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 10.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      fontFamily = CairoFontFamily,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
      fontSize = (11 * fontScale).sp,
      color = if (selected) LiquidEmeraldNeon else TextSilver
    )
  }
}

@Composable
private fun PulsingAudioWaves(reduceMotion: Boolean) {
  val infiniteTransition = rememberInfiniteTransition(label = "audio_waves")

  val wave1 by infiniteTransition.animateFloat(
    initialValue = if (reduceMotion) 10f else 6f,
    targetValue = if (reduceMotion) 10f else 18f,
    animationSpec = infiniteRepeatable(
      animation = tween(350, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wave_1"
  )

  val wave2 by infiniteTransition.animateFloat(
    initialValue = if (reduceMotion) 14f else 16f,
    targetValue = if (reduceMotion) 14f else 8f,
    animationSpec = infiniteRepeatable(
      animation = tween(420, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wave_2"
  )

  val wave3 by infiniteTransition.animateFloat(
    initialValue = if (reduceMotion) 8f else 5f,
    targetValue = if (reduceMotion) 8f else 15f,
    animationSpec = infiniteRepeatable(
      animation = tween(380, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wave_3"
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(3.dp),
    modifier = Modifier.height(20.dp)
  ) {
    Box(
      modifier = Modifier
        .width(3.dp)
        .height(wave1.dp)
        .clip(CircleShape)
        .background(LiquidEmeraldNeon)
    )
    Box(
      modifier = Modifier
        .width(3.dp)
        .height(wave2.dp)
        .clip(CircleShape)
        .background(LiquidGoldBright)
    )
    Box(
      modifier = Modifier
        .width(3.dp)
        .height(wave3.dp)
        .clip(CircleShape)
        .background(LiquidEmeraldNeon)
    )
  }
}

@Composable
private fun SettingsActionTile(
  icon: ImageVector,
  title: String,
  subtitle: String,
  accentColor: Color,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color(0x14FFFFFF))
      .border(1.dp, Color(0x24FFFFFF), RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(accentColor.copy(alpha = 0.15f))
          .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(19.dp)
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = TextCrystalWhite
        )
        Text(
          text = subtitle,
          fontFamily = CairoFontFamily,
          fontSize = 10.sp,
          color = TextSilver
        )
      }
    }
  }
}
