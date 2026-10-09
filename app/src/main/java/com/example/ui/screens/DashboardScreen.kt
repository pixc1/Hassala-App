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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.components.HassalaCircularProgress
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
fun DashboardScreen(
  uiState: HassalaUiState,
  onPayDuesClick: () -> Unit,
  onAdvanceCycleMonth: () -> Unit,
  onViewMembersClick: () -> Unit,
  onViewSecurityClick: () -> Unit,
  onViewReceiptsClick: () -> Unit,
  onConfigureCircleClick: () -> Unit,
  onAddMemberClick: () -> Unit,
  onShareJsonClick: () -> Unit,
  onExportPdfClick: () -> Unit,
  onImportJsonClick: () -> Unit,
  onSpeakCircleSummary: () -> Unit = {},
  onStopSpeaking: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val isArabic = uiState.language == AppLanguage.ARABIC
  val fontScale = uiState.fontSizeScale
  val highContrast = uiState.highContrastMode
  val circle = uiState.circle
  val numberFormat = NumberFormat.getNumberInstance(Locale.US)

  val circleTitle = if (isArabic) circle.titleAr else circle.titleEn
  val circleCategory = if (isArabic) circle.categoryAr else circle.categoryEn
  val formattedMonthly = "${circle.currencySymbol}${numberFormat.format(circle.monthlyContribution)}"
  val totalExpectedPool = if (uiState.members.isNotEmpty()) {
    uiState.members.sumOf { it.monthlyContribution }
  } else {
    circle.monthlyContribution * circle.totalMonths
  }
  val formattedTotalPool = "${circle.currencySymbol}${numberFormat.format(totalExpectedPool)}"
  val nextPayoutDate = if (isArabic) circle.nextPayoutDateAr else circle.nextPayoutDateEn

  val recipient = uiState.currentMonthRecipient
  val hasMembers = uiState.members.isNotEmpty()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top Active Circle Header Liquid Glass Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .liquidGlassEffect(shape = RoundedCornerShape(24.dp))
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
                .size(8.dp)
                .clip(CircleShape)
                .background(LiquidEmeraldNeon)
            )
            Text(
              text = if (isArabic) "حلقة نشطة • الجولة ${circle.currentCycleMonth}/${circle.totalMonths}"
              else "ACTIVE CIRCLE • ROUND ${circle.currentCycleMonth}/${circle.totalMonths}",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = LiquidEmeraldNeon,
              letterSpacing = 0.5.sp
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = circleTitle,
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = TextCrystalWhite
          )

          Text(
            text = "$circleCategory • ${circle.currencySymbol}",
            fontFamily = CairoFontFamily,
            fontSize = 12.sp,
            color = TextSilver
          )
        }

        // Action: Configure Settings Button
        Box(
          modifier = Modifier
            .testTag("dashboard_configure_button")
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x2E10B981))
            .border(1.dp, Color(0x6634D399), RoundedCornerShape(14.dp))
            .clickable { onConfigureCircleClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = null,
              tint = LiquidGoldBright,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = if (isArabic) "إعدادات الجمعية" else "Settings",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = LiquidGoldBright
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // CLEAN SLATE ONBOARDING BANNER (Shown when no members exist yet)
    if (!hasMembers) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .liquidGlassEffect(
            shape = RoundedCornerShape(22.dp),
            borderBrush = Brush.linearGradient(listOf(LiquidGoldBright, LiquidEmeraldNeon))
          )
          .padding(18.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = if (isArabic) "✨ الجمعية جديدة وفارغة (تحكم كامل)" else "✨ Empty Slate Circle (Full Control)",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = LiquidGoldBright
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (isArabic)
              "بصفتك منسق الجمعية، أنت تحدد كل شيء: يمكنك الآن إضافة الأعضاء بأسمائهم وأرقامهم، أو استيراد جمعية جاهزة كملف JSON."
            else
              "As the organizer, you have full control. Add members with their turn order or import a shared circle JSON file.",
            fontFamily = CairoFontFamily,
            fontSize = 12.sp,
            color = TextCrystalWhite,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = onAddMemberClick,
              colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .background(
                  Brush.horizontalGradient(listOf(Color(0xFF047857), LiquidEmeraldNeon)),
                  RoundedCornerShape(12.dp)
                )
            ) {
              Icon(imageVector = Icons.Default.GroupAdd, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = if (isArabic) "إضافة عضو" else "Add Member", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = onImportJsonClick,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FFFFFF))
            ) {
              Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, tint = LiquidGoldBright, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = if (isArabic) "استيراد JSON" else "Import JSON", fontFamily = CairoFontFamily, fontSize = 12.sp, color = TextCrystalWhite)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // VOICE READER QUICK BANNER (Liquid Glass Accessibility Action)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .liquidGlassEffect(
          shape = RoundedCornerShape(20.dp),
          highContrast = highContrast,
          borderBrush = if (uiState.isTtsSpeaking) Brush.linearGradient(listOf(LiquidEmeraldNeon, Color(0xFF38BDF8))) else null
        )
        .clickable {
          if (uiState.isTtsSpeaking) onStopSpeaking() else onSpeakCircleSummary()
        }
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(if (uiState.isTtsSpeaking) Color(0x3310B981) else Color(0x1AFFFFFF))
              .border(1.dp, if (uiState.isTtsSpeaking) LiquidEmeraldNeon else Color(0x33FFFFFF), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (uiState.isTtsSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
              contentDescription = "Voice Reader",
              tint = if (uiState.isTtsSpeaking) LiquidEmeraldNeon else LiquidGoldBright,
              modifier = Modifier.size(20.dp)
            )
          }

          Column {
            Text(
              text = if (uiState.isTtsSpeaking) {
                if (isArabic) "جارٍ الاستماع للملخص الصوتي..." else "Reading aloud..."
              } else {
                if (isArabic) "استمع لملخص الجمعية بالصوت 🔊" else "Listen to Circle Summary 🔊"
              },
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = (13 * fontScale).sp,
              color = TextCrystalWhite
            )
            Text(
              text = if (isArabic) "قراءة تفاعلية فورية لأرقام الجمعية وحالة المستلم" else "Tap to hear round, pool & recipient details",
              fontFamily = CairoFontFamily,
              fontSize = (10 * fontScale).sp,
              color = TextSilver
            )
          }
        }

        if (uiState.isTtsSpeaking) {
          DashboardAudioEqualizer(reduceMotion = uiState.reduceMotion)
        } else {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0x2610B981))
              .border(1.dp, Color(0x6634D399), RoundedCornerShape(12.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = if (isArabic) "استمع الآن" else "Play",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = (11 * fontScale).sp,
              color = LiquidEmeraldNeon
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // CENTERPIECE: Liquid Glass Circular Progress Ring
    HassalaCircularProgress(
      progress = uiState.progressFraction,
      collectedAmount = uiState.collectedAmountThisMonth,
      totalPool = totalExpectedPool,
      currencySymbol = circle.currencySymbol,
      currentRound = circle.currentCycleMonth,
      totalRounds = circle.totalMonths,
      paidMembersCount = uiState.paidMembersCount,
      totalMembersCount = uiState.totalMembersCount,
      language = uiState.language,
      modifier = Modifier.testTag("circular_progress_chart")
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Responsive 2-Tier Liquid Glass Metrics (Prevents text clipping on all devices)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Metric 1: Monthly Contribution
      LiquidStatCard(
        icon = Icons.Default.Payments,
        title = if (isArabic) "القسط الشهري" else "Monthly Dues",
        value = formattedMonthly,
        subtitle = if (isArabic) "لكل مشترك شهرياً" else "Per Member Monthly",
        accentColor = LiquidEmeraldNeon,
        isArabic = isArabic,
        modifier = Modifier.weight(1f)
      )

      // Metric 2: Next Payout Date
      LiquidStatCard(
        icon = Icons.Default.CalendarToday,
        title = if (isArabic) "الصرف القادم" else "Next Payout",
        value = nextPayoutDate,
        subtitle = if (isArabic) "${circle.daysRemaining} يوم متبقي للجولة" else "${circle.daysRemaining} Days Left",
        accentColor = LiquidGoldBright,
        isArabic = isArabic,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Metric 3: Members Count & Collection Progress (Full Width Card)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .liquidGlassEffect(shape = RoundedCornerShape(18.dp), elevation = 0.dp)
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(LiquidEmerald.copy(alpha = 0.2f))
              .border(1.dp, Color(0x3310B981), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = LiquidEmeraldNeon,
              modifier = Modifier.size(18.dp)
            )
          }

          Column {
            Text(
              text = if (isArabic) "المشتركون وحالة السداد" else "Members & Collection Status",
              fontFamily = CairoFontFamily,
              fontSize = 11.sp,
              color = TextSilver
            )
            Text(
              text = "${uiState.members.size} / ${circle.totalMonths} ${if (isArabic) "مشترك" else "Members"} • ${uiState.paidMembersCount} ${if (isArabic) "سددوا بالفعل" else "Paid"}",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = TextCrystalWhite,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // Live progress pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (uiState.paidMembersCount > 0) Color(0x3310B981) else Color(0x22F59E0B))
            .border(1.dp, if (uiState.paidMembersCount > 0) Color(0x6610B981) else Color(0x4DF59E0B), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text(
            text = "${(uiState.progressFraction * 100).toInt()}% ${if (isArabic) "مكتمل" else "Paid"}",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = if (uiState.paidMembersCount > 0) LiquidEmeraldNeon else LiquidGoldBright
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Recipient Spotlight Card
    if (recipient != null) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .liquidGlassEffect(
            shape = RoundedCornerShape(20.dp),
            borderBrush = Brush.linearGradient(listOf(Color(0x66F59E0B), Color(0x22FFFFFF)))
          )
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(listOf(LiquidGold, Color(0xFFB45309)))
                )
                .border(1.5.dp, Color(0x80FFFFFF), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = recipient.avatarInitials,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
              )
            }

            Column {
              Text(
                text = if (isArabic) "مستحق هذا الشهر (${recipient.payoutMonthAr}) 👑"
                else "Current Recipient (${recipient.payoutMonthEn}) 👑",
                fontFamily = CairoFontFamily,
                fontSize = 11.sp,
                color = LiquidGoldBright,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (isArabic) recipient.nameAr else recipient.nameEn,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextCrystalWhite
              )
              Text(
                text = if (isArabic) "إجمالي استحقاق الدفعة: $formattedTotalPool"
                else "Total payout: $formattedTotalPool",
                fontFamily = CairoFontFamily,
                fontSize = 11.sp,
                color = TextSilver
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0x2910B981))
              .border(1.dp, LiquidEmeraldNeon, RoundedCornerShape(12.dp))
              .clickable { onViewMembersClick() }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = if (isArabic) "عرض الكشف" else "View",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = LiquidEmeraldNeon
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // High-Priority Action Strip: Share JSON, Export PDF, Add Member
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Button: Share JSON
      OutlinedButton(
        onClick = onShareJsonClick,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .weight(1f)
          .testTag("dashboard_share_json_action"),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4010B981))
      ) {
        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = LiquidEmeraldNeon, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isArabic) "مشاركة JSON" else "Share JSON",
          fontFamily = CairoFontFamily,
          fontSize = 11.sp,
          color = TextCrystalWhite
        )
      }

      // Button: Export PDF
      OutlinedButton(
        onClick = onExportPdfClick,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .weight(1f)
          .testTag("dashboard_export_pdf_action"),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x40F43F5E))
      ) {
        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isArabic) "تقرير PDF" else "PDF Report",
          fontFamily = CairoFontFamily,
          fontSize = 11.sp,
          color = TextCrystalWhite
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Primary CTA Button: Pay Dues or Add Members
    if (hasMembers) {
      Button(
        onClick = onPayDuesClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("pay_dues_button")
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFF047857), LiquidEmeraldNeon)
            ),
            RoundedCornerShape(18.dp)
          )
          .border(1.2.dp, Color(0x80FFFFFF), RoundedCornerShape(18.dp))
          .padding(vertical = 4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Payments,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isArabic) "سداد القسط لشهر ${recipient?.payoutMonthAr ?: "الحالي"} ($formattedMonthly)"
          else "Pay Dues for ${recipient?.payoutMonthEn ?: "Current Month"} ($formattedMonthly)",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = Color.White
        )
      }
    } else {
      Button(
        onClick = onAddMemberClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("empty_add_member_button")
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFF047857), LiquidEmeraldNeon)
            ),
            RoundedCornerShape(18.dp)
          )
          .border(1.2.dp, Color(0x80FFFFFF), RoundedCornerShape(18.dp))
          .padding(vertical = 4.dp)
      ) {
        Icon(imageVector = Icons.Default.GroupAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isArabic) "إضافة المشترك الأول إلى الجمعية ➕" else "Add First Member to Circle ➕",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = Color.White
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Secondary Actions Row: Receipts and Advance Month
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Receipts Action
      OutlinedButton(
        onClick = onViewReceiptsClick,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.weight(1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
      ) {
        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = LiquidGoldBright, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isArabic) "السندات (${uiState.receipts.size})" else "Receipts (${uiState.receipts.size})",
          fontFamily = CairoFontFamily,
          fontSize = 12.sp,
          color = TextCrystalWhite
        )
      }

      // Advance Month Action
      OutlinedButton(
        onClick = onAdvanceCycleMonth,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.weight(1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
      ) {
        Icon(imageVector = Icons.Default.FastForward, contentDescription = null, tint = LiquidEmeraldNeon, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isArabic) "الجولة التالية" else "Next Round",
          fontFamily = CairoFontFamily,
          fontSize = 12.sp,
          color = TextCrystalWhite
        )
      }
    }

    Spacer(modifier = Modifier.height(90.dp))
  }
}

