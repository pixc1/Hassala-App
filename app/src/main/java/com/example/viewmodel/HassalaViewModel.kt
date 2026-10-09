package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.HassalaDatabase
import com.example.data.local.entity.CircleEntity
import com.example.data.local.entity.MemberEntity
import com.example.data.local.entity.PaymentReceiptEntity
import com.example.data.repository.HassalaRepository
import com.example.model.ActiveTab
import com.example.model.AppLanguage
import com.example.model.CircleMember
import com.example.model.MemberFilter
import com.example.model.MemberPaymentStatus
import com.example.model.MoneyCircle
import com.example.model.PaymentReceipt
import com.example.util.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HassalaUiState(
  val circle: MoneyCircle,
  val members: List<CircleMember>,
  val receipts: List<PaymentReceipt> = emptyList(),
  val language: AppLanguage = AppLanguage.ARABIC,
  val activeTab: ActiveTab = ActiveTab.DASHBOARD,
  val memberFilter: MemberFilter = MemberFilter.ALL,
  val isPaymentModalOpen: Boolean = false,
  val isSecurityModalOpen: Boolean = false,
  val isAddMemberModalOpen: Boolean = false,
  val isReceiptsModalOpen: Boolean = false,
  val isEditCircleModalOpen: Boolean = false,
  val isImportCircleModalOpen: Boolean = false,
  val selectedMember: CircleMember? = null,
  val memberToManagePayment: CircleMember? = null,
  val managerCliqAlias: String = "HASSALA-0798765432",
  val feedbackToast: String? = null,
  // Accessibility & Settings states
  val isTtsSpeaking: Boolean = false,
  val ttsSpeechRate: Float = 1.0f,
  val ttsAutoReadEnabled: Boolean = false,
  val highContrastMode: Boolean = false,
  val fontSizeScale: Float = 1.0f, // 1.0f = Normal, 1.15f = Large, 1.30f = Extra Large
  val reduceMotion: Boolean = false,
  val hapticFeedbackEnabled: Boolean = true,
  val glowIntensity: Float = 1.0f
) {
  val paidMembersCount: Int
    get() = members.count { it.status == MemberPaymentStatus.PAID }

  val totalMembersCount: Int
    get() = members.size

  val collectedAmountThisMonth: Long
    get() = paidMembersCount * circle.monthlyContribution

  val progressFraction: Float
    get() = if (totalMembersCount > 0) paidMembersCount.toFloat() / totalMembersCount else 0f

  val currentUser: CircleMember?
    get() = members.firstOrNull { it.isCurrentUser } ?: members.firstOrNull()

  val isCurrentUserPaidThisMonth: Boolean
    get() = currentUser?.status == MemberPaymentStatus.PAID

  val currentMonthRecipient: CircleMember?
    get() = members.firstOrNull { it.turnNumber == circle.currentCycleMonth } ?: members.firstOrNull()
}

class HassalaViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: HassalaRepository
  private val ttsManager: TtsManager = TtsManager(application)
  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  private val _uiState = MutableStateFlow(
    HassalaUiState(
      circle = MoneyCircle(
        id = "circle_primary",
        titleEn = "My Money Circle",
        titleAr = "جمعيتي الخاصة",
        categoryEn = "Community Savings Pool",
        categoryAr = "حلقة ادخار عائلية",
        totalPoolAmount = 0L,
        monthlyContribution = 1000L,
        currencySymbol = "ر.س",
        totalMonths = 10,
        currentCycleMonth = 1,
        nextPayoutDateEn = "First of next month",
        nextPayoutDateAr = "الأول من الشهر القادم",
        daysRemaining = 30,
        userTurn = 1,
        escrowGuaranteeId = "HSSL-ESCROW-2026-A",
        shariaCertified = true
      ),
      members = emptyList(),
      language = AppLanguage.ARABIC
    )
  )
  val uiState: StateFlow<HassalaUiState> = _uiState.asStateFlow()

  init {
    val database = HassalaDatabase.getDatabase(application)
    repository = HassalaRepository(
      circleDao = database.circleDao(),
      memberDao = database.memberDao(),
      receiptDao = database.paymentReceiptDao()
    )

    // Sync TTS speaking state with UI
    viewModelScope.launch {
      ttsManager.isSpeaking.collect { speaking ->
        _uiState.update { it.copy(isTtsSpeaking = speaking) }
      }
    }

    // Continuously observe database entities so members are always in sync and never lost
    viewModelScope.launch {
      combine(
        repository.circleFlow,
        repository.membersFlow,
        repository.receiptsFlow
      ) { circleEntity, memberEntities, receiptEntities ->
        Triple(circleEntity, memberEntities, receiptEntities)
      }.collect { (circleEntity, memberEntities, receiptEntities) ->
        val mappedCircle = circleEntity?.toDomain() ?: _uiState.value.circle
        val mappedMembers = memberEntities.map { it.toDomain() }
        val mappedReceipts = receiptEntities.map { it.toDomain() }

        _uiState.update { current ->
          current.copy(
            circle = mappedCircle,
            members = mappedMembers,
            receipts = mappedReceipts
          )
        }
      }
    }

    // Ensure database bootstrap without blocking flow collection
    viewModelScope.launch {
      try {
        repository.ensureInitialData()
      } catch (e: Exception) {
        android.util.Log.e("HassalaViewModel", "Error in ensureInitialData", e)
      }
    }
  }

  fun performHapticFeedback() {
    if (_uiState.value.hapticFeedbackEnabled && vibrator != null && vibrator.hasVibrator()) {
      try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
          @Suppress("DEPRECATION")
          vibrator.vibrate(25)
        }
      } catch (_: Exception) {}
    }
  }

  // --- Voice Reader / Text-to-Speech (TTS) Methods ---

  fun speakCircleSummary() {
    performHapticFeedback()
    val state = _uiState.value
    ttsManager.speakCircleSummary(
      circle = state.circle,
      members = state.members,
      language = state.language,
      speechRate = state.ttsSpeechRate
    )
  }

  fun speakText(text: String) {
    performHapticFeedback()
    val state = _uiState.value
    ttsManager.speak(text, state.language, state.ttsSpeechRate)
  }

  fun stopSpeaking() {
    performHapticFeedback()
    ttsManager.stop()
  }

  fun setTtsSpeechRate(rate: Float) {
    performHapticFeedback()
    _uiState.update { it.copy(ttsSpeechRate = rate) }
    speakText(if (_uiState.value.language == AppLanguage.ARABIC) "تم ضبط سرعة الصوت" else "Speech rate adjusted")
  }

  fun toggleTtsAutoRead() {
    performHapticFeedback()
    _uiState.update { it.copy(ttsAutoReadEnabled = !it.ttsAutoReadEnabled) }
  }

  // --- Accessibility Settings ---

  fun toggleHighContrastMode() {
    performHapticFeedback()
    _uiState.update { it.copy(highContrastMode = !it.highContrastMode) }
  }

  fun setFontSizeScale(scale: Float) {
    performHapticFeedback()
    _uiState.update { it.copy(fontSizeScale = scale) }
  }

  fun toggleReduceMotion() {
    performHapticFeedback()
    _uiState.update { it.copy(reduceMotion = !it.reduceMotion) }
  }

  fun toggleHapticFeedback() {
    _uiState.update { it.copy(hapticFeedbackEnabled = !it.hapticFeedbackEnabled) }
    performHapticFeedback()
  }

  fun setGlowIntensity(intensity: Float) {
    performHapticFeedback()
    _uiState.update { it.copy(glowIntensity = intensity) }
  }

  // --- App Navigation & Circle Methods ---

  fun toggleLanguage() {
    performHapticFeedback()
    _uiState.update { current ->
      val newLang = if (current.language == AppLanguage.ENGLISH) AppLanguage.ARABIC else AppLanguage.ENGLISH
      current.copy(language = newLang)
    }
  }

  fun setActiveTab(tab: ActiveTab) {
    performHapticFeedback()
    _uiState.update { it.copy(activeTab = tab) }
  }

  fun setMemberFilter(filter: MemberFilter) {
    performHapticFeedback()
    _uiState.update { it.copy(memberFilter = filter) }
  }

  fun openMemberPaymentManager(member: CircleMember) {
    performHapticFeedback()
    _uiState.update { it.copy(memberToManagePayment = member) }
  }

  fun closeMemberPaymentManager() {
    _uiState.update { it.copy(memberToManagePayment = null) }
  }

  fun toggleMemberPaymentStatus(memberId: String, paymentType: String = "كاش") {
    performHapticFeedback()
    viewModelScope.launch {
      repository.toggleMemberPayment(memberId, paymentType)
      val currentLang = _uiState.value.language
      val toastMsg = if (currentLang == AppLanguage.ARABIC) {
        "تم تحديث حالة الدفع ($paymentType) بنجاح"
      } else {
        "Member payment status updated ($paymentType)"
      }
      _uiState.update { it.copy(feedbackToast = toastMsg, memberToManagePayment = null) }
    }
  }

  fun setMemberPaymentStatus(memberId: String, isPaid: Boolean, paymentType: String = "كاش") {
    performHapticFeedback()
    viewModelScope.launch {
      repository.setMemberPaymentStatus(memberId, isPaid, paymentType)
      val currentLang = _uiState.value.language
      val toastMsg = if (currentLang == AppLanguage.ARABIC) {
        if (isPaid) "تم توثيق السداد ($paymentType) وإصدار السند فورياً" else "تم إلغاء السداد وتحويل العضو إلى معلق"
      } else {
        if (isPaid) "Payment recorded ($paymentType) & receipt generated" else "Payment revoked, marked as pending"
      }
      _uiState.update { it.copy(feedbackToast = toastMsg, memberToManagePayment = null) }
    }
  }

  fun switchMemberPaymentMethod(memberId: String, newMethod: String) {
    performHapticFeedback()
    viewModelScope.launch {
      repository.switchMemberPaymentMethod(memberId, newMethod)
      val currentLang = _uiState.value.language
      val toastMsg = if (currentLang == AppLanguage.ARABIC) {
        "تم تحويل طريقة السداد إلى ($newMethod)"
      } else {
        "Payment method changed to ($newMethod)"
      }
      _uiState.update { it.copy(feedbackToast = toastMsg, memberToManagePayment = null) }
    }
  }

  fun switchReceiptPaymentMethod(receiptId: String, newMethod: String) {
    performHapticFeedback()
    viewModelScope.launch {
      repository.updateReceiptPaymentMethod(receiptId, newMethod)
      val currentLang = _uiState.value.language
      val toastMsg = if (currentLang == AppLanguage.ARABIC) {
        "تم تعديل السند إلى ($newMethod)"
      } else {
        "Receipt updated to ($newMethod)"
      }
      showToast(toastMsg)
    }
  }

  fun updateManagerCliqAlias(alias: String) {
    val clean = alias.trim().ifEmpty { "HASSALA-0798765432" }
    _uiState.update { it.copy(managerCliqAlias = clean) }
    val currentLang = _uiState.value.language
    showToast(if (currentLang == AppLanguage.ARABIC) "تم حفظ معرّف كليك لمدير الجمعية" else "Manager CliQ alias saved")
  }

  fun payCurrentUserDues(paymentMethod: String = "كاش") {
    performHapticFeedback()
    viewModelScope.launch {
      repository.recordUserPayment(paymentMethod)
      val currentLang = _uiState.value.language
      val methodLabel = if (paymentMethod.contains("كليك", ignoreCase = true) || paymentMethod.contains("cliq", ignoreCase = true)) "كليك" else "كاش"
      val toastMsg = if (currentLang == AppLanguage.ARABIC) {
        "تم توثيق دفع القسط ($methodLabel) وإصدار سند رسمي!"
      } else {
        "Payment recorded ($methodLabel) and official receipt issued!"
      }
      _uiState.update {
        it.copy(
          isPaymentModalOpen = false,
          feedbackToast = toastMsg
        )
      }
    }
  }

  fun addNewMember(name: String, phone: String, contribution: Long, turn: Int, isCurrentUser: Boolean = false) {
    performHapticFeedback()
    viewModelScope.launch {
      repository.addMember(name, phone, contribution, turn, isCurrentUser)
      val currentLang = _uiState.value.language
      val toastMsg = if (currentLang == AppLanguage.ARABIC) {
        "تمت إضافة ($name) إلى الجمعية بنجاح"
      } else {
        "Added ($name) to circle successfully"
      }
      _uiState.update {
        it.copy(
          isAddMemberModalOpen = false,
          feedbackToast = toastMsg
        )
      }
    }
  }

  fun deleteMember(memberId: String) {
    performHapticFeedback()
    viewModelScope.launch {
      val member = _uiState.value.members.firstOrNull { it.id == memberId }
      val name = member?.let { if (_uiState.value.language == AppLanguage.ARABIC) it.nameAr else it.nameEn } ?: ""
      repository.deleteMember(memberId)
      val currentLang = _uiState.value.language
      val toastMsg = if (currentLang == AppLanguage.ARABIC) {
        "تم حذف المشترك ($name) من الجمعية"
      } else {
        "Removed ($name) from circle"
      }
      _uiState.update {
        it.copy(
          selectedMember = null,
          feedbackToast = toastMsg
        )
      }
    }
  }

  fun updateCircleDetails(
    titleAr: String,
    titleEn: String,
    monthlyContribution: Long,
    totalMonths: Int,
    currencySymbol: String,
    currentMonth: Int
  ) {
    performHapticFeedback()
    viewModelScope.launch {
      val currentCircle = _uiState.value.circle
      val effectiveTotalMonths = maxOf(totalMonths, _uiState.value.members.size)
      val updatedCircle = currentCircle.copy(
        titleAr = titleAr.ifBlank { currentCircle.titleAr },
        titleEn = titleEn.ifBlank { currentCircle.titleEn },
        monthlyContribution = monthlyContribution,
        totalMonths = effectiveTotalMonths,
        currencySymbol = currencySymbol.ifBlank { currentCircle.currencySymbol },
        currentCycleMonth = currentMonth
      )

      repository.updateCircle(
        titleAr = titleAr.ifBlank { currentCircle.titleAr },
        titleEn = titleEn.ifBlank { currentCircle.titleEn },
        monthlyContribution = monthlyContribution,
        totalMonths = effectiveTotalMonths,
        currencySymbol = currencySymbol.ifBlank { currentCircle.currencySymbol },
        currentMonth = currentMonth
      )

      val currentLang = _uiState.value.language
      val toastMsg = if (currentLang == AppLanguage.ARABIC) {
        "تم حفظ إعدادات الجمعية وتحديث البيانات بنجاح"
      } else {
        "Circle settings saved successfully"
      }
      _uiState.update {
        it.copy(
          isEditCircleModalOpen = false,
          feedbackToast = toastMsg
        )
      }
    }
  }

  fun clearAllMembers() {
    performHapticFeedback()
    viewModelScope.launch {
      repository.clearAllMembers()
      val currentLang = _uiState.value.language
      val toastMsg = if (currentLang == AppLanguage.ARABIC) {
        "تم مسح جميع الأعضاء وأصبحت الجمعية فارغة"
      } else {
        "All members cleared from circle"
      }
      _uiState.update {
        it.copy(
          isEditCircleModalOpen = false,
          selectedMember = null,
          feedbackToast = toastMsg
        )
      }
    }
  }

  fun importCircle(circle: MoneyCircle, members: List<CircleMember>) {
    performHapticFeedback()
    viewModelScope.launch {
      repository.importCircle(circle, members)

      val currentLang = _uiState.value.language
      val toastMsg = if (currentLang == AppLanguage.ARABIC) {
        "تم استيراد الجمعية و${members.size} أعضاء بنجاح!"
      } else {
        "Successfully imported circle and ${members.size} members!"
      }
      _uiState.update {
        it.copy(
          isImportCircleModalOpen = false,
          feedbackToast = toastMsg
        )
      }
    }
  }

  fun advanceCycleMonth() {
    performHapticFeedback()
    viewModelScope.launch {
      repository.advanceCycleMonth()
      val curCircle = _uiState.value.circle
      val nextMonth = if (curCircle.currentCycleMonth < curCircle.totalMonths) {
        curCircle.currentCycleMonth + 1
      } else 1
      val currentLang = _uiState.value.language
      val toastMsg = if (currentLang == AppLanguage.ARABIC) {
        "تم الانتقال إلى الجولة ($nextMonth)"
      } else {
        "Advanced to cycle month ($nextMonth)"
      }
      _uiState.update { it.copy(feedbackToast = toastMsg) }
    }
  }

  fun selectMember(member: CircleMember?) {
    performHapticFeedback()
    _uiState.update { it.copy(selectedMember = member) }
  }

  fun setSecurityModalVisible(visible: Boolean) {
    performHapticFeedback()
    _uiState.update { it.copy(isSecurityModalOpen = visible) }
  }

  fun setPaymentModalVisible(visible: Boolean) {
    performHapticFeedback()
    _uiState.update { it.copy(isPaymentModalOpen = visible) }
  }

  fun setAddMemberModalVisible(visible: Boolean) {
    performHapticFeedback()
    _uiState.update { it.copy(isAddMemberModalOpen = visible) }
  }

  fun setReceiptsModalVisible(visible: Boolean) {
    performHapticFeedback()
    _uiState.update { it.copy(isReceiptsModalOpen = visible) }
  }

  fun setEditCircleModalVisible(visible: Boolean) {
    performHapticFeedback()
    _uiState.update { it.copy(isEditCircleModalOpen = visible) }
  }

  fun setImportCircleModalVisible(visible: Boolean) {
    performHapticFeedback()
    _uiState.update { it.copy(isImportCircleModalOpen = visible) }
  }

  fun deleteReceipt(receiptId: String) {
    viewModelScope.launch {
      repository.deleteReceipt(receiptId)
      showToast(
        if (_uiState.value.language == AppLanguage.ARABIC) "تم حذف السند بنجاح"
        else "Receipt deleted successfully"
      )
    }
  }

  fun clearAllReceipts() {
    viewModelScope.launch {
      repository.clearAllReceipts()
      showToast(
        if (_uiState.value.language == AppLanguage.ARABIC) "تم مسح جميع السجلات والسندات"
        else "All receipts cleared"
      )
    }
  }

  fun generateReceiptForMember(memberId: String) {
    viewModelScope.launch {
      repository.generateReceiptForMember(memberId)
      showToast(
        if (_uiState.value.language == AppLanguage.ARABIC) "تم إصدار سند سداد رسمي للعضو"
        else "Official receipt generated for member"
      )
    }
  }

  fun syncReceiptsForPaidMembers() {
    viewModelScope.launch {
      val paidMembers = _uiState.value.members.filter { it.status == MemberPaymentStatus.PAID }
      val existingReceiptMemberIds = _uiState.value.receipts.map { it.memberId }.toSet()
      val missing = paidMembers.filter { it.id !in existingReceiptMemberIds }
      missing.forEach {
        repository.generateReceiptForMember(it.id)
      }
      if (missing.isNotEmpty()) {
        showToast(
          if (_uiState.value.language == AppLanguage.ARABIC) "تم تحديث السندات وتوليد ${missing.size} سند فعلي للأعضاء المسددين"
          else "Updated receipts and generated ${missing.size} receipts for paid members"
        )
      }
    }
  }

  private fun showToast(msg: String) {
    _uiState.update { it.copy(feedbackToast = msg) }
  }

  fun clearToast() {
    _uiState.update { it.copy(feedbackToast = null) }
  }

  override fun onCleared() {
    super.onCleared()
    ttsManager.shutdown()
  }

  private fun CircleEntity.toDomain() = MoneyCircle(
    id = id,
    titleEn = titleEn,
    titleAr = titleAr,
    categoryEn = categoryEn,
    categoryAr = categoryAr,
    totalPoolAmount = totalPoolAmount,
    monthlyContribution = monthlyContribution,
    currencySymbol = currencySymbol,
    totalMonths = totalMonths,
    currentCycleMonth = currentCycleMonth,
    nextPayoutDateEn = nextPayoutDateEn,
    nextPayoutDateAr = nextPayoutDateAr,
    daysRemaining = daysRemaining,
    userTurn = userTurn,
    escrowGuaranteeId = escrowGuaranteeId,
    shariaCertified = true
  )

  private fun MemberEntity.toDomain() = CircleMember(
    id = id,
    nameEn = nameEn,
    nameAr = nameAr,
    roleEn = roleEn,
    roleAr = roleAr,
    turnNumber = turnNumber,
    payoutMonthEn = payoutMonthEn,
    payoutMonthAr = payoutMonthAr,
    monthlyContribution = monthlyContribution,
    status = if (isPaid) MemberPaymentStatus.PAID else MemberPaymentStatus.PENDING,
    isCurrentUser = isCurrentUser,
    avatarInitials = avatarInitials,
    avatarColorHex = avatarColorHex,
    phone = phone,
    paidTimestampEn = paidTimestampEn,
    paidTimestampAr = paidTimestampAr
  )

  private fun PaymentReceiptEntity.toDomain() = PaymentReceipt(
    receiptId = receiptId,
    memberId = memberId,
    memberNameEn = memberNameEn,
    memberNameAr = memberNameAr,
    amount = amount,
    currencySymbol = currencySymbol,
    roundNumber = roundNumber,
    paymentMethod = paymentMethod,
    formattedDateEn = formattedDateEn,
    formattedDateAr = formattedDateAr
  )
}
