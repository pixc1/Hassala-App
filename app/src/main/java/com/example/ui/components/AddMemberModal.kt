package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.LiquidEmeraldNeon
import com.example.ui.theme.LiquidGold
import com.example.ui.theme.LiquidGoldBright
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.TajawalFontFamily
import com.example.ui.theme.TextCrystalWhite
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextSilver

@Composable
fun AddMemberModal(
  language: AppLanguage,
  defaultContribution: Long,
  currencySymbol: String,
  suggestedTurn: Int,
  onDismiss: () -> Unit,
  onConfirmAdd: (name: String, phone: String, contribution: Long, turn: Int) -> Unit
) {
  val isArabic = language == AppLanguage.ARABIC
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var contributionStr by remember { mutableStateOf("$defaultContribution") }
  var turnStr by remember { mutableStateOf("$suggestedTurn") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

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
            listOf(Color(0xFF0E2217), Color(0xFF07120B), Color(0xFF030705))
          )
        )
        .border(
          1.2.dp,
          Brush.linearGradient(
            listOf(Color(0x66FFFFFF), Color(0x20FFFFFF), Color(0x40F59E0B))
          ),
          RoundedCornerShape(26.dp)
        ),
      color = Color.Transparent
    ) {
      Column(
        modifier = Modifier
          .padding(24.dp)
          .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
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
                .background(Color(0x33F59E0B))
                .border(1.dp, Color(0x80F59E0B), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                tint = LiquidGoldBright,
                modifier = Modifier.size(20.dp)
              )
            }
            Text(
              text = if (isArabic) "إضافة عضو جديد للجمعية" else "Add New Member",
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = TextCrystalWhite
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_add_member_modal_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextMutedGray
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        val textFieldColors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextCrystalWhite,
          unfocusedTextColor = TextCrystalWhite,
          focusedBorderColor = LiquidEmeraldNeon,
          unfocusedBorderColor = Color(0x33FFFFFF),
          focusedLabelColor = LiquidEmeraldNeon,
          unfocusedLabelColor = TextSilver,
          cursorColor = LiquidEmeraldNeon
        )

        // Full Name Field
        OutlinedTextField(
          value = name,
          onValueChange = {
            name = it
            errorMessage = null
          },
          label = {
            Text(
              text = if (isArabic) "اسم العضو بالكامل" else "Member Full Name",
              fontFamily = CairoFontFamily
            )
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_member_name"),
          shape = RoundedCornerShape(14.dp),
          colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Phone Number Field
        OutlinedTextField(
          value = phone,
          onValueChange = {
            phone = it
            errorMessage = null
          },
          label = {
            Text(
              text = if (isArabic) "رقم الهاتف / واتساب (مثال: +966...)" else "Phone / WhatsApp (+971...)",
              fontFamily = CairoFontFamily
            )
          },
          placeholder = { Text("+966 50 000 0000", color = TextMutedGray) },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_member_phone"),
          shape = RoundedCornerShape(14.dp),
          colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Contribution & Turn in one row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = contributionStr,
            onValueChange = { contributionStr = it },
            label = {
              Text(
                text = if (isArabic) "القسط ($currencySymbol)" else "Dues ($currencySymbol)",
                fontFamily = CairoFontFamily
              )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .weight(1f)
              .testTag("input_member_contribution"),
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors
          )

          OutlinedTextField(
            value = turnStr,
            onValueChange = { turnStr = it },
            label = {
              Text(
                text = if (isArabic) "رقم الدور" else "Turn #",
                fontFamily = CairoFontFamily
              )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .weight(1f)
              .testTag("input_member_turn"),
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors
          )
        }

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = errorMessage ?: "",
            color = Color(0xFFF87171),
            fontSize = 12.sp,
            fontFamily = CairoFontFamily
          )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Button(
          onClick = {
            if (name.isBlank()) {
              errorMessage = if (isArabic) "يرجى إدخال اسم العضو" else "Please enter member name"
              return@Button
            }
            if (phone.isBlank()) {
              errorMessage = if (isArabic) "يرجى إدخال رقم الهاتف" else "Please enter phone number"
              return@Button
            }
            val contrib = contributionStr.toLongOrNull() ?: defaultContribution
            val turn = turnStr.toIntOrNull() ?: suggestedTurn
            onConfirmAdd(name.trim(), phone.trim(), contrib, turn)
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
              Brush.horizontalGradient(listOf(Color(0xFF047857), LiquidEmeraldNeon)),
              RoundedCornerShape(16.dp)
            )
            .border(1.2.dp, Color(0x80FFFFFF), RoundedCornerShape(16.dp))
            .testTag("confirm_add_member_button")
        ) {
          Text(
            text = if (isArabic) "حفظ العضو في قاعدة البيانات" else "Save Member to Database",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
          )
        }
      }
    }
  }
}
