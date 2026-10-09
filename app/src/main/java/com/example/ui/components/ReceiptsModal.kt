package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.PaymentReceipt
import com.example.model.PaymentType
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.LiquidEmeraldNeon
import com.example.ui.theme.LiquidGold
import com.example.ui.theme.LiquidGoldBright
import com.example.ui.theme.TextCrystalWhite
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextSilver
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ReceiptsModal(
  receipts: List<PaymentReceipt>,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onDeleteReceipt: (String) -> Unit = {},
  onClearAllReceipts: () -> Unit = {},
  onSyncReceipts: () -> Unit = {},
  onSwitchPaymentMethod: (String, String) -> Unit = { _, _ -> }
) {
  val isArabic = language == AppLanguage.ARABIC
  val context = LocalContext.current
  val numberFormat = NumberFormat.getNumberInstance(Locale.US)

  var filterType by remember { mutableStateOf<String?>("ALL") } // "ALL", "كاش", "كليك"
  var receiptToDelete by remember { mutableStateOf<PaymentReceipt?>(null) }
  var showClearAllConfirm by remember { mutableStateOf(false) }

  val currency = receipts.firstOrNull()?.currencySymbol ?: "ر.س"
  val totalReceiptsAmount = receipts.sumOf { it.amount }

  val cashReceipts = receipts.filter { it.paymentType == PaymentType.CASH }
  val cliqReceipts = receipts.filter { it.paymentType == PaymentType.CLIQ }
  val cashTotal = cashReceipts.sumOf { it.amount }
  val cliqTotal = cliqReceipts.sumOf { it.amount }

  val displayedReceipts = when (filterType) {
    "كاش" -> cashReceipts
    "كليك" -> cliqReceipts
    else -> receipts
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .heightIn(max = 680.dp)
        .clip(RoundedCornerShape(26.dp))
        .background(
          Brush.linearGradient(
            listOf(
              Color(0xFF0F261B),
              Color(0xFF08170F),
              Color(0xFF040A07)
            )
          )
        )
        .border(
          1.2.dp,
          Brush.linearGradient(
            listOf(
              Color(0x66FFFFFF),
              Color(0x20FFFFFF),
              Color(0x40F59E0B),
              Color(0x4010B981)
            )
          ),
          RoundedCornerShape(26.dp)
        ),
      color = Color.Transparent
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Modal Header
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
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x29F59E0B))
                .border(1.dp, Color(0x80F59E0B), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                tint = LiquidGoldBright,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = if (isArabic) "سندات السداد (تحكم صاحب الجمعية)" else "Payment Receipts (Owner Control)",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextCrystalWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = if (isArabic) "نوعان فقط: كاش 💵 أو كليك ⚡ معتمدان" else "2 Types: Cash 💵 or CliQ ⚡ Only",
                fontFamily = CairoFontFamily,
                fontSize = 11.sp,
                color = LiquidGoldBright
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .testTag("close_receipts_modal_button")
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0x1AFFFFFF))
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextCrystalWhite,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dual Financial Summary Card (Cash vs CliQ Breakdown)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x20000000))
            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Cash Tally
            Column {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text(text = "💵", fontSize = 12.sp)
                Text(
                  text = if (isArabic) "مجموع الكاش:" else "Cash Total:",
                  fontFamily = CairoFontFamily,
                  fontSize = 10.sp,
                  color = TextSilver
                )
              }
              Text(
                text = "$currency${numberFormat.format(cashTotal)}",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = LiquidEmeraldNeon
              )
              Text(
                text = "${cashReceipts.size} ${if (isArabic) "سند كاش" else "cash receipts"}",
                fontFamily = CairoFontFamily,
                fontSize = 9.sp,
                color = TextMutedGray
              )
            }

            Box(
              modifier = Modifier
                .height(36.dp)
                .width(1.dp)
                .background(Color(0x2AFFFFFF))
            )

            // CliQ Tally
            Column {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text(text = "⚡", fontSize = 12.sp)
                Text(
                  text = if (isArabic) "مجموع كليك:" else "CliQ Total:",
                  fontFamily = CairoFontFamily,
                  fontSize = 10.sp,
                  color = TextSilver
                )
              }
              Text(
                text = "$currency${numberFormat.format(cliqTotal)}",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = LiquidGoldBright
              )
              Text(
                text = "${cliqReceipts.size} ${if (isArabic) "سند كليك" else "cliq receipts"}",
                fontFamily = CairoFontFamily,
                fontSize = 9.sp,
                color = TextMutedGray
              )
            }

            Box(
              modifier = Modifier
                .height(36.dp)
                .width(1.dp)
                .background(Color(0x2AFFFFFF))
            )

            // Total Pool
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = if (isArabic) "الإجمالي الكلي:" else "Total Pool:",
                fontFamily = CairoFontFamily,
                fontSize = 10.sp,
                color = TextSilver
              )
              Text(
                text = "$currency${numberFormat.format(totalReceiptsAmount)}",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = TextCrystalWhite
              )
              Text(
                text = "${receipts.size} ${if (isArabic) "سند موثق" else "verified"}",
                fontFamily = CairoFontFamily,
                fontSize = 9.sp,
                color = TextMutedGray
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips (All / Cash / CliQ)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // All
          val isAll = filterType == "ALL"
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isAll) Color(0x33FFFFFF) else Color(0x10FFFFFF))
              .border(1.dp, if (isAll) Color(0x80FFFFFF) else Color(0x20FFFFFF), RoundedCornerShape(10.dp))
              .clickable { filterType = "ALL" }
              .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (isArabic) "الكل (${receipts.size})" else "All (${receipts.size})",
              fontFamily = CairoFontFamily,
              fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
              fontSize = 11.sp,
              color = if (isAll) TextCrystalWhite else TextSilver
            )
          }

          // Cash Filter
          val isCash = filterType == "كاش"
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isCash) Color(0x3310B981) else Color(0x10FFFFFF))
              .border(1.dp, if (isCash) LiquidEmeraldNeon else Color(0x20FFFFFF), RoundedCornerShape(10.dp))
              .clickable { filterType = "كاش" }
              .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (isArabic) "💵 كاش (${cashReceipts.size})" else "💵 Cash (${cashReceipts.size})",
              fontFamily = CairoFontFamily,
              fontWeight = if (isCash) FontWeight.Bold else FontWeight.Medium,
              fontSize = 11.sp,
              color = if (isCash) LiquidEmeraldNeon else TextSilver
            )
          }

          // CliQ Filter
          val isCliq = filterType == "كليك"
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isCliq) Color(0x33F59E0B) else Color(0x10FFFFFF))
              .border(1.dp, if (isCliq) LiquidGoldBright else Color(0x20FFFFFF), RoundedCornerShape(10.dp))
              .clickable { filterType = "كليك" }
              .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (isArabic) "⚡ كليك (${cliqReceipts.size})" else "⚡ CliQ (${cliqReceipts.size})",
              fontFamily = CairoFontFamily,
              fontWeight = if (isCliq) FontWeight.Bold else FontWeight.Medium,
              fontSize = 11.sp,
              color = if (isCliq) LiquidGoldBright else TextSilver
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Receipts List
        if (displayedReceipts.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
              .padding(20.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                tint = TextMutedGray,
                modifier = Modifier.size(46.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = if (isArabic) "لا توجد سندات سداد مطابقة للفلتر" else "No payment receipts matching filter",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextSilver
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (isArabic)
                  "يتم إصدار السند تلقائياً ومباشرة عند تسجيل أي سداد للأعضاء كاش أو كليك"
                else
                  "Receipts are automatically issued when members pay via Cash or CliQ",
                fontFamily = CairoFontFamily,
                fontSize = 10.sp,
                color = TextMutedGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
          ) {
            items(displayedReceipts, key = { it.receiptId }) { receipt ->
              val isReceiptCash = receipt.paymentType == PaymentType.CASH
              val memberName = if (isArabic) receipt.memberNameAr else receipt.memberNameEn
              val formattedAmt = "${receipt.currencySymbol}${numberFormat.format(receipt.amount)}"
              val date = if (isArabic) receipt.formattedDateAr else receipt.formattedDateEn

              Box(
                modifier = Modifier
                  .testTag("receipt_card_${receipt.receiptId}")
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color(0x1CFFFFFF))
                  .border(
                    1.dp,
                    if (isReceiptCash) Color(0x3310B981) else Color(0x33F59E0B),
                    RoundedCornerShape(16.dp)
                  )
                  .padding(12.dp)
              ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                  // Receipt Top: Serial Code & Payment Method Pill
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    // Official Serial Code
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = LiquidEmeraldNeon,
                        modifier = Modifier.size(13.dp)
                      )
                      Text(
                        text = receipt.receiptId,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = LiquidEmeraldNeon
                      )
                    }

                    // Payment Method Badge (Cash or CliQ)
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isReceiptCash) Color(0x3310B981) else Color(0x33F59E0B))
                        .border(
                          1.dp,
                          if (isReceiptCash) Color(0x8010B981) else Color(0x80F59E0B),
                          RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                      ) {
                        Text(
                          text = if (isReceiptCash) "💵" else "⚡",
                          fontSize = 11.sp
                        )
                        Text(
                          text = if (isReceiptCash) {
                            if (isArabic) "كاش (تسليم نقدي)" else "Cash"
                          } else {
                            if (isArabic) "كليك (CliQ فوري)" else "CliQ"
                          },
                          fontFamily = CairoFontFamily,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.sp,
                          color = if (isReceiptCash) LiquidEmeraldNeon else LiquidGoldBright
                        )
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  // Member Name & Amount
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = memberName,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextCrystalWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = "$date • ${if (isArabic) "الجولة #${receipt.roundNumber}" else "Round #${receipt.roundNumber}"}",
                        fontFamily = CairoFontFamily,
                        fontSize = 10.sp,
                        color = TextSilver
                      )
                    }

                    Text(
                      text = formattedAmt,
                      fontFamily = CairoFontFamily,
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 16.sp,
                      color = if (isReceiptCash) LiquidEmeraldNeon else LiquidGoldBright
                    )
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  // Manager Controller Actions: Switch Method, Share, Delete
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    // Manager Switch Type Button (صاحب الجمعية المتحكم)
                    val nextMethod = if (isReceiptCash) "كليك" else "كاش"
                    val nextLabel = if (isReceiptCash) {
                      if (isArabic) "تحويل لكليك ⚡" else "Switch to CliQ ⚡"
                    } else {
                      if (isArabic) "تحويل لكاش 💵" else "Switch to Cash 💵"
                    }

                    OutlinedButton(
                      onClick = { onSwitchPaymentMethod(receipt.receiptId, nextMethod) },
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x40FFFFFF)),
                      contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.SwapHoriz,
                          contentDescription = null,
                          modifier = Modifier.size(13.dp),
                          tint = if (isReceiptCash) LiquidGoldBright else LiquidEmeraldNeon
                        )
                        Text(
                          text = nextLabel,
                          fontFamily = CairoFontFamily,
                          fontSize = 10.sp,
                          color = TextCrystalWhite,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }

                    // Share Button
                    OutlinedButton(
                      onClick = {
                        val methodText = if (isReceiptCash) {
                          if (isArabic) "كاش (تسليم نقدي باليد لمدير الجمعية)" else "Cash (Direct Handover)"
                        } else {
                          if (isArabic) "كليك (تحويل فوري مباشر CliQ)" else "CliQ (Instant Transfer)"
                        }

                        val shareText = if (isArabic) {
                          """
                          📄 سند سداد رسمي - تطبيق حصّالة
                          ━━━━━━━━━━━━━━━━━━━━━━
                          رقم السند: ${receipt.receiptId}
                          العضو: $memberName
                          المبلغ: $formattedAmt
                          طريقة السداد: $methodText
                          الجولة: #${receipt.roundNumber}
                          التاريخ: $date
                          المعتمد والمتحكم: صاحب ومدير الجمعية 🛡️
                          حالة السند: سداد فعلي معتمد في حساب الصندوق.
                          ━━━━━━━━━━━━━━━━━━━━━━
                          """.trimIndent()
                        } else {
                          """
                          Official Payment Receipt - Hassala
                          ━━━━━━━━━━━━━━━━━━━━━━
                          Receipt ID: ${receipt.receiptId}
                          Member: $memberName
                          Amount: $formattedAmt
                          Payment Method: $methodText
                          Round: #${receipt.roundNumber}
                          Date: $date
                          Authorized by: Circle Owner & Manager 🛡️
                          Status: Verified & Settled.
                          ━━━━━━━━━━━━━━━━━━━━━━
                          """.trimIndent()
                        }
                        val sendIntent = Intent().apply {
                          action = Intent.ACTION_SEND
                          putExtra(Intent.EXTRA_TEXT, shareText)
                          type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Receipt"))
                      },
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x40FFFFFF)),
                      contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.Share,
                          contentDescription = null,
                          modifier = Modifier.size(12.dp),
                          tint = LiquidEmeraldNeon
                        )
                        Text(
                          text = if (isArabic) "مشاركة" else "Share",
                          fontFamily = CairoFontFamily,
                          fontSize = 10.sp,
                          color = TextCrystalWhite,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }

                    // Delete Receipt Button
                    IconButton(
                      onClick = { receiptToDelete = receipt },
                      modifier = Modifier
                        .testTag("delete_receipt_${receipt.receiptId}")
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x20EF4444))
                    ) {
                      Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Receipt",
                        tint = Color(0xFFF87171),
                        modifier = Modifier.size(15.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Actions: Sync missing & Clear all
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Sync Receipts
          OutlinedButton(
            onClick = onSyncReceipts,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4010B981))
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Cached,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = LiquidEmeraldNeon
              )
              Text(
                text = if (isArabic) "مزامنة السندات" else "Sync Receipts",
                fontFamily = CairoFontFamily,
                fontSize = 11.sp,
                color = LiquidEmeraldNeon,
                fontWeight = FontWeight.Bold
              )
            }
          }

          if (receipts.isNotEmpty()) {
            OutlinedButton(
              onClick = { showClearAllConfirm = true },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x40EF4444))
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
                  tint = Color(0xFFF87171)
                )
                Text(
                  text = if (isArabic) "مسح السندات" else "Clear All",
                  fontFamily = CairoFontFamily,
                  fontSize = 11.sp,
                  color = Color(0xFFF87171),
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }

  // Delete Single Receipt Confirmation Dialog
  receiptToDelete?.let { receipt ->
    AlertDialog(
      onDismissRequest = { receiptToDelete = null },
      title = {
        Text(
          text = if (isArabic) "حذف سند السداد" else "Delete Receipt",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      },
      text = {
        val name = if (isArabic) receipt.memberNameAr else receipt.memberNameEn
        Text(
          text = if (isArabic)
            "هل أنت متأكد من حذف سند السداد رقم (${receipt.receiptId}) الخاص بالعضو ($name)؟"
          else
            "Are you sure you want to delete receipt (${receipt.receiptId}) for ($name)?",
          fontFamily = CairoFontFamily,
          fontSize = 13.sp,
          color = TextSilver
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onDeleteReceipt(receipt.receiptId)
            receiptToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(text = if (isArabic) "نعم، حذف" else "Yes, Delete", fontFamily = CairoFontFamily, color = Color.White)
        }
      },
      dismissButton = {
        OutlinedButton(
          onClick = { receiptToDelete = null },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(text = if (isArabic) "إلغاء" else "Cancel", fontFamily = CairoFontFamily, color = TextSilver)
        }
      },
      containerColor = Color(0xFF0F261B),
      shape = RoundedCornerShape(20.dp)
    )
  }

  // Clear All Receipts Confirmation Dialog
  if (showClearAllConfirm) {
    AlertDialog(
      onDismissRequest = { showClearAllConfirm = false },
      title = {
        Text(
          text = if (isArabic) "مسح جميع سندات السداد" else "Clear All Receipts",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      },
      text = {
        Text(
          text = if (isArabic)
            "هل أنت متأكد من مسح جميع سندات السداد؟ لا يمكن التراجع عن هذه العملية."
          else
            "Are you sure you want to clear all payment receipts? This action cannot be undone.",
          fontFamily = CairoFontFamily,
          fontSize = 13.sp,
          color = TextSilver
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onClearAllReceipts()
            showClearAllConfirm = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(text = if (isArabic) "نعم، مسح الكل" else "Yes, Clear All", fontFamily = CairoFontFamily, color = Color.White)
        }
      },
      dismissButton = {
        OutlinedButton(
          onClick = { showClearAllConfirm = false },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(text = if (isArabic) "إلغاء" else "Cancel", fontFamily = CairoFontFamily, color = TextSilver)
        }
      },
      containerColor = Color(0xFF0F261B),
      shape = RoundedCornerShape(20.dp)
    )
  }
}
