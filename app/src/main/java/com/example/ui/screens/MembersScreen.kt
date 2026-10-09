package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.MemberFilter
import com.example.model.MemberPaymentStatus
import com.example.ui.components.liquidGlassEffect
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.GlassStatusPaidBg
import com.example.ui.theme.GlassStatusPaidBorder
import com.example.ui.theme.GlassStatusPaidText
import com.example.ui.theme.GlassStatusPendingBg
import com.example.ui.theme.GlassStatusPendingBorder
import com.example.ui.theme.GlassStatusPendingText
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
fun MembersScreen(
  uiState: HassalaUiState,
  onFilterChanged: (MemberFilter) -> Unit,
  onMemberClick: (CircleMember) -> Unit,
  onToggleMemberStatus: (String) -> Unit,
  onDeleteMember: (String) -> Unit,
  onAddMemberClick: () -> Unit,
  onExportPdfClick: () -> Unit,
  onShareJsonClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isArabic = uiState.language == AppLanguage.ARABIC
  val numberFormat = NumberFormat.getNumberInstance(Locale.US)

  val paidCount = uiState.paidMembersCount
  val pendingCount = uiState.totalMembersCount - paidCount
  var memberToDelete by remember { mutableStateOf<CircleMember?>(null) }

  val filteredMembers = uiState.members.filter { member ->
    when (uiState.memberFilter) {
      MemberFilter.ALL -> true
      MemberFilter.PAID -> member.status == MemberPaymentStatus.PAID
      MemberFilter.PENDING -> member.status == MemberPaymentStatus.PENDING
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    // Header & Summary Bar + Add Member Action
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = if (isArabic) "أعضاء حلقة الادخار" else "Circle Members",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 21.sp,
          color = TextCrystalWhite
        )
        Text(
          text = if (isArabic) "إجمالي المشتركين: ${uiState.members.size} عضو • سدد: $paidCount"
          else "Total members: ${uiState.members.size} • Paid: $paidCount",
          fontFamily = CairoFontFamily,
          fontSize = 11.sp,
          color = TextSilver
        )
      }

      // Radiant Liquid Add Member Button
      Button(
        onClick = onAddMemberClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .testTag("add_member_button")
          .background(
            Brush.horizontalGradient(listOf(Color(0xFF047857), LiquidEmeraldNeon)),
            RoundedCornerShape(14.dp)
          )
          .border(1.2.dp, Color(0x80FFFFFF), RoundedCornerShape(14.dp))
      ) {
        Icon(
          imageVector = Icons.Default.PersonAdd,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isArabic) "إضافة عضو" else "Add Member",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = Color.White
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Collection Metrics Pill (Collected vs Pending)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .liquidGlassEffect(shape = RoundedCornerShape(16.dp), elevation = 4.dp)
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Collected metric
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(LiquidEmeraldNeon)
          )
          Column {
            Text(
              text = if (isArabic) "المحصل هذا الشهر" else "Collected This Month",
              fontFamily = CairoFontFamily,
              fontSize = 10.sp,
              color = TextMutedGray
            )
            Text(
              text = "${uiState.circle.currencySymbol}${numberFormat.format(uiState.collectedAmountThisMonth)} ($paidCount)",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = LiquidEmeraldNeon
            )
          }
        }

        // Pending metric
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(LiquidGold)
          )
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = if (isArabic) "المبلغ المتبقي" else "Pending Balance",
              fontFamily = CairoFontFamily,
              fontSize = 10.sp,
              color = TextMutedGray
            )
            Text(
              text = "${uiState.circle.currencySymbol}${numberFormat.format(pendingCount * uiState.circle.monthlyContribution)} ($pendingCount)",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = LiquidGoldBright
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Filter Buttons (All, Paid, Pending) + Quick Actions (PDF & JSON)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Liquid Segmented Filter Controls
      Row(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0x18FFFFFF))
          .border(1.dp, Color(0x28FFFFFF), RoundedCornerShape(14.dp))
          .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
      ) {
        LiquidSegmentedButton(
          text = if (isArabic) "الكل (${uiState.members.size})" else "All (${uiState.members.size})",
          selected = uiState.memberFilter == MemberFilter.ALL,
          isArabic = isArabic,
          tag = "filter_all_button",
          modifier = Modifier.weight(1f),
          onClick = { onFilterChanged(MemberFilter.ALL) }
        )

        LiquidSegmentedButton(
          text = if (isArabic) "مسدد ($paidCount)" else "Paid ($paidCount)",
          selected = uiState.memberFilter == MemberFilter.PAID,
          isArabic = isArabic,
          tag = "filter_paid_button",
          modifier = Modifier.weight(1f),
          onClick = { onFilterChanged(MemberFilter.PAID) }
        )

        LiquidSegmentedButton(
          text = if (isArabic) "معلق ($pendingCount)" else "Pending ($pendingCount)",
          selected = uiState.memberFilter == MemberFilter.PENDING,
          isArabic = isArabic,
          tag = "filter_pending_button",
          modifier = Modifier.weight(1f),
          onClick = { onFilterChanged(MemberFilter.PENDING) }
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      // PDF Report Quick Button
      IconButton(
        onClick = onExportPdfClick,
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0x26F43F5E))
          .border(1.dp, Color(0x66F43F5E), RoundedCornerShape(12.dp))
      ) {
        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "PDF Report", tint = Color(0xFFF43F5E), modifier = Modifier.size(18.dp))
      }

      Spacer(modifier = Modifier.width(4.dp))

      // JSON Share Quick Button
      IconButton(
        onClick = onShareJsonClick,
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0x2610B981))
          .border(1.dp, Color(0x6610B981), RoundedCornerShape(12.dp))
      ) {
        Icon(imageVector = Icons.Default.Share, contentDescription = "Share JSON", tint = LiquidEmeraldNeon, modifier = Modifier.size(18.dp))
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // List of Members in Liquid Glass Cards (or Empty State)
    if (filteredMembers.isEmpty()) {
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
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            tint = LiquidGoldBright,
            modifier = Modifier.size(42.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = if (isArabic) "لا يوجد أعضاء في هذا القسم حالياً" else "No members found in this section",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextCrystalWhite
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (isArabic)
              "يمكنك إضافة أعضاء جدد إلى الجمعية وتحديد أدوارهم وأرقام هواتفهم."
            else
              "Add members to your circle with their turn order and phone numbers.",
            fontFamily = CairoFontFamily,
            fontSize = 12.sp,
            color = TextSilver,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onAddMemberClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .background(
                Brush.horizontalGradient(listOf(Color(0xFF047857), LiquidEmeraldNeon)),
                RoundedCornerShape(12.dp)
              )
          ) {
            Text(
              text = if (isArabic) "إضافة عضو جديد ➕" else "Add New Member ➕",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color.White
            )
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("members_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(
          items = filteredMembers,
          key = { it.id }
        ) { member ->
          LiquidMemberCardItem(
            member = member,
            currencySymbol = uiState.circle.currencySymbol,
            isArabic = isArabic,
            isCurrentRoundRecipient = member.turnNumber == uiState.circle.currentCycleMonth,
            onClick = { onMemberClick(member) },
            onToggleStatus = { onToggleMemberStatus(member.id) },
            onDeleteClick = { memberToDelete = member }
          )
        }

        item {
          Spacer(modifier = Modifier.height(90.dp))
        }
      }
    }
  }

  // Delete Member Confirmation Dialog
  memberToDelete?.let { member ->
    val name = if (isArabic) member.nameAr else member.nameEn
    AlertDialog(
      onDismissRequest = { memberToDelete = null },
      title = {
        Text(
          text = if (isArabic) "حذف العضو من الجمعية" else "Remove Member",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      },
      text = {
        Text(
          text = if (isArabic)
            "هل أنت متأكد من حذف ($name) من قائمة الجمعية؟ سيتم إعادة حساب إجمالي الصندوق تلقائياً."
          else
            "Are you sure you want to remove ($name)? The circle pool will be automatically recalculated.",
          fontFamily = CairoFontFamily,
          fontSize = 13.sp,
          color = TextSilver
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onDeleteMember(member.id)
            memberToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(text = if (isArabic) "نعم، حذف" else "Yes, Remove", fontFamily = CairoFontFamily, color = Color.White)
        }
      },
      dismissButton = {
        OutlinedButton(
          onClick = { memberToDelete = null },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(text = if (isArabic) "إلغاء" else "Cancel", fontFamily = CairoFontFamily, color = TextSilver)
        }
      },
      containerColor = Color(0xFF0A1C14),
      shape = RoundedCornerShape(20.dp)
    )
  }
}