@Composable
private fun LiquidStatCard(
  icon: ImageVector,
  title: String,
  value: String,
  subtitle: String,
  accentColor: Color,
  isArabic: Boolean,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .liquidGlassEffect(shape = RoundedCornerShape(18.dp), elevation = 0.dp)
      .padding(12.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .size(30.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(accentColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(16.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = title,
        fontFamily = CairoFontFamily,
        fontSize = 11.sp,
        color = TextSilver,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = value,
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = TextCrystalWhite,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = subtitle,
        fontFamily = CairoFontFamily,
        fontSize = 10.sp,
        color = TextMutedGray,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun DashboardAudioEqualizer(reduceMotion: Boolean) {
  val infiniteTransition = rememberInfiniteTransition(label = "dash_eq")

  val h1 by infiniteTransition.animateFloat(
    initialValue = if (reduceMotion) 12f else 6f,
    targetValue = if (reduceMotion) 12f else 18f,
    animationSpec = infiniteRepeatable(
      animation = tween(360, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "eq1"
  )

  val h2 by infiniteTransition.animateFloat(
    initialValue = if (reduceMotion) 16f else 18f,
    targetValue = if (reduceMotion) 16f else 8f,
    animationSpec = infiniteRepeatable(
      animation = tween(440, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "eq2"
  )

  val h3 by infiniteTransition.animateFloat(
    initialValue = if (reduceMotion) 10f else 5f,
    targetValue = if (reduceMotion) 10f else 16f,
    animationSpec = infiniteRepeatable(
      animation = tween(390, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "eq3"
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(3.dp),
    modifier = Modifier.height(20.dp)
  ) {
    Box(
      modifier = Modifier
        .width(3.5.dp)
        .height(h1.dp)
        .clip(CircleShape)
        .background(LiquidEmeraldNeon)
    )
    Box(
      modifier = Modifier
        .width(3.5.dp)
        .height(h2.dp)
        .clip(CircleShape)
        .background(LiquidGoldBright)
    )
    Box(
      modifier = Modifier
        .width(3.5.dp)
        .height(h3.dp)
        .clip(CircleShape)
        .background(LiquidEmeraldNeon)
    )
  }
}

