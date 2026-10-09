package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ActiveTab
import com.example.model.AppLanguage
import com.example.ui.components.AddMemberModal
import com.example.ui.components.AmbientBackgroundMesh
import com.example.ui.components.ConfigureCircleModal
import com.example.ui.components.HassalaBottomBar
import com.example.ui.components.HassalaTopBar
import com.example.ui.components.ImportCircleModal
import com.example.ui.components.MemberDetailModal
import com.example.ui.components.PaymentModal
import com.example.ui.components.ReceiptsModal
import com.example.ui.components.SecurityModal
import com.example.ui.components.liquidGlassEffect
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MembersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TimelineScreen
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.HassalaTheme
import com.example.ui.theme.LiquidEmeraldNeon
import com.example.ui.theme.LiquidGoldBright
import com.example.util.JsonCircleManager
import com.example.util.PdfReportGenerator
import com.example.viewmodel.HassalaViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
  private val viewModel: HassalaViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      HassalaTheme {
        HassalaApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun HassalaApp(viewModel: HassalaViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current

  // Dynamic RTL / LTR layout direction based on language selection
  val layoutDirection = if (uiState.language == AppLanguage.ARABIC) {
    LayoutDirection.Rtl
  } else {
    LayoutDirection.Ltr
  }

  // Handle hardware / gesture back navigation gracefully
  BackHandler(
    enabled = uiState.activeTab != ActiveTab.DASHBOARD ||
        uiState.isPaymentModalOpen ||
        uiState.isSecurityModalOpen ||
        uiState.isAddMemberModalOpen ||
        uiState.isReceiptsModalOpen ||
        uiState.isEditCircleModalOpen ||
        uiState.isImportCircleModalOpen ||
        uiState.selectedMember != null
  ) {
    when {
      uiState.isPaymentModalOpen -> viewModel.setPaymentModalVisible(false)
      uiState.isSecurityModalOpen -> viewModel.setSecurityModalVisible(false)
      uiState.isAddMemberModalOpen -> viewModel.setAddMemberModalVisible(false)
      uiState.isReceiptsModalOpen -> viewModel.setReceiptsModalVisible(false)
      uiState.isEditCircleModalOpen -> viewModel.setEditCircleModalVisible(false)
      uiState.isImportCircleModalOpen -> viewModel.setImportCircleModalVisible(false)
      uiState.selectedMember != null -> viewModel.selectMember(null)
      uiState.activeTab != ActiveTab.DASHBOARD -> viewModel.setActiveTab(ActiveTab.DASHBOARD)
    }
  }

  // Auto-dismiss toast after 3 seconds
  LaunchedEffect(uiState.feedbackToast) {
    if (uiState.feedbackToast != null) {
      delay(3000)
      viewModel.clearToast()
    }
  }

  CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
    Box(modifier = Modifier.fillMaxSize()) {
      // Atmospheric iOS 26 Liquid Glass Ambient Background Mesh
      AmbientBackgroundMesh(
        reduceMotion = uiState.reduceMotion,
        glowIntensity = uiState.glowIntensity
      )

      Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
          HassalaTopBar(
            language = uiState.language,
            onToggleLanguage = { viewModel.toggleLanguage() },
            onOpenSecurityModal = { viewModel.setSecurityModalVisible(true) },
            onOpenConfigureCircle = { viewModel.setEditCircleModalVisible(true) },
            onShareJson = {
              JsonCircleManager.shareCircleJsonFile(context, uiState.circle, uiState.members)
            },
            onImportJson = { viewModel.setImportCircleModalVisible(true) },
            onExportPdf = {
              PdfReportGenerator.generateAndSharePdfReport(context, uiState.circle, uiState.members)
            }
          )
        },
        bottomBar = {
          HassalaBottomBar(
            currentTab = uiState.activeTab,
            onTabSelected = { viewModel.setActiveTab(it) },
            language = uiState.language
          )
        }
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
          // Dynamic Fluid Animated Screen Transitions between tabs
          AnimatedContent(
            targetState = uiState.activeTab,
            transitionSpec = {
              if (uiState.reduceMotion) {
                fadeIn(animationSpec = tween(180)) togetherWith fadeOut(animationSpec = tween(180))
              } else {
                val isMovingForward = targetState.ordinal > initialState.ordinal
                val slideMultiplier = if (layoutDirection == LayoutDirection.Rtl) {
                  if (isMovingForward) -1 else 1
                } else {
                  if (isMovingForward) 1 else -1
                }

                (slideInHorizontally(
                  animationSpec = tween(340, easing = FastOutSlowInEasing)
                ) { width -> slideMultiplier * (width / 3) } + fadeIn(tween(260))) togetherWith
                    (slideOutHorizontally(
                      animationSpec = tween(340, easing = FastOutSlowInEasing)
                    ) { width -> -slideMultiplier * (width / 3) } + fadeOut(tween(200)))
              }
            },
            label = "liquid_screen_transition"
          ) { targetTab ->
            when (targetTab) {
              ActiveTab.DASHBOARD -> {
                DashboardScreen(
                  uiState = uiState,
                  onPayDuesClick = { viewModel.setPaymentModalVisible(true) },
                  onAdvanceCycleMonth = { viewModel.advanceCycleMonth() },
                  onViewMembersClick = { viewModel.setActiveTab(ActiveTab.MEMBERS) },
                  onViewSecurityClick = { viewModel.setSecurityModalVisible(true) },
                  onViewReceiptsClick = { viewModel.setReceiptsModalVisible(true) },
                  onConfigureCircleClick = { viewModel.setEditCircleModalVisible(true) },
                  onAddMemberClick = { viewModel.setAddMemberModalVisible(true) },
                  onShareJsonClick = {
                    JsonCircleManager.shareCircleJsonFile(context, uiState.circle, uiState.members)
                  },
                  onExportPdfClick = {
                    PdfReportGenerator.generateAndSharePdfReport(context, uiState.circle, uiState.members)
                  },
                  onImportJsonClick = { viewModel.setImportCircleModalVisible(true) },
                  onSpeakCircleSummary = { viewModel.speakCircleSummary() },
                  onStopSpeaking = { viewModel.stopSpeaking() }
                )
              }

              ActiveTab.MEMBERS -> {
                MembersScreen(
                  uiState = uiState,
                  onFilterChanged = { viewModel.setMemberFilter(it) },
                  onMemberClick = { viewModel.selectMember(it) },
                  onToggleMemberStatus = { viewModel.toggleMemberPaymentStatus(it) },
                  onDeleteMember = { viewModel.deleteMember(it) },
                  onAddMemberClick = { viewModel.setAddMemberModalVisible(true) },
                  onExportPdfClick = {
                    PdfReportGenerator.generateAndSharePdfReport(context, uiState.circle, uiState.members)
                  },
                  onShareJsonClick = {
                    JsonCircleManager.shareCircleJsonFile(context, uiState.circle, uiState.members)
                  }
                )
              }

              ActiveTab.TIMELINE -> {
                TimelineScreen(
                  uiState = uiState,
                  onMemberClick = { viewModel.selectMember(it) }
                )
              }

              ActiveTab.SETTINGS -> {
                SettingsScreen(
                  uiState = uiState,
                  onSpeakCircleSummary = { viewModel.speakCircleSummary() },
                  onStopSpeaking = { viewModel.stopSpeaking() },
                  onTestVoice = {
                    val sample = if (uiState.language == AppLanguage.ARABIC) {
                      "أهلاً بك في نظام حصالة الذكي للجمعيات المالية. تم تفعيل ميزة قراءة النصوص بنجاح."
                    } else {
                      "Welcome to Hassala Smart ROSCA system. Voice reading has been activated successfully."
                    }
                    viewModel.speakText(sample)
                  },
                  onSetSpeechRate = { viewModel.setTtsSpeechRate(it) },
                  onToggleAutoRead = { viewModel.toggleTtsAutoRead() },
                  onToggleHighContrast = { viewModel.toggleHighContrastMode() },
                  onSetFontSizeScale = { viewModel.setFontSizeScale(it) },
                  onToggleReduceMotion = { viewModel.toggleReduceMotion() },
                  onToggleHaptics = { viewModel.toggleHapticFeedback() },
                  onSetGlowIntensity = { viewModel.setGlowIntensity(it) },
                  onConfigureCircleClick = { viewModel.setEditCircleModalVisible(true) },
                  onShareJsonClick = {
                    JsonCircleManager.shareCircleJsonFile(context, uiState.circle, uiState.members)
                  },
                  onImportJsonClick = { viewModel.setImportCircleModalVisible(true) },
                  onExportPdfClick = {
                    PdfReportGenerator.generateAndSharePdfReport(context, uiState.circle, uiState.members)
                  },
                  onViewReceiptsClick = { viewModel.setReceiptsModalVisible(true) },
                  onViewSecurityClick = { viewModel.setSecurityModalVisible(true) },
                  onToggleLanguage = { viewModel.toggleLanguage() }
                )
              }
            }
          }
        }
      }

      // Feedback Toast notification
      AnimatedVisibility(
        visible = uiState.feedbackToast != null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 90.dp, start = 20.dp, end = 20.dp)
      ) {
        uiState.feedbackToast?.let { toast ->
          Box(
            modifier = Modifier
              .liquidGlassEffect(
                shape = RoundedCornerShape(16.dp),
                highContrast = uiState.highContrastMode,
                borderBrush = androidx.compose.ui.graphics.Brush.linearGradient(
                  listOf(LiquidEmeraldNeon, LiquidGoldBright)
                )
              )
              .padding(horizontal = 18.dp, vertical = 12.dp)
          ) {
            Text(
              text = toast,
              fontFamily = CairoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = (13 * uiState.fontSizeScale).sp,
              color = Color.White
            )
          }
        }
      }

      // 1. Payment Modal
      if (uiState.isPaymentModalOpen) {
        PaymentModal(
          circle = uiState.circle,
          language = uiState.language,
          managerCliqAlias = uiState.managerCliqAlias,
          onDismiss = { viewModel.setPaymentModalVisible(false) },
          onConfirmPayment = { method -> viewModel.payCurrentUserDues(method) }
        )
      }

      // 2. Security Modal
      if (uiState.isSecurityModalOpen) {
        SecurityModal(
          escrowId = uiState.circle.escrowGuaranteeId,
          language = uiState.language,
          onDismiss = { viewModel.setSecurityModalVisible(false) }
        )
      }

      // 3. Add Member Modal
      if (uiState.isAddMemberModalOpen) {
        AddMemberModal(
          language = uiState.language,
          defaultContribution = uiState.circle.monthlyContribution,
          currencySymbol = uiState.circle.currencySymbol,
          suggestedTurn = uiState.members.size + 1,
          onDismiss = { viewModel.setAddMemberModalVisible(false) },
          onConfirmAdd = { name, phone, contrib, turn ->
            viewModel.addNewMember(name, phone, contrib, turn)
          }
        )
      }

      // 4. Receipts Modal
      if (uiState.isReceiptsModalOpen) {
        ReceiptsModal(
          receipts = uiState.receipts,
          language = uiState.language,
          onDismiss = { viewModel.setReceiptsModalVisible(false) },
          onDeleteReceipt = { viewModel.deleteReceipt(it) },
          onClearAllReceipts = { viewModel.clearAllReceipts() },
          onSyncReceipts = { viewModel.syncReceiptsForPaidMembers() },
          onSwitchPaymentMethod = { receiptId, newMethod ->
            viewModel.switchReceiptPaymentMethod(receiptId, newMethod)
          }
        )
      }

      // 5. Configure / Edit Circle Modal
      if (uiState.isEditCircleModalOpen) {
        ConfigureCircleModal(
          circle = uiState.circle,
          language = uiState.language,
          onDismiss = { viewModel.setEditCircleModalVisible(false) },
          onSave = { titleAr, titleEn, contrib, months, currency, currentM ->
            viewModel.updateCircleDetails(
              titleAr = titleAr,
              titleEn = titleEn,
              monthlyContribution = contrib,
              totalMonths = months,
              currencySymbol = currency,
              currentMonth = currentM
            )
          },
          onClearAllMembers = {
            viewModel.clearAllMembers()
          }
        )
      }

      // 6. Import Circle (JSON) Modal
      if (uiState.isImportCircleModalOpen) {
        ImportCircleModal(
          language = uiState.language,
          onDismiss = { viewModel.setImportCircleModalVisible(false) },
          onImport = { circle, members ->
            viewModel.importCircle(circle, members)
          }
        )
      }

      // 7. Member Profile Detail Modal
      uiState.selectedMember?.let { member ->
        MemberDetailModal(
          member = member,
          language = uiState.language,
          currencySymbol = uiState.circle.currencySymbol,
          onDismiss = { viewModel.selectMember(null) },
          onToggleStatus = {
            viewModel.toggleMemberPaymentStatus(member.id)
            viewModel.selectMember(null)
          },
          onDeleteMember = {
            viewModel.deleteMember(member.id)
          }
        )
      }
    }
  }
}
