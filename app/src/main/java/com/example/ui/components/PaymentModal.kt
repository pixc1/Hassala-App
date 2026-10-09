package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.MoneyCircle
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.LiquidEmeraldNeon
import com.example.ui.theme.LiquidGold
import com.example.ui.theme.LiquidGoldBright
import com.example.ui.theme.TextCrystalWhite
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextSilver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaymentModal(
  circle: MoneyCircle,
  language: AppLanguage,
  managerCliqAlias: String = "HASSALA-0798765432",
  onDismiss: () -> Unit,
  onConfirmPayment: (String) -> Unit
) {
  val isArabic = language == AppLanguage.ARABIC
  var selectedMethod by remember { mutableStateOf("كاش") } // "كاش" or "كليك"
  var isProcessing by remember { mutableStateOf(false) }
  var isCompleted by remember { mutableStateOf(false) }
  var hasCopiedCliq by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()
  val clipboardManager = LocalClipboardManager.current

  val numberFormat = NumberFormat.getNumberInstance(Locale.US)
  val formattedAmount = "${circle.currencySymbol}${numberFormat.format(circle.monthlyContribution)}"

  Dialog(
    onDismissRequest = { if (!isProcessing) onDismiss() },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(26.dp))
        .background(
          Brush.linearGradient(
            listOf(Color(0xFF0C1F16), Color(0xFF06140D), Color(0xFF030A06))
          )
        )
        .border(
          1.2.dp,
          Brush.linearGradient(
            listOf(Color(0x5510B981), Color(0x22FFFFFF), Color(0x44F59E0B))
          ),
          RoundedCornerShape(26.dp)
        ),
      color = Color.Transparent
    ) {
      Column(
        modifier = Modifier.padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0x3310B981))
                .border(1.dp, Color(0x6634D399), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                tint = LiquidEmeraldNeon,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = if (isArabic) "سداد القسط الشهري" else "Pay Monthly Contribution",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextCrystalWhite
              )
              Text(
                text = if (isArabic) "سند سداد رسمي موثق لمدير الجمعية" else "Official Verified Receipt via Owner",
                fontFamily = CairoFontFamily,
                fontSize = 10.sp,
                color = TextSilver
              )
            }
          }

          IconButton(
            onClick = { if (!isProcessing) onDismiss() },
            modifier = Modifier
              .testTag("close_payment_modal_button")
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

        // Amount Display Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x2210B981))
            .border(1.dp, Color(0x3310B981), RoundedCornerShape(18.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = if (isArabic) "المبلغ المطلوب سداده" else "Required Contribution Amount",
              fontFamily = CairoFontFamily,
              fontSize = 11.sp,
              color = TextSilver
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = formattedAmount,
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 32.sp,
              color = LiquidEmeraldNeon
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = LiquidGoldBright,
                modifier = Modifier.size(13.dp)
              )
              Text(
                text = if (isArabic) "تحكم واعتماد حصري بواسطة صاحب الجمعية" else "Controlled & Certified by Circle Owner",
                fontFamily = CairoFontFamily,
                fontSize = 10.sp,
                color = LiquidGoldBright,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Payment Method Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isArabic) "طريقة الدفع (كاش أو كليك فقط):" else "Payment Method (Cash or CliQ only):",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = TextCrystalWhite
          )
          Text(
            text = if (isArabic) "نوعان حصرياً" else "2 Types Only",
            fontFamily = CairoFontFamily,
            fontSize = 10.sp,
            color = LiquidGoldBright
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // OPTION 1: CASH (كاش)
        val isCashSelected = selectedMethod == "كاش"
        Box(
          modifier = Modifier
            .testTag("payment_method_cash")
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isCashSelected) Color(0x3310B981) else Color(0x12FFFFFF))
            .border(
              1.2.dp,
              if (isCashSelected) LiquidEmeraldNeon else Color(0x22FFFFFF),
              RoundedCornerShape(14.dp)
            )
            .clickable { selectedMethod = "كاش" }
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isCashSelected) LiquidEmeraldNeon else Color(0x22FFFFFF)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "💵",
                  fontSize = 18.sp
                )
              }

              Column {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = if (isArabic) "كاش (تسليم نقدي مباشر)" else "Cash (Direct Handover)",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextCrystalWhite
                  )
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(Color(0x3310B981))
                      .padding(horizontal = 5.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = if (isArabic) "نقدي" else "CASH",
                      fontFamily = CairoFontFamily,
                      fontSize = 8.sp,
                      color = LiquidEmeraldNeon,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
                Text(
                  text = if (isArabic)
                    "تسليم القسط نقداً باليد لصاحب ومدير الجمعية"
                  else
                    "Hand cash directly to the circle manager",
                  fontFamily = CairoFontFamily,
                  fontSize = 10.sp,
                  color = TextSilver
                )
              }
            }

            // Radio ring
            Box(
              modifier = Modifier
                .size(20.dp)
                .border(
                  2.dp,
                  if (isCashSelected) LiquidEmeraldNeon else Color(0x40FFFFFF),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              if (isCashSelected) {
                Box(
                  modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(LiquidEmeraldNeon)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // OPTION 2: CLIQ (كليك)
        val isCliqSelected = selectedMethod == "كليك"
        Box(
          modifier = Modifier
            .testTag("payment_method_cliq")
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isCliqSelected) Color(0x33F59E0B) else Color(0x12FFFFFF))
            .border(
              1.2.dp,
              if (isCliqSelected) LiquidGoldBright else Color(0x22FFFFFF),
              RoundedCornerShape(14.dp)
            )
            .clickable { selectedMethod = "كليك" }
            .padding(12.dp)
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isCliqSelected) LiquidGoldBright else Color(0x22FFFFFF)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "⚡",
                    fontSize = 18.sp
                  )
                }

                Column {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = if (isArabic) "كليك (CliQ تحويل فوري)" else "CliQ (Instant Transfer)",
                      fontFamily = CairoFontFamily,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = TextCrystalWhite
                    )
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x33F59E0B))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                      Text(
                        text = if (isArabic) "فوري" else "INSTANT",
                        fontFamily = CairoFontFamily,
                        fontSize = 8.sp,
                        color = LiquidGoldBright,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                  Text(
                    text = if (isArabic)
                      "تحويل فوري مباشر لحساب كليك الخاص بمدير الجمعية"
                    else
                      "Instant transfer to manager's CliQ alias",
                    fontFamily = CairoFontFamily,
                    fontSize = 10.sp,
                    color = TextSilver
                  )
                }
              }

              // Radio ring
              Box(
                modifier = Modifier
                  .size(20.dp)
                .border(
                  2.dp,
                  if (isCliqSelected) LiquidGoldBright else Color(0x40FFFFFF),
                  CircleShape
                ),
                contentAlignment = Alignment.Center
              ) {
                if (isCliqSelected) {
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .clip(CircleShape)
                      .background(LiquidGoldBright)
                  )
                }
              }
            }

            // CliQ Alias details box if CliQ selected
            if (isCliqSelected) {
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0x22000000))
                  .border(1.dp, Color(0x33F59E0B), RoundedCornerShape(10.dp))
                  .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(
                    text = if (isArabic) "معرّف كليك لمدير الجمعية:" else "Manager CliQ Alias:",
                    fontFamily = CairoFontFamily,
                    fontSize = 9.sp,
                    color = TextMutedGray
                  )
                  Text(
                    text = managerCliqAlias,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = LiquidGoldBright
                  )
                }

                // Copy button
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33F59E0B))
                    .clickable {
                      clipboardManager.setText(AnnotatedString(managerCliqAlias))
                      hasCopiedCliq = true
                      coroutineScope.launch {
                        delay(2000)
                        hasCopiedCliq = false
                      }
                    }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(
                      imageVector = if (hasCopiedCliq) Icons.Default.Check else Icons.Default.ContentCopy,
                      contentDescription = "Copy CliQ",
                      tint = LiquidGoldBright,
                      modifier = Modifier.size(12.dp)
                    )
                    Text(
                      text = if (hasCopiedCliq) {
                        if (isArabic) "تم النسخ!" else "Copied!"
                      } else {
                        if (isArabic) "نسخ المعرّف" else "Copy"
                      },
                      fontFamily = CairoFontFamily,
                      fontSize = 10.sp,
                      color = LiquidGoldBright,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Confirm & Pay CTA
        Box(
          modifier = Modifier
            .testTag("confirm_payment_cta_button")
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.horizontalGradient(
                colors = if (selectedMethod == "كاش")
                  listOf(Color(0xFF047857), LiquidEmeraldNeon)
                else
                  listOf(Color(0xFFB45309), LiquidGoldBright)
              )
            )
            .border(1.dp, Color(0x80FFFFFF), RoundedCornerShape(16.dp))
            .clickable(enabled = !isProcessing && !isCompleted) {
              isProcessing = true
              coroutineScope.launch {
                delay(900)
                isProcessing = false
                isCompleted = true
                delay(400)
                onConfirmPayment(selectedMethod)
              }
            },
          contentAlignment = Alignment.Center
        ) {
          if (isProcessing) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp
              )
              Text(
                text = if (isArabic) "جاري توثيق السند..." else "Recording receipt...",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
              )
            }
          } else if (isCompleted) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = if (isArabic) "تم السداد وإصدار السند!" else "Payment Recorded!",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
              )
            }
          } else {
            Text(
              text = if (isArabic)
                "تأكيد السداد عبر ($selectedMethod) • $formattedAmount"
              else
                "Confirm Payment via ($selectedMethod) • $formattedAmount",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color.White
            )
          }
        }
      }
    }
  }
}
