package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.LiquidEmerald
import com.example.ui.theme.LiquidEmeraldNeon
import com.example.ui.theme.LiquidGold
import com.example.ui.theme.LiquidGoldBright
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.TajawalFontFamily
import com.example.ui.theme.TextCrystalWhite
import com.example.ui.theme.TextSilver
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HassalaCircularProgress(
  progress: Float,
  collectedAmount: Long,
  totalPool: Long,
  currencySymbol: String,
  currentRound: Int,
  totalRounds: Int,
  paidMembersCount: Int,
  totalMembersCount: Int,
  language: AppLanguage,
  modifier: Modifier = Modifier,
  size: Dp = 270.dp,
  strokeWidth: Dp = 18.dp
) {
  val isArabic = language == AppLanguage.ARABIC

  val animatedProgress by animateFloatAsState(
    targetValue = progress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
    label = "liquid_progress_anim"
  )

  val numberFormat = NumberFormat.getNumberInstance(Locale.US)
  val formattedCollected = "$currencySymbol${numberFormat.format(collectedAmount)}"
  val formattedTotal = "$currencySymbol${numberFormat.format(totalPool)}"
  val percentInt = (progress * 100).toInt()

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    // Ambient liquid glass orbital halo
    Box(
      modifier = Modifier
        .size(size - 24.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(
              Color(0x3810B981),
              Color(0x22F59E0B),
              Color(0x0A000000),
              Color.Transparent
            )
          )
        )
        .border(
          1.dp,
          Brush.linearGradient(
            listOf(Color(0x40FFFFFF), Color(0x10FFFFFF), Color(0x30F59E0B))
          ),
          CircleShape
        )
    )

    // Canvas drawing glowing track & sweep neon arc
    Canvas(modifier = Modifier.fillMaxSize()) {
      val strokePx = strokeWidth.toPx()
      val arcDiameter = this.size.minDimension - strokePx
      val arcTopLeft = Offset(strokePx / 2, strokePx / 2)
      val arcSize = Size(arcDiameter, arcDiameter)

      // Background glass track
      drawArc(
        color = Color(0x1AFFFFFF),
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = arcTopLeft,
        size = arcSize,
        style = Stroke(width = strokePx, cap = StrokeCap.Round)
      )

      // Foreground Liquid Sweep Gradient Arc
      if (animatedProgress > 0f) {
        val sweepGradient = Brush.sweepGradient(
          0.0f to Color(0xFF047857),
          0.35f to LiquidEmeraldNeon,
          0.7f to LiquidGold,
          1.0f to LiquidGoldBright,
          center = center
        )

        drawArc(
          brush = sweepGradient,
          startAngle = -90f,
          sweepAngle = 360f * animatedProgress,
          useCenter = false,
          topLeft = arcTopLeft,
          size = arcSize,
          style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )
      }
    }

    // Inside Ring: Liquid Glass Statistics Core
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(horizontal = 24.dp)
    ) {
      // Floating Round Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0x33F59E0B))
          .border(1.dp, Color(0x80F59E0B), RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Text(
          text = if (isArabic) "الجولة $currentRound من $totalRounds" else "Round $currentRound of $totalRounds",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = LiquidGoldBright
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Primary Collected Amount
      Text(
        text = formattedCollected,
        fontFamily = if (isArabic) CairoFontFamily else OutfitFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        color = TextCrystalWhite
      )

      // Goal Subtitle
      Text(
        text = if (isArabic) "من إجمالي $formattedTotal" else "of $formattedTotal Target",
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = TextSilver
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Status Pill: Paid members count in liquid glass
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0x2E10B981))
          .border(1.dp, Color(0x8034D399), RoundedCornerShape(20.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(LiquidEmeraldNeon)
          )
          Text(
            text = if (isArabic) "$paidMembersCount من $totalMembersCount سددوا ($percentInt٪)"
            else "$paidMembersCount/$totalMembersCount Paid ($percentInt%)",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = LiquidEmeraldNeon
          )
        }
      }
    }
  }
}
