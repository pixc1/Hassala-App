package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.ui.theme.LiquidEmeraldNeon
import com.example.ui.theme.LiquidGold
import com.example.ui.theme.LiquidGoldBright
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.TajawalFontFamily
import com.example.ui.theme.TextCrystalWhite
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextSilver

@Composable
fun SecurityModal(
  escrowId: String,
  language: AppLanguage,
  onDismiss: () -> Unit
) {
  val isArabic = language == AppLanguage.ARABIC

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
            listOf(Color(0xFF0F2317), Color(0xFF07120B), Color(0xFF030705))
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
        modifier = Modifier.padding(24.dp),
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
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = LiquidGoldBright,
                modifier = Modifier.size(20.dp)
              )
            }
            Text(
              text = if (isArabic) "ضمان الحماية والامتثال" else "Security & Sharia Guarantee",
              fontFamily = if (isArabic) TajawalFontFamily else OutfitFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = TextCrystalWhite
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_security_modal_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextMutedGray
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Certificate Badge Box (Liquid Glass)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x2910B981))
            .border(1.dp, Color(0x6634D399), RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = null,
              tint = LiquidEmeraldNeon,
              modifier = Modifier.size(26.dp)
            )
            Column {
              Text(
                text = if (isArabic) "شهادة الضمان المشفر" else "Escrow Vault Certificate",
                fontFamily = if (isArabic) TajawalFontFamily else OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = LiquidEmeraldNeon
              )
              Text(
                text = "ID: $escrowId",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = TextSilver
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security Pillars
        SecurityPillarItem(
          icon = Icons.Default.AccountBalance,
          title = if (isArabic) "حساب ضمان بنكي معزول" else "Segregated Escrow Accounts",
          description = if (isArabic)
            "يتم إيداع جميع المساهمات في حساب ضمان مصرفي مرخص ومفصول بالكامل عن أموال المنصة."
          else "All contributions are held in tier-1 regulated banking escrow accounts until the scheduled payout turn.",
          isArabic = isArabic
        )

        SecurityPillarItem(
          icon = Icons.Default.Gavel,
          title = if (isArabic) "مطابقة لأحكام الشريعة الإسلامية" else "100% Sharia-Compliant ROSCA",
          description = if (isArabic)
            "صفر فوائد ربوية وصفر عمولات استغلالية. نظام جمعية تكافلي أصيل موثق شرعياً."
          else "Zero interest (Riba-free) and zero hidden fees. Built on authentic cooperative mutual aid principles.",
          isArabic = isArabic
        )

        SecurityPillarItem(
          icon = Icons.Default.Lock,
          title = if (isArabic) "عقود رقمية ملزمة" else "Digitally Signed ROSCA Agreements",
          description = if (isArabic)
            "كل عضو يوقع عقداً رقمياً معتمداً يضمن التزام الجميع بسداد الأقساط حتى نهاية الدورة."
          else "Every member is identity-verified (KYC) with a legally enforceable digital commitment bond.",
          isArabic = isArabic
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp))
        ) {
          Text(
            text = if (isArabic) "فهمت، متابعة التصفح" else "Understood, Continue",
            fontFamily = if (isArabic) TajawalFontFamily else OutfitFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
          )
        }
      }
    }
  }
}

@Composable
private fun SecurityPillarItem(
  icon: ImageVector,
  title: String,
  description: String,
  isArabic: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Box(
      modifier = Modifier
        .size(34.dp)
        .clip(CircleShape)
        .background(Color(0x22FFFFFF))
        .border(1.dp, Color(0x33FFFFFF), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = LiquidEmeraldNeon,
        modifier = Modifier.size(17.dp)
      )
    }

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontFamily = if (isArabic) TajawalFontFamily else OutfitFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = TextCrystalWhite
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = description,
        fontFamily = if (isArabic) TajawalFontFamily else OutfitFontFamily,
        fontSize = 11.sp,
        color = TextSilver,
        lineHeight = 16.sp
      )
    }
  }
}
