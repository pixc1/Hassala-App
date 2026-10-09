package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Custom Cairo Font Family with full Arabic & Latin character sets in Normal, SemiBold, and Bold
val CairoFontFamily = FontFamily(
  Font(R.font.cairo, FontWeight.Normal),
  Font(R.font.cairo, FontWeight.Medium),
  Font(R.font.cairo_semibold, FontWeight.SemiBold),
  Font(R.font.cairo_bold, FontWeight.Bold),
  Font(R.font.cairo_bold, FontWeight.ExtraBold),
  Font(R.font.cairo_bold, FontWeight.Black)
)

// Outfit as supplementary font for Latin/numbers
val OutfitFontFamily = FontFamily(
  Font(R.font.outfit, FontWeight.Normal),
  Font(R.font.outfit, FontWeight.Medium),
  Font(R.font.outfit, FontWeight.SemiBold),
  Font(R.font.outfit, FontWeight.Bold)
)

// Legacy aliases all mapping to CairoFontFamily so every font reference resolves to Cairo
val TajawalFontFamily = CairoFontFamily
val JakartaFontFamily = CairoFontFamily

val Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = (-0.5).sp,
    color = TextCrystalWhite
  ),
  displayMedium = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 26.sp,
    lineHeight = 34.sp,
    letterSpacing = (-0.25).sp,
    color = TextCrystalWhite
  ),
  displaySmall = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    color = TextCrystalWhite
  ),
  headlineLarge = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    color = TextCrystalWhite
  ),
  headlineMedium = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 28.sp,
    color = TextCrystalWhite
  ),
  headlineSmall = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    color = TextCrystalWhite
  ),
  titleLarge = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.sp,
    color = TextCrystalWhite
  ),
  titleMedium = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.1.sp,
    color = TextCrystalWhite
  ),
  titleSmall = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    color = TextCrystalWhite
  ),
  bodyLarge = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.2.sp,
    color = TextSilver
  ),
  bodyMedium = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.2.sp,
    color = TextSilver
  ),
  bodySmall = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    color = TextSilver
  ),
  labelLarge = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp,
    color = TextCrystalWhite
  ),
  labelMedium = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.2.sp,
    color = TextSilver
  ),
  labelSmall = TextStyle(
    fontFamily = CairoFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.3.sp,
    color = TextMutedGray
  )
)
