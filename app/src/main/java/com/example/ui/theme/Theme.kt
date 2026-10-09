package com.example.ui.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

private val DarkLiquidGlassColorScheme = darkColorScheme(
  primary = LiquidEmeraldNeon,
  onPrimary = Color(0xFF022C22),
  primaryContainer = LiquidEmeraldDeep,
  onPrimaryContainer = TextCrystalWhite,
  secondary = LiquidGold,
  onSecondary = Color(0xFF1E1B00),
  secondaryContainer = Color(0x33F59E0B),
  onSecondaryContainer = LiquidGoldBright,
  tertiary = LiquidGoldBright,
  onTertiary = Color.Black,
  background = DarkCanvasBg,
  onBackground = TextCrystalWhite,
  surface = DarkCanvasSurface,
  onSurface = TextCrystalWhite,
  surfaceVariant = LiquidGlassBackground,
  onSurfaceVariant = TextSilver,
  outline = GlassBorderHighlight,
  outlineVariant = GlassBorderSubtle
)

@Composable
fun HassalaTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkLiquidGlassColorScheme,
    typography = Typography
  ) {
    // Ensure every single Text composable across the app defaults to CairoFontFamily
    CompositionLocalProvider(
      LocalTextStyle provides TextStyle(fontFamily = CairoFontFamily)
    ) {
      content()
    }
  }
}
