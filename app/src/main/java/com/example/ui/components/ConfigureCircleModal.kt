package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

@Composable
fun ConfigureCircleModal(
  circle: MoneyCircle,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onSave: (titleAr: String, titleEn: String, monthlyContribution: Long, totalMonths: Int, currencySymbol: String, currentMonth: Int) -> Unit,
  onClearAllMembers: () -> Unit = {}
) {
  val isArabic = language == AppLanguage.ARABIC

  var titleAr by remember { mutableStateOf(circle.titleAr) }
  var titleEn by remember { mutableStateOf(circle.titleEn) }
  var monthlyAmountText by remember { mutableStateOf(circle.monthlyContribution.toString()) }
  var totalMonthsText by remember { mutableStateOf(circle.totalMonths.toString()) }
  var currentMonthText by remember { mutableStateOf(circle.currentCycleMonth.toString()) }
  var selectedCurrency by remember { mutableStateOf(circle.currencySymbol) }
  var customCurrency by remember { mutableStateOf("") }

  val currencies = listOf("ر.س", "د.إ", "$", "د.ك", "ج.م", "ر.ق", "د.ب", "ر.ع", "€")
  val quickDurations = listOf(3, 5, 6, 10, 12)
  val quickAmounts = listOf(500L, 1000L, 2000L, 3000L, 5000L)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
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
          Brush.linearGradient(listOf(LiquidGoldBright, LiquidEmeraldNeon, Color(0x33FFFFFF))),
          RoundedCornerShape(26.dp)
        ),
      color = Color.Transparent
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Modal Header
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
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33F59E0B))
                .border(1.dp, LiquidGold, RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = LiquidGoldBright,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Text(
                text = if (isArabic) "تخصيص وإعدادات الجمعية" else "Circle Settings & Setup",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextCrystalWhite
              )
              Text(
                text = if (isArabic) "تحكم بالاسم، القسط، المدة، والعملة" else "Manage name, dues, duration & currency",
                fontFamily = CairoFontFamily,
                fontSize = 11.sp,
                color = TextSilver
              )
            }
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

        // 1. Circle Title (Arabic)
        Text(
          text = if (isArabic) "اسم الجمعية (بالعربية) *" else "Circle Name (Arabic) *",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          color = TextSilver
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = titleAr,
          onValueChange = { titleAr = it },
          placeholder = {
            Text("مثال: جمعية الأهل والأصدقاء", fontFamily = CairoFontFamily, color = TextMutedGray, fontSize = 13.sp)
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("circle_name_ar_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = LiquidEmeraldNeon,
            unfocusedBorderColor = Color(0x33FFFFFF),
            focusedContainerColor = Color(0x18FFFFFF),
            unfocusedContainerColor = Color(0x0CFFFFFF)
          ),
          shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Circle Title (English)
        Text(
          text = if (isArabic) "اسم الجمعية (بالإنجليزية)" else "Circle Name (English)",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          color = TextSilver
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = titleEn,
          onValueChange = { titleEn = it },
          placeholder = {
            Text("e.g. Friends & Family Pool", fontFamily = CairoFontFamily, color = TextMutedGray, fontSize = 13.sp)
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("circle_name_en_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = LiquidEmeraldNeon,
            unfocusedBorderColor = Color(0x33FFFFFF),
            focusedContainerColor = Color(0x18FFFFFF),
            unfocusedContainerColor = Color(0x0CFFFFFF)
          ),
          shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Currency Selector
        Text(
          text = if (isArabic) "العملة المحددة للجمعية *" else "Circle Currency *",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          color = TextSilver
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          currencies.forEach { cur ->
            val isSelected = selectedCurrency == cur
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) LiquidGold.copy(alpha = 0.25f) else Color(0x15FFFFFF))
                .border(
                  1.dp,
                  if (isSelected) LiquidGoldBright else Color(0x33FFFFFF),
                  RoundedCornerShape(12.dp)
                )
                .clickable { selectedCurrency = cur }
                .padding(horizontal = 14.dp, vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = cur,
                fontFamily = CairoFontFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                color = if (isSelected) LiquidGoldBright else TextCrystalWhite
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Monthly Contribution Amount
        Text(
          text = if (isArabic) "مبلغ القسط الشهري لكل عضو ($selectedCurrency) *" else "Monthly Contribution ($selectedCurrency) *",
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          color = TextSilver
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = monthlyAmountText,
          onValueChange = { monthlyAmountText = it.filter { char -> char.isDigit() } },
          placeholder = { Text("1000", fontFamily = CairoFontFamily, color = TextMutedGray) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("circle_monthly_amount_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = LiquidEmeraldNeon,
            unfocusedBorderColor = Color(0x33FFFFFF),
            focusedContainerColor = Color(0x18FFFFFF),
            unfocusedContainerColor = Color(0x0CFFFFFF)
          ),
          shape = RoundedCornerShape(14.dp)
        )

        // Quick amount chips
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          quickAmounts.forEach { amount ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x12FFFFFF))
                .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(10.dp))
                .clickable { monthlyAmountText = amount.toString() }
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = "$selectedCurrency$amount",
                fontFamily = CairoFontFamily,
                fontSize = 11.sp,
                color = LiquidEmeraldNeon
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Total Duration / Number of Months
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (isArabic) "مدة الجمعية (أشهر) *" else "Duration (Months) *",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp,
              color = TextSilver
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = totalMonthsText,
              onValueChange = { totalMonthsText = it.filter { char -> char.isDigit() } },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = LiquidEmeraldNeon,
                unfocusedBorderColor = Color(0x33FFFFFF),
                focusedContainerColor = Color(0x18FFFFFF),
                unfocusedContainerColor = Color(0x0CFFFFFF)
              ),
              shape = RoundedCornerShape(14.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (isArabic) "الجولة الحالية" else "Current Round",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp,
              color = TextSilver
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = currentMonthText,
              onValueChange = { currentMonthText = it.filter { char -> char.isDigit() } },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = LiquidEmeraldNeon,
                unfocusedBorderColor = Color(0x33FFFFFF),
                focusedContainerColor = Color(0x18FFFFFF),
                unfocusedContainerColor = Color(0x0CFFFFFF)
              ),
              shape = RoundedCornerShape(14.dp)
            )
          }
        }

        // Quick duration chips
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          quickDurations.forEach { duration ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x12FFFFFF))
                .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(10.dp))
                .clickable { totalMonthsText = duration.toString() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (isArabic) "$duration أشهر" else "$duration mo",
                fontFamily = CairoFontFamily,
                fontSize = 11.sp,
                color = TextSilver
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save Button
        Button(
          onClick = {
            val amount = monthlyAmountText.toLongOrNull() ?: 1000L
            val months = (totalMonthsText.toIntOrNull() ?: 10).coerceAtLeast(1)
            val currentM = (currentMonthText.toIntOrNull() ?: 1).coerceIn(1, months)
            onSave(
              titleAr.ifBlank { "جمعيتي الخاصة" },
              titleEn.ifBlank { "My Money Circle" },
              amount,
              months,
              selectedCurrency,
              currentM
            )
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("save_circle_settings_button")
            .background(
              Brush.horizontalGradient(listOf(Color(0xFF047857), LiquidEmeraldNeon)),
              RoundedCornerShape(16.dp)
            )
            .border(1.2.dp, Color(0x80FFFFFF), RoundedCornerShape(16.dp))
            .padding(vertical = 4.dp)
        ) {
          Text(
            text = if (isArabic) "حفظ إعدادات الجمعية والعملة" else "Save Settings & Currency",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.White
          )
        }
      }
    }
  }
}
