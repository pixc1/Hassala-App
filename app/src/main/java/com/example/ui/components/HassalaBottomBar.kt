package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.model.ActiveTab
import com.example.model.AppLanguage
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.LiquidEmeraldNeon
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.TajawalFontFamily
import com.example.ui.theme.TextCrystalWhite
import com.example.ui.theme.TextMutedGray
import com.example.ui.theme.TextSilver

@Composable
fun HassalaBottomBar(
  currentTab: ActiveTab,
  onTabSelected: (ActiveTab) -> Unit,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val isArabic = language == AppLanguage.ARABIC

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 20.dp, vertical = 12.dp),
    color = Color.Transparent
  ) {
    // iOS 26 Floating Liquid Acrylic Dock
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(32.dp))
        .background(
          Brush.linearGradient(
            colors = listOf(
              Color(0x28FFFFFF),
              Color(0x180D2115),
              Color(0x24000000)
            )
          )
        )
        .border(
          1.2.dp,
          Brush.linearGradient(
            colors = listOf(
              Color(0x66FFFFFF),
              Color(0x1AFFFFFF),
              Color(0x38F59E0B)
            )
          ),
          RoundedCornerShape(32.dp)
        )
        .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Tab 1: Dashboard
        LiquidBottomNavItem(
          title = if (isArabic) "حلقة الادخار" else "Circle",
          selected = currentTab == ActiveTab.DASHBOARD,
          activeIcon = Icons.Filled.Savings,
          inactiveIcon = Icons.Outlined.Savings,
          isArabic = isArabic,
          tag = "nav_tab_dashboard",
          onClick = { onTabSelected(ActiveTab.DASHBOARD) }
        )

        // Tab 2: Members
        LiquidBottomNavItem(
          title = if (isArabic) "الأعضاء" else "Members",
          selected = currentTab == ActiveTab.MEMBERS,
          activeIcon = Icons.Filled.Group,
          inactiveIcon = Icons.Outlined.Group,
          isArabic = isArabic,
          tag = "nav_tab_members",
          onClick = { onTabSelected(ActiveTab.MEMBERS) }
        )

        // Tab 3: Timeline
        LiquidBottomNavItem(
          title = if (isArabic) "الجدول" else "Timeline",
          selected = currentTab == ActiveTab.TIMELINE,
          activeIcon = Icons.Filled.CalendarMonth,
          inactiveIcon = Icons.Outlined.CalendarMonth,
          isArabic = isArabic,
          tag = "nav_tab_timeline",
          onClick = { onTabSelected(ActiveTab.TIMELINE) }
        )

        // Tab 4: Settings & Accessibility
        LiquidBottomNavItem(
          title = if (isArabic) "الإعدادات" else "Settings",
          selected = currentTab == ActiveTab.SETTINGS,
          activeIcon = Icons.Filled.Settings,
          inactiveIcon = Icons.Outlined.Settings,
          isArabic = isArabic,
          tag = "nav_tab_settings",
          onClick = { onTabSelected(ActiveTab.SETTINGS) }
        )
      }
    }
  }
}

@Composable
private fun LiquidBottomNavItem(
  title: String,
  selected: Boolean,
  activeIcon: ImageVector,
  inactiveIcon: ImageVector,
  isArabic: Boolean,
  tag: String,
  onClick: () -> Unit
) {
  val iconColor by animateColorAsState(
    targetValue = if (selected) TextCrystalWhite else TextMutedGray,
    animationSpec = tween(220),
    label = "liquid_icon_color"
  )

  val interactionSource = remember { MutableInteractionSource() }

  val backgroundModifier = if (selected) {
    Modifier.background(
      Brush.horizontalGradient(
        colors = listOf(
          Color(0x3D10B981),
          Color(0x28059669)
        )
      )
    )
  } else Modifier

  Box(
    modifier = Modifier
      .testTag(tag)
      .clip(RoundedCornerShape(22.dp))
      .then(backgroundModifier)
      .border(
        if (selected) 1.dp else 0.dp,
        if (selected) Color(0x6634D399) else Color.Transparent,
        RoundedCornerShape(22.dp)
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null
      ) { onClick() }
      .padding(horizontal = 16.dp, vertical = 9.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(
        imageVector = if (selected) activeIcon else inactiveIcon,
        contentDescription = title,
        tint = if (selected) LiquidEmeraldNeon else iconColor,
        modifier = Modifier.size(20.dp)
      )

      if (selected) {
        Text(
          text = title,
          fontFamily = CairoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = TextCrystalWhite
        )
      }
    }
  }
}
