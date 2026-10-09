package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkCanvasBg

/**
 * Atmospheric iOS 26 Liquid Glass Background Mesh with floating, pulsing fluid orbs
 */
@Composable
fun AmbientBackgroundMesh(
  modifier: Modifier = Modifier,
  reduceMotion: Boolean = false,
  glowIntensity: Float = 1.0f
) {
  val infiniteTransition = rememberInfiniteTransition(label = "ambient_mesh_transition")

  val pulse1 by infiniteTransition.animateFloat(
    initialValue = if (reduceMotion) 1f else 0.85f,
    targetValue = if (reduceMotion) 1f else 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(4200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "emerald_orb_pulse"
  )

  val pulse2 by infiniteTransition.animateFloat(
    initialValue = if (reduceMotion) 1f else 1.1f,
    targetValue = if (reduceMotion) 1f else 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(5300, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "gold_orb_pulse"
  )

  val pulse3 by infiniteTransition.animateFloat(
    initialValue = if (reduceMotion) 1f else 0.95f,
    targetValue = if (reduceMotion) 1f else 1.2f,
    animationSpec = infiniteRepeatable(
      animation = tween(4700, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "cyan_orb_pulse"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkCanvasBg)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // 1. Neon Emerald Top-Left Fluid Orb
      val alphaEmerald = (0.35f * glowIntensity).coerceIn(0.1f, 0.6f)
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF10B981).copy(alpha = alphaEmerald),
            Color(0xFF047857).copy(alpha = alphaEmerald * 0.5f),
            Color.Transparent
          ),
          center = Offset(w * 0.12f, h * 0.10f),
          radius = (w * 0.8f) * pulse1
        ),
        radius = (w * 0.8f) * pulse1,
        center = Offset(w * 0.12f, h * 0.10f)
      )

      // 2. Liquid Gold Center-Right Solar Orb
      val alphaGold = (0.28f * glowIntensity).coerceIn(0.08f, 0.5f)
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFFF59E0B).copy(alpha = alphaGold),
            Color(0xFFD97706).copy(alpha = alphaGold * 0.45f),
            Color.Transparent
          ),
          center = Offset(w * 0.88f, h * 0.42f),
          radius = (w * 0.7f) * pulse2
        ),
        radius = (w * 0.7f) * pulse2,
        center = Offset(w * 0.88f, h * 0.42f)
      )

      // 3. Azure Cyan Refraction Orb (Mid-Left)
      val alphaCyan = (0.18f * glowIntensity).coerceIn(0.05f, 0.35f)
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF06B6D4).copy(alpha = alphaCyan),
            Color(0xFF0E7490).copy(alpha = alphaCyan * 0.4f),
            Color.Transparent
          ),
          center = Offset(w * 0.05f, h * 0.65f),
          radius = (w * 0.65f) * pulse3
        ),
        radius = (w * 0.65f) * pulse3,
        center = Offset(w * 0.05f, h * 0.65f)
      )

      // 4. Deep Obsidian / Emerald Glow on Base
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF059669).copy(alpha = (0.22f * glowIntensity).coerceIn(0.05f, 0.4f)),
            Color(0xFF064E3B).copy(alpha = 0.1f),
            Color.Transparent
          ),
          center = Offset(w * 0.5f, h * 0.96f),
          radius = w * 0.85f
        ),
        radius = w * 0.85f,
        center = Offset(w * 0.5f, h * 0.96f)
      )
    }
  }
}

/**
 * Reusable modifier to convert any container into an iOS 26 Liquid Glass card
 * with layered specular refraction, translucent frosted acrylic, and radiant edge borders.
 */
fun Modifier.liquidGlassEffect(
  shape: Shape = RoundedCornerShape(22.dp),
  elevation: Dp = 10.dp,
  highContrast: Boolean = false,
  borderBrush: Brush? = null,
  backgroundBrush: Brush? = null
): Modifier {
  val finalBorderBrush = borderBrush ?: if (highContrast) {
    Brush.linearGradient(
      listOf(
        Color(0xFFE2E8F0),
        Color(0xCC34D399),
        Color(0xCCFBBF24),
        Color(0xFF94A3B8)
      )
    )
  } else {
    Brush.linearGradient(
      listOf(
        Color(0x66FFFFFF),
        Color(0x1AFFFFFF),
        Color(0x4038BDF8),
        Color(0x4410B981),
        Color(0x40F59E0B)
      )
    )
  }

  val finalBackgroundBrush = backgroundBrush ?: if (highContrast) {
    Brush.linearGradient(
      listOf(
        Color(0x33FFFFFF),
        Color(0x2E0B1912),
        Color(0x40020B06)
      )
    )
  } else {
    Brush.linearGradient(
      listOf(
        Color(0x22FFFFFF),
        Color(0x121A3024),
        Color(0x1C06120B)
      )
    )
  }

  val borderWidth = if (highContrast) 2.dp else 1.2.dp

  val shadowModifier = if (elevation > 0.dp) {
    this.shadow(
      elevation = elevation.coerceAtMost(4.dp),
      shape = shape,
      spotColor = Color(0x1F000000),
      ambientColor = Color(0x14000000)
    )
  } else {
    this
  }

  return shadowModifier
    .clip(shape)
    .background(finalBackgroundBrush)
    .border(borderWidth, finalBorderBrush, shape)
}

/**
 * Tactile Liquid Glass Interactive Button Modifier with bouncy spring press feedback
 */
@Composable
fun Modifier.liquidGlassButton(
  shape: Shape = RoundedCornerShape(18.dp),
  elevation: Dp = 0.dp,
  reduceMotion: Boolean = false,
  highContrast: Boolean = false,
  onClick: () -> Unit
): Modifier {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale = if (!reduceMotion && isPressed) 0.95f else 1.0f

  return this
    .scale(scale)
    .liquidGlassEffect(
      shape = shape,
      elevation = elevation,
      highContrast = highContrast
    )
    .clickable(
      interactionSource = interactionSource,
      indication = null
    ) { onClick() }
}

