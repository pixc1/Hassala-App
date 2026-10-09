package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.People
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.CircleMember
import com.example.model.MoneyCircle
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.LiquidEmeraldNeon
import com.example.ui.theme.LiquidGold
import com.example.ui.theme.LiquidGoldBright
import com.example.ui.theme.TextCrystalWhite
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextSilver
import com.example.util.JsonCircleManager

@Composable
fun ImportCircleModal(
  language: AppLanguage,
  onDismiss: () -> Unit,
  onImport: (MoneyCircle, List<CircleMember>) -> Unit
) {
  val isArabic = language == AppLanguage.ARABIC
  val context = LocalContext.current

  var jsonInputText by remember { mutableStateOf("") }
  var parseError by remember { mutableStateOf<String?>(null) }
  var parsedResult by remember { mutableStateOf<Pair<MoneyCircle, List<CircleMember>>?>(null) }

  // File Picker Launcher for JSON files
  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        val stream = context.contentResolver.openInputStream(uri)
        val content = stream?.bufferedReader()?.use { it.readText() } ?: ""
        jsonInputText = content
        val res = JsonCircleManager.parseCircleFromJson(content)
        if (res != null) {
          parsedResult = res
          parseError = null
        } else {
          parsedResult = null
          parseError = if (isArabic) "ملف غير صالح أو لا يحتوي على بنية الجمعية الصحيحة" else "Invalid JSON format"
        }
      } catch (e: Exception) {
        parseError = if (isArabic) "فشل قراءة الملف: ${e.localizedMessage}" else "Failed to read file"
      }
    }
  }

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
                .background(Color(0x3310B981))
                .border(1.dp, LiquidEmeraldNeon, RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = null,
                tint = LiquidEmeraldNeon,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Text(
                text = if (isArabic) "استيراد جمعية (ملف JSON)" else "Import Circle (JSON)",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextCrystalWhite
              )
              Text(
                text = if (isArabic) "ارفع ملف جمعية تمت مشاركته معك" else "Upload or paste shared circle JSON",
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

        Spacer(modifier = Modifier.height(18.dp))

        // File upload button
        Button(
          onClick = {
            filePickerLauncher.launch("*/*")
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pick_json_file_button")
            .background(
              Brush.horizontalGradient(listOf(Color(0xFF047857), Color(0xFF065F46))),
              RoundedCornerShape(14.dp)
            )
            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(14.dp))
            .padding(vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.FileUpload,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isArabic) "اختيار ملف JSON من الجهاز 📁" else "Choose JSON File from Device 📁",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(1.dp)
              .background(Color(0x22FFFFFF))
          )
          Text(
            text = if (isArabic) " أو الصق النص هنا " else " OR PASTE RAW JSON ",
            fontFamily = CairoFontFamily,
            fontSize = 10.sp,
            color = TextMutedGray,
            modifier = Modifier.padding(horizontal = 8.dp)
          )
          Box(
            modifier = Modifier
              .weight(1f)
              .height(1.dp)
              .background(Color(0x22FFFFFF))
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Raw JSON text input
        OutlinedTextField(
          value = jsonInputText,
          onValueChange = { text ->
            jsonInputText = text
            if (text.isNotBlank()) {
              val res = JsonCircleManager.parseCircleFromJson(text)
              if (res != null) {
                parsedResult = res
                parseError = null
              } else {
                parsedResult = null
                parseError = if (isArabic) "الصيغة غير صحيحة" else "Invalid format"
              }
            } else {
              parsedResult = null
              parseError = null
            }
          },
          placeholder = {
            Text(
              text = if (isArabic) "الصق محتوى ملف JSON هنا..." else "Paste JSON code here...",
              fontFamily = CairoFontFamily,
              color = TextMutedGray,
              fontSize = 12.sp
            )
          },
          minLines = 4,
          maxLines = 6,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("paste_json_input"),
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

        // Error message if any
        if (parseError != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
            Text(text = parseError ?: "", color = Color(0xFFEF4444), fontSize = 11.sp, fontFamily = CairoFontFamily)
          }
        }

        // Preview Card when parsed successfully
        AnimatedVisibility(visible = parsedResult != null) {
          parsedResult?.let { (circle, members) ->
            Column {
              Spacer(modifier = Modifier.height(16.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color(0x2410B981))
                  .border(1.dp, LiquidEmeraldNeon, RoundedCornerShape(16.dp))
                  .padding(14.dp)
              ) {
                Column {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = LiquidEmeraldNeon, modifier = Modifier.size(16.dp))
                    Text(
                      text = if (isArabic) "تم التعرف على ملف الجمعية بنجاح ✓" else "Circle JSON Verified ✓",
                      fontFamily = CairoFontFamily,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      color = LiquidEmeraldNeon
                    )
                  }
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "${circle.titleAr} (${circle.titleEn})",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextCrystalWhite
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = if (isArabic)
                      "القسط: ${circle.currencySymbol}${circle.monthlyContribution} • المدة: ${circle.totalMonths} أشهر • الأعضاء: ${members.size} عضو"
                    else
                      "Dues: ${circle.currencySymbol}${circle.monthlyContribution} • Duration: ${circle.totalMonths} mo • Members: ${members.size}",
                    fontFamily = CairoFontFamily,
                    fontSize = 11.sp,
                    color = TextSilver
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Confirm & Import Button
        Button(
          onClick = {
            parsedResult?.let { (circle, members) ->
              onImport(circle, members)
            }
          },
          enabled = parsedResult != null,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color(0x22FFFFFF)
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("confirm_import_circle_button")
            .background(
              if (parsedResult != null) Brush.horizontalGradient(listOf(Color(0xFF047857), LiquidEmeraldNeon))
              else Brush.horizontalGradient(listOf(Color(0x33FFFFFF), Color(0x33FFFFFF))),
              RoundedCornerShape(16.dp)
            )
            .border(1.2.dp, Color(0x80FFFFFF), RoundedCornerShape(16.dp))
            .padding(vertical = 4.dp)
        ) {
          Text(
            text = if (isArabic) "تأكيد واستيراد الجمعية إلى التطبيق 📥" else "Confirm & Import Circle 📥",
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (parsedResult != null) Color.White else TextMutedGray
          )
        }
      }
    }
  }
}
