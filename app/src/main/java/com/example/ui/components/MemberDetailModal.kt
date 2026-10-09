package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.CircleMember
import com.example.model.MemberPaymentStatus
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
import java.net.URLEncoder

@Composable
fun MemberDetailModal(
  member: CircleMember,
  language: AppLanguage,
  currencySymbol: String,
  onDismiss: () -> Unit,
  onToggleStatus: () -> Unit,
  onDeleteMember: () -> Unit
) {
  val isArabic = language == AppLanguage.ARABIC
  val context = LocalContext.current
  var reminderSent by remember { mutableStateOf(false) }
  var showDeleteConfirm by remember { mutableStateOf(false) }

  val isPaid = member.status == MemberPaymentStatus.PAID
  val name = if (isArabic) member.nameAr else member.nameEn
  val role = if (isArabic) member.roleAr else member.roleEn
  val payoutMonth = if (isArabic) member.payoutMonthAr else member.payoutMonthEn
  val cleanPhone = member.phone.replace(" ", "").replace("-", "")

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(26.dp))
        .background(
          Brush.linearGradient(
            listOf(
              Color(0xFA091510),
              Color(0xF504100B),
              Color(0xFA020604)
            )
          )
        )
        .border(
          1.5.dp,
          Brush.linearGradient(
            listOf(
              LiquidGoldBright,
              LiquidEmeraldNeon,
              Color(0x33FFFFFF)
            )
          ),
          RoundedCornerShape(26.dp)
        ),
      color = Color.Transparent
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Modal Header: Title & Close Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (isPaid) LiquidEmeraldNeon else LiquidGoldBright)
            )
            Text(
              text = if (isArabic) "بطاقة العضو التفصيلية" else "Member Profile Details",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = TextCrystalWhite
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color(0x22FFFFFF))
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextSilver,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Profile Card: Avatar, Name, Role, and Turn Badge
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .liquidGlassEffect(shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              // Glowing Profile Avatar
              Box(
                modifier = Modifier
                  .size(56.dp)
                  .clip(CircleShape)
                  .background(Color(member.avatarColorHex))
                  .border(2.dp, Color(0x66FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = member.avatarInitials,
                  fontFamily = CairoFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 20.sp,
                  color = Color.White
                )
              }

              Column {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = name,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextCrystalWhite
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
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LiquidGoldBright
                      )
                    }
                  }
                }

                Text(
                  text = role,
                  fontFamily = CairoFontFamily,
                  fontSize = 12.sp,
                  color = TextSilver
                )
              }
            }

            // Payout Turn Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33F59E0B))
                .border(1.dp, LiquidGold, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = if (isArabic) "الدور" else "TURN",
                  fontFamily = CairoFontFamily,
                  fontSize = 9.sp,
                  color = LiquidGoldBright
                )
                Text(
                  text = "#${member.turnNumber}",
                  fontFamily = CairoFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp,
                  color = Color.White
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Key Data Rows: Payout Month, Contribution, and Status
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .liquidGlassEffect(shape = RoundedCornerShape(16.dp), elevation = 4.dp)
            .padding(14.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Month of Payout
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.CalendarMonth,
                  contentDescription = null,
                  tint = TextMutedGray,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = if (isArabic) "شهر استحقاق الصرف:" else "Payout Month:",
                  fontFamily = CairoFontFamily,
                  fontSize = 12.sp,
                  color = TextSilver
                )
              }
              Text(
                text = payoutMonth,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = LiquidGoldBright
              )
            }

            // Monthly Contribution Amount
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Paid,
                  contentDescription = null,
                  tint = TextMutedGray,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = if (isArabic) "القسط الشهري:" else "Monthly Dues:",
                  fontFamily = CairoFontFamily,
                  fontSize = 12.sp,
                  color = TextSilver
                )
              }
              Text(
                text = "$currencySymbol${member.monthlyContribution}",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = LiquidEmeraldNeon
              )
            }

            // Status Badge
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Schedule,
                  contentDescription = null,
                  tint = TextMutedGray,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = if (isArabic) "حالة الدفع الحالية:" else "Payment Status:",
                  fontFamily = CairoFontFamily,
                  fontSize = 12.sp,
                  color = TextSilver
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isPaid) GlassStatusPaidBg else GlassStatusPendingBg)
                  .border(
                    1.dp,
                    if (isPaid) GlassStatusPaidBorder else GlassStatusPendingBorder,
                    RoundedCornerShape(12.dp)
                  )
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = if (isPaid) {
                    if (isArabic) "مدفوع وموثق ✓" else "Paid & Verified ✓"
                  } else {
                    if (isArabic) "معلق بانتظار السداد" else "Pending"
                  },
                  fontFamily = CairoFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = if (isPaid) GlassStatusPaidText else GlassStatusPendingText
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Phone & Direct Communication
        if (member.phone.isNotBlank()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0x18FFFFFF))
              .border(1.dp, Color(0x28FFFFFF), RoundedCornerShape(14.dp))
              .padding(horizontal = 14.dp, vertical = 10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Phone,
                  contentDescription = "Phone",
                  tint = LiquidGold,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = member.phone,
                  fontFamily = CairoFontFamily,
                  fontSize = 13.sp,
                  color = TextCrystalWhite
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0x2810B981))
                  .border(1.dp, LiquidEmeraldNeon, RoundedCornerShape(8.dp))
                  .clickable {
                    try {
                      val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone"))
                      context.startActivity(callIntent)
                    } catch (_: Exception) {}
                  }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call",
                    tint = LiquidEmeraldNeon,
                    modifier = Modifier.size(12.dp)
                  )
                  Text(
                    text = if (isArabic) "اتصال" else "Call",
                    fontFamily = CairoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LiquidEmeraldNeon
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions: Toggle Status in Room Database
        Button(
          onClick = onToggleStatus,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isPaid) Color(0xFFB45309) else Color(0xFF047857)
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.2.dp, Color(0x66FFFFFF), RoundedCornerShape(14.dp))
            .testTag("toggle_member_status_button")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = if (isPaid) Icons.Default.Schedule else Icons.Default.CheckCircle,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = if (isPaid) {
                if (isArabic) "تغيير الحالة إلى (معلق)" else "Mark as Pending"
              } else {
                if (isArabic) "تأكيد الدفع (تغيير لمدفوع)" else "Mark as Paid"
              },
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color.White
            )
          }
        }

        // Action: Send WhatsApp Reminder
        if (!isPaid && member.phone.isNotBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedButton(
            onClick = {
              reminderSent = true
              val message = if (isArabic) {
                "السلام عليكم ${member.nameAr}، تذكير لطيف بسداد قسط جمعية حصّالة المستحق لشهر $payoutMonth بمبلغ $currencySymbol${member.monthlyContribution}. موثق بحساب الضمان."
              } else {
                "Hello ${member.nameEn}, friendly reminder to settle your Hassala circle payment for $payoutMonth ($currencySymbol${member.monthlyContribution}). Protected by Escrow."
              }
              try {
                val encodedMsg = URLEncoder.encode(message, "UTF-8")
                val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanPhone?text=$encodedMsg"))
                context.startActivity(waIntent)
              } catch (e: Exception) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                  putExtra(Intent.EXTRA_TEXT, message)
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Send Reminder"))
              }
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .background(Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
              .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp))
              .testTag("send_whatsapp_reminder_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Message,
                contentDescription = null,
                tint = if (reminderSent) LiquidEmeraldNeon else TextSilver,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = if (reminderSent) {
                  if (isArabic) "فتح تطبيق واتساب..." else "Opening WhatsApp..."
                } else {
                  if (isArabic) "إرسال تذكير عبر واتساب (WhatsApp)" else "Send WhatsApp Reminder"
                },
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (reminderSent) LiquidEmeraldNeon else TextCrystalWhite
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action: Remove Member ("وازيل")
        OutlinedButton(
          onClick = { showDeleteConfirm = true },
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = null,
            tint = Color(0xFFF87171),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isArabic) "حذف هذا العضو من الجمعية" else "Remove Member from Circle",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = Color(0xFFF87171)
          )
        }
      }
    }
  }

  // Delete Member Confirmation
  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = {
        Text(
          text = if (isArabic) "تأكيد حذف العضو" else "Confirm Remove Member",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      },
      text = {
        Text(
          text = if (isArabic)
            "هل أنت متأكد من حذف ($name)؟ سيتم حذف العضو فوراً وإعادة حساب مجموع الصندوق."
          else
            "Are you sure you want to remove ($name)? Total pool will be updated.",
          fontFamily = CairoFontFamily,
          fontSize = 13.sp,
          color = TextSilver
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showDeleteConfirm = false
            onDeleteMember()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(text = if (isArabic) "نعم، احذف" else "Yes, Remove", fontFamily = CairoFontFamily, color = Color.White)
        }
      },
      dismissButton = {
        OutlinedButton(
          onClick = { showDeleteConfirm = false },
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
