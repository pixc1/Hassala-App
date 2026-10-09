package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.CircleMember
import com.example.ui.components.liquidGlassEffect
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.LiquidEmerald
import com.example.ui.theme.LiquidEmeraldNeon
import com.example.ui.theme.LiquidGold
import com.example.ui.theme.LiquidGoldBright
import com.example.ui.theme.TextCrystalWhite
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextSilver
import com.example.viewmodel.HassalaUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TimelineScreen(
  uiState: HassalaUiState,
  onMemberClick: (CircleMember) -> Unit,
  modifier: Modifier = Modifier
) {
  val isArabic = uiState.language == AppLanguage.ARABIC
  val circle = uiState.circle
  val currentCycleMonth = circle.currentCycleMonth
  val numberFormat = NumberFormat.getNumberInstance(Locale.US)
  val totalPool = if (uiState.members.isNotEmpty()) {
    uiState.members.sumOf { it.monthlyContribution }
  } else {
    circle.monthlyContribution * circle.totalMonths
  }
  val formattedPool = "${circle.currencySymbol}${numberFormat.format(totalPool)}"

  val sortedMembers = uiState.members.sortedBy { it.turnNumber }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    // Header
    Column {
      Text(
        text = if (isArabic) "جدول صرف الجمعية (الخط الزمني)" else "Payout Turn Schedule",
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        color = TextCrystalWhite
      )
      Text(
        text = if (isArabic) "دورة من ${circle.totalMonths} أشهر • استحقاق كل دور: $formattedPool"
        else "${circle.totalMonths}-Month cycle • Lump-sum: $formattedPool",
        fontFamily = CairoFontFamily,
        fontSize = 11.sp,
        color = TextSilver
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    if (sortedMembers.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 40.dp)
          .liquidGlassEffect(shape = RoundedCornerShape(22.dp))
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            tint = LiquidGoldBright,
            modifier = Modifier.size(40.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = if (isArabic) "الجدول الزمني بانتظار المشتركين" else "Timeline Waiting for Members",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextCrystalWhite
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (isArabic)
              "بمجرد إضافة المشتركين وتحديد أدوارهم من شاشة الأعضاء، سيتم رسم الجدول الزمني ومواعيد الصرف لكل شهر تلقائياً."
            else
              "Once you add members and assign their turn order, the monthly payout schedule will be generated automatically.",
            fontFamily = CairoFontFamily,
            fontSize = 12.sp,
            color = TextSilver,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }
    } else {
      // Interactive Vertical Timeline List in Liquid Glass
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("timeline_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(sortedMembers, key = { it.id }) { member ->
          val turn = member.turnNumber
          val isCompleted = turn < currentCycleMonth
          val isCurrent = turn == currentCycleMonth

          LiquidTimelineNodeItem(
            member = member,
            isCompleted = isCompleted,
            isCurrent = isCurrent,
            currencySymbol = circle.currencySymbol,
            formattedLumpSum = formattedPool,
            isArabic = isArabic,
            onClick = { onMemberClick(member) }
          )
        }

        item {
          Spacer(modifier = Modifier.height(90.dp))
        }
      }
    }
  }
}

@Composable
private fun LiquidTimelineNodeItem(
  member: CircleMember,
  isCompleted: Boolean,
  isCurrent: Boolean,
  currencySymbol: String,
  formattedLumpSum: String,
  isArabic: Boolean,
  onClick: () -> Unit
) {
  val name = if (isArabic) member.nameAr else member.nameEn
  val payoutMonth = if (isArabic) member.payoutMonthAr else member.payoutMonthEn

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .liquidGlassEffect(
        shape = RoundedCornerShape(18.dp),
        elevation = if (isCurrent) 10.dp else 4.dp,
        borderBrush = if (isCurrent) {
          Brush.linearGradient(listOf(LiquidGoldBright, LiquidEmeraldNeon))
        } else if (isCompleted) {
          Brush.linearGradient(listOf(Color(0x4010B981), Color(0x15FFFFFF)))
        } else {
          Brush.linearGradient(listOf(Color(0x25FFFFFF), Color(0x10FFFFFF)))
        }
      )
      .clickable { onClick() }
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Node Indicator + Member Details
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        // Timeline Round Number Sphere
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
              when {
                isCurrent -> Brush.linearGradient(listOf(LiquidGoldBright, Color(0xFFB45309)))
                isCompleted -> Brush.linearGradient(listOf(LiquidEmeraldNeon, Color(0xFF047857)))
                else -> Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x11FFFFFF)))
              }
            )
            .border(
              1.5.dp,
              if (isCurrent) Color.White else Color(0x40FFFFFF),
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          if (isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Completed",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          } else {
            Text(
              text = "#${member.turnNumber}",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = Color.White
            )
          }
        }

        // Details Column (Ellipsis to prevent pushing lump sum)
        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = name,
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = TextCrystalWhite,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            if (isCurrent) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0x33F59E0B))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (isArabic) "هذا الشهر" else "Active",
                  fontFamily = CairoFontFamily,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = LiquidGoldBright
                )
              }
            }
          }

          Text(
            text = if (isArabic) "استحقاق: $payoutMonth • القسط: $currencySymbol${member.monthlyContribution}"
            else "Payout: $payoutMonth • Dues: $currencySymbol${member.monthlyContribution}",
            fontFamily = CairoFontFamily,
            fontSize = 11.sp,
            color = TextSilver,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Right: Lump sum or status badge
      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = formattedLumpSum,
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = if (isCurrent) LiquidGoldBright else if (isCompleted) LiquidEmeraldNeon else TextCrystalWhite
        )
        Text(
          text = when {
            isCompleted -> if (isArabic) "تم الاستلام ✓" else "Received ✓"
            isCurrent -> if (isArabic) "قيد الصرف 👑" else "Disbursing 👑"
            else -> if (isArabic) "قادم" else "Upcoming"
          },
          fontFamily = CairoFontFamily,
          fontSize = 10.sp,
          color = when {
            isCompleted -> LiquidEmeraldNeon
            isCurrent -> LiquidGoldBright
            else -> TextMutedGray
          }
        )
      }
    }
  }
}