@Composable
private fun LiquidSegmentedButton(
  text: String,
  selected: Boolean,
  isArabic: Boolean,
  tag: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val bgColor by animateColorAsState(
    targetValue = if (selected) Color(0x38FFFFFF) else Color.Transparent,
    label = "liquid_seg_bg"
  )

  Box(
    modifier = modifier
      .testTag(tag)
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .border(
        if (selected) 1.dp else 0.dp,
        if (selected) Color(0x66FFFFFF) else Color.Transparent,
        RoundedCornerShape(12.dp)
      )
      .clickable { onClick() }
      .padding(vertical = 7.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      fontFamily = CairoFontFamily,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
      fontSize = 11.sp,
      color = if (selected) TextCrystalWhite else TextSilver
    )
  }
}

@Composable
private fun LiquidMemberCardItem(
  member: CircleMember,
  currencySymbol: String,
  isArabic: Boolean,
  isCurrentRoundRecipient: Boolean,
  onClick: () -> Unit,
  onToggleStatus: () -> Unit,
  onDeleteClick: () -> Unit
) {
  val isPaid = member.status == MemberPaymentStatus.PAID
  val name = if (isArabic) member.nameAr else member.nameEn
  val payoutMonth = if (isArabic) member.payoutMonthAr else member.payoutMonthEn

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("member_card_${member.id}")
      .liquidGlassEffect(
        shape = RoundedCornerShape(18.dp),
        elevation = 6.dp,
        borderBrush = if (member.isCurrentUser) {
          Brush.linearGradient(listOf(Color(0x80F59E0B), Color(0x4010B981)))
        } else {
          Brush.linearGradient(listOf(Color(0x38FFFFFF), Color(0x10FFFFFF)))
        }
      )
      .clickable { onClick() }
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Profile Avatar with initials and Turn Badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(contentAlignment = Alignment.BottomEnd) {
          // Circular Avatar with Holographic Edge
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(Color(member.avatarColorHex))
              .border(1.2.dp, Color(0x40FFFFFF), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = member.avatarInitials,
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color.White
            )
          }

          // Small Turn Number Badge
          Box(
            modifier = Modifier
              .size(18.dp)
              .clip(CircleShape)
              .background(if (isCurrentRoundRecipient) LiquidGold else Color(0xFF0F1E14))
              .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${member.turnNumber}",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              color = Color.White
            )
          }
        }

        // Middle: Member Name and Details (Prevent overlap with actions)
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

            if (member.isCurrentUser) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0x33F59E0B))
                  .border(1.dp, Color(0x80F59E0B), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (isArabic) "أنت" else "YOU",
                  fontFamily = CairoFontFamily,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = LiquidGoldBright
                )
              }
            }

            if (isCurrentRoundRecipient) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0x33F59E0B))
                  .padding(horizontal = 5.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (isArabic) "المستحق 👑" else "Recipient 👑",
                  fontFamily = CairoFontFamily,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = LiquidGoldBright
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = TextMutedGray,
              modifier = Modifier.size(11.dp)
            )
            Text(
              text = if (isArabic) "استحقاق: $payoutMonth (دور #${member.turnNumber}) • $currencySymbol${member.monthlyContribution}"
              else "Payout: $payoutMonth (Turn #${member.turnNumber}) • $currencySymbol${member.monthlyContribution}",
              fontFamily = CairoFontFamily,
              fontSize = 10.sp,
              color = TextSilver,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      // Right: Actions (Status Pill Badge + Delete Button)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Status Pill Badge (Paid / Pending)
        Box(
          modifier = Modifier
            .testTag("status_badge_${member.id}")
            .clip(RoundedCornerShape(18.dp))
            .background(if (isPaid) GlassStatusPaidBg else GlassStatusPendingBg)
            .border(
              1.dp,
              if (isPaid) GlassStatusPaidBorder else GlassStatusPendingBorder,
              RoundedCornerShape(18.dp)
            )
            .clickable { onToggleStatus() }
            .padding(horizontal = 9.dp, vertical = 5.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.Schedule,
              contentDescription = null,
              tint = if (isPaid) GlassStatusPaidText else GlassStatusPendingText,
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = if (isPaid) {
                if (isArabic) "تم الدفع" else "Paid"
              } else {
                if (isArabic) "معلق" else "Pending"
              },
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = if (isPaid) GlassStatusPaidText else GlassStatusPendingText
            )
          }
        }

        // Delete Member Icon Button ("وازيل")
        IconButton(
          onClick = onDeleteClick,
          modifier = Modifier
            .testTag("delete_member_${member.id}")
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0x20EF4444))
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Remove member",
            tint = Color(0xFFF87171),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
