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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Icon
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
import com.example.model.AppLanguage
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.LiquidEmeraldNeon
import com.example.ui.theme.LiquidGold
import com.example.ui.theme.LiquidGoldBright
import com.example.ui.theme.TextCrystalWhite
import com.example.ui.theme.TextSilver

@Composable
fun HassalaTopBar(
  language: AppLanguage,
  onToggleLanguage: () -> Unit,
  onOpenSecurityModal: () -> Unit,
  onOpenConfigureCircle: () -> Unit,
  onShareJson: () -> Unit,
  onImportJson: () -> Unit,
  onExportPdf: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isArabic = language == AppLanguage.ARABIC

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(horizontal = 14.dp, vertical = 4.dp),
    color = Color.Transparent
  ) {
    // Floating iOS 26 Liquid Glass Bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .liquidGlassEffect(shape = RoundedCornerShape(26.dp), elevation = 0.dp)
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Main Row: Brand + Escrow Shield + Language
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Brand Emblem & Title
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f, fill = false)
          ) {
            // Liquid Holographic Emblem
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                  Brush.linearGradient(
                    colors = listOf(
                      Color(0xFF0D3325),
                      Color(0xFF041810),
                      Color(0xFF14241B)
                    )
                  )
                )
                .border(
                  1.5.dp,
                  Brush.linearGradient(
                    listOf(LiquidGoldBright, LiquidEmeraldNeon, Color(0x33FFFFFF))
                  ),
                  RoundedCornerShape(14.dp)
                ),
              contentAlignment = Alignment.Center
            ) {
              Box(
                modifier = Modifier
                  .size(22.dp)
                  .border(2.dp, LiquidGold, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Box(
                  modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(LiquidEmeraldNeon)
                )
              }
            }

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = if (isArabic) "حصّالة" else "Hassala",
                  fontFamily = CairoFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 20.sp,
                  color = TextCrystalWhite,
                  letterSpacing = 0.2.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x29F59E0B))
                    .border(1.dp, Color(0x80F59E0B), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = if (isArabic) "موثق" else "PRO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = LiquidGoldBright,
                    fontFamily = CairoFontFamily
                  )
                }
              }

              Text(
                text = if (isArabic) "نظام الجمعيات المالية الذكي" else "Liquid ROSCA Protocol",
                fontFamily = CairoFontFamily,
                fontSize = 10.sp,
                color = TextSilver
              )
            }
          }

          // Top right actions: Security Shield + Language Switcher
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            // Escrow Shield Liquid Button
            Box(
              modifier = Modifier
                .testTag("security_shield_button")
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0x22FFFFFF))
                .border(1.dp, Color(0x40FFFFFF), CircleShape)
                .clickable { onOpenSecurityModal() },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = "Escrow Security",
                tint = LiquidGoldBright,
                modifier = Modifier.size(16.dp)
              )
            }

            // Language Switcher Capsule
            Box(
              modifier = Modifier
                .testTag("language_toggle_button")
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0x26FFFFFF))
                .border(
                  1.dp,
                  if (isArabic) LiquidGold.copy(alpha = 0.8f) else Color(0x4DFFFFFF),
                  RoundedCornerShape(18.dp)
                )
                .clickable { onToggleLanguage() }
                .padding(horizontal = 9.dp, vertical = 5.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Outlined.Language,
                  contentDescription = "Language",
                  tint = if (isArabic) LiquidEmeraldNeon else TextSilver,
                  modifier = Modifier.size(13.dp)
                )
                Text(
                  text = if (isArabic) "EN" else "عربي",
                  fontFamily = CairoFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = TextCrystalWhite
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sub Quick Action Dock (Configure, Share JSON, Import JSON, Export PDF)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // 1. Configure Circle Capsule
          TopBarActionCapsule(
            icon = Icons.Default.Tune,
            label = if (isArabic) "إعدادات الجمعية" else "Configure",
            accentColor = LiquidGoldBright,
            tag = "topbar_configure_button",
            onClick = onOpenConfigureCircle
          )

          // 2. Share JSON Capsule
          TopBarActionCapsule(
            icon = Icons.Default.Share,
            label = if (isArabic) "مشاركة JSON" else "Share JSON",
            accentColor = LiquidEmeraldNeon,
            tag = "topbar_share_json_button",
            onClick = onShareJson
          )

          // 3. Import JSON Capsule
          TopBarActionCapsule(
            icon = Icons.Default.CloudDownload,
            label = if (isArabic) "استيراد JSON" else "Import JSON",
            accentColor = Color(0xFF38BDF8),
            tag = "topbar_import_json_button",
            onClick = onImportJson
          )

          // 4. Export PDF Capsule
          TopBarActionCapsule(
            icon = Icons.Default.PictureAsPdf,
            label = if (isArabic) "تقرير PDF" else "Export PDF",
            accentColor = Color(0xFFF43F5E),
            tag = "topbar_export_pdf_button",
            onClick = onExportPdf
          )
        }
      }
    }
  }
}

@Composable
private fun TopBarActionCapsule(
  icon: ImageVector,
  label: String,
  accentColor: Color,
  tag: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .testTag(tag)
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0x1AFFFFFF))
      .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(horizontal = 9.dp, vertical = 5.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = accentColor,
        modifier = Modifier.size(13.dp)
      )
      Text(
        text = label,
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        color = TextCrystalWhite
      )
    }
  }
}
