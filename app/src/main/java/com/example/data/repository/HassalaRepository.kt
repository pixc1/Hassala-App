package com.example.data.repository

import com.example.data.local.dao.CircleDao
import com.example.data.local.dao.MemberDao
import com.example.data.local.dao.PaymentReceiptDao
import com.example.data.local.entity.CircleEntity
import com.example.data.local.entity.MemberEntity
import com.example.data.local.entity.PaymentReceiptEntity
import com.example.model.CircleMember
import com.example.model.MemberPaymentStatus
import com.example.model.MoneyCircle
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class HassalaRepository(
  private val circleDao: CircleDao,
  private val memberDao: MemberDao,
  private val receiptDao: PaymentReceiptDao
) {
  val circleFlow: Flow<CircleEntity?> = circleDao.getCircle()
  val membersFlow: Flow<List<MemberEntity>> = memberDao.getMembers()
  val receiptsFlow: Flow<List<PaymentReceiptEntity>> = receiptDao.getReceipts()

  suspend fun ensureInitialData() {
    val existingCircle = circleDao.getCircleSync()
    if (existingCircle == null) {
      val defaultCircle = CircleEntity(
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
        escrowGuaranteeId = "HSSL-ESCROW-${(1000..9999).random()}-A"
      )
      circleDao.insertCircle(defaultCircle)
    }
  }

  private fun normalizePaymentType(method: String?): String {
    if (method == null) return "كاش"
    val lower = method.lowercase()
    return if (lower.contains("cliq") || lower.contains("كليك")) "كليك" else "كاش"
  }

  suspend fun toggleMemberPayment(memberId: String, paymentType: String = "كاش") {
    val members = memberDao.getMembersSync()
    val member = members.firstOrNull { it.id == memberId } ?: return
    val newPaid = !member.isPaid
    setMemberPaymentStatus(memberId, newPaid, paymentType)
  }

  suspend fun setMemberPaymentStatus(memberId: String, isPaid: Boolean, paymentType: String = "كاش") {
    val members = memberDao.getMembersSync()
    val member = members.firstOrNull { it.id == memberId } ?: return
    val chosenMethod = normalizePaymentType(paymentType)
    val now = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date())
    val nowAr = SimpleDateFormat("dd MMM yyyy، hh:mm a", Locale("ar")).format(Date())

    val timeEnWithMethod = if (isPaid) "$now ($chosenMethod)" else null
    val timeArWithMethod = if (isPaid) "$nowAr ($chosenMethod)" else null

    memberDao.updateMemberPaymentStatus(
      memberId = memberId,
      isPaid = isPaid,
      timeEn = timeEnWithMethod,
      timeAr = timeArWithMethod
    )

    val currentCircle = circleDao.getCircleSync()
    val round = currentCircle?.currentCycleMonth ?: 1
    val currency = currentCircle?.currencySymbol ?: "ر.س"

    if (isPaid) {
      // Remove any prior receipt for this round to prevent duplicates
      receiptDao.deleteReceiptForMemberRound(memberId, round)

      val randomCode = (10000..99999).random()
      val datePart = SimpleDateFormat("yyMMdd", Locale.US).format(Date())
      val receipt = PaymentReceiptEntity(
        receiptId = "HSL-REC-$datePart-$randomCode",
        circleId = currentCircle?.id ?: "circle_primary",
        memberId = member.id,
        memberNameEn = member.nameEn,
        memberNameAr = member.nameAr,
        amount = member.monthlyContribution,
        currencySymbol = currency,
        roundNumber = round,
        paymentMethod = chosenMethod,
        formattedDateEn = now,
        formattedDateAr = nowAr
      )
      receiptDao.insertReceipt(receipt)
    } else {
      // Remove receipt for this member and round
      receiptDao.deleteReceiptForMemberRound(memberId, round)
    }
  }

  suspend fun switchMemberPaymentMethod(memberId: String, newMethod: String) {
    val chosenMethod = normalizePaymentType(newMethod)
    val currentCircle = circleDao.getCircleSync()
    val round = currentCircle?.currentCycleMonth ?: 1
    receiptDao.updateReceiptMethodForMemberRound(memberId, round, chosenMethod)

    val members = memberDao.getMembersSync()
    val member = members.firstOrNull { it.id == memberId } ?: return
    if (member.isPaid) {
      val now = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date())
      val nowAr = SimpleDateFormat("dd MMM yyyy، hh:mm a", Locale("ar")).format(Date())
      memberDao.updateMemberPaymentStatus(
        memberId = memberId,
        isPaid = true,
        timeEn = "$now ($chosenMethod)",
        timeAr = "$nowAr ($chosenMethod)"
      )
    }
  }

  suspend fun updateReceiptPaymentMethod(receiptId: String, newMethod: String) {
    val chosenMethod = normalizePaymentType(newMethod)
    receiptDao.updateReceiptPaymentMethod(receiptId, chosenMethod)
  }

  suspend fun recordUserPayment(paymentMethod: String) {
    val members = memberDao.getMembersSync()
    val user = members.firstOrNull { it.isCurrentUser } ?: members.firstOrNull() ?: return
    val chosenMethod = normalizePaymentType(paymentMethod)
    setMemberPaymentStatus(user.id, true, chosenMethod)
  }

  suspend fun addMember(
    name: String,
    phone: String,
    contribution: Long,
    turn: Int,
    isCurrentUser: Boolean = false
  ) {
    val monthsEn = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val monthsAr = listOf("يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر")
    val monthIndex = ((turn - 1) % 12).coerceAtLeast(0)

    val initials = name.split(" ")
      .filter { it.isNotBlank() }
      .take(2)
      .joinToString("") { it.take(1).uppercase() }
      .ifEmpty { "M" }

    val colors = listOf(0xFF047857, 0xFFD4AF37, 0xFF059669, 0xFFB45309, 0xFF0F766E, 0xFF4338CA, 0xFF6B21A8, 0xFF15803D)
    val chosenColor = colors[(turn - 1).coerceAtLeast(0) % colors.size]

    val newMember = MemberEntity(
      id = "m_${UUID.randomUUID()}",
      circleId = "circle_primary",
      nameEn = name,
      nameAr = name,
      roleEn = if (isCurrentUser) "You" else "Member",
      roleAr = if (isCurrentUser) "أنت" else "عضو",
      turnNumber = turn,
      payoutMonthEn = monthsEn[monthIndex],
      payoutMonthAr = monthsAr[monthIndex],
      monthlyContribution = contribution,
      isPaid = false,
      isCurrentUser = isCurrentUser,
      avatarInitials = initials,
      avatarColorHex = chosenColor,
      phone = phone
    )

    memberDao.insertMember(newMember)

    // Recalculate circle total pool and total months
    val all = memberDao.getMembersSync()
    val totalPool = all.sumOf { it.monthlyContribution }
    val currentCircle = circleDao.getCircleSync()
    if (currentCircle != null) {
      circleDao.updateCircle(
        currentCircle.copy(
          totalMonths = maxOf(currentCircle.totalMonths, all.size),
          totalPoolAmount = totalPool
        )
      )
    }
  }

  suspend fun deleteMember(memberId: String) {
    memberDao.deleteMemberById(memberId)
    // Delete orphan receipts for removed member
    receiptDao.deleteReceiptsByMemberId(memberId)

    // Recalculate circle total pool
    val remaining = memberDao.getMembersSync()
    val totalPool = remaining.sumOf { it.monthlyContribution }
    val currentCircle = circleDao.getCircleSync()
    if (currentCircle != null) {
      circleDao.updateCircle(
        currentCircle.copy(
          totalPoolAmount = totalPool
        )
      )
    }
  }

  suspend fun updateCircle(
    titleAr: String,
    titleEn: String,
    monthlyContribution: Long,
    totalMonths: Int,
    currencySymbol: String,
    currentMonth: Int = 1,
    nextPayoutDateAr: String = "الأول من الشهر القادم",
    nextPayoutDateEn: String = "First of next month"
  ) {
    val existing = circleDao.getCircleSync() ?: return
    val members = memberDao.getMembersSync()
    val totalPool = if (members.isNotEmpty()) {
      members.sumOf { it.monthlyContribution }
    } else {
      monthlyContribution * totalMonths
    }

    val updated = existing.copy(
      titleAr = titleAr,
      titleEn = titleEn,
      monthlyContribution = monthlyContribution,
      totalMonths = maxOf(totalMonths, members.size),
      currencySymbol = currencySymbol,
      currentCycleMonth = currentMonth,
      totalPoolAmount = totalPool,
      nextPayoutDateAr = nextPayoutDateAr,
      nextPayoutDateEn = nextPayoutDateEn
    )
    circleDao.updateCircle(updated)
  }

  suspend fun clearAllMembers() {
    memberDao.deleteAllMembers()
    receiptDao.deleteAllReceipts()
    val currentCircle = circleDao.getCircleSync()
    if (currentCircle != null) {
      circleDao.updateCircle(
        currentCircle.copy(
          totalPoolAmount = 0L,
          currentCycleMonth = 1
        )
      )
    }
  }

  suspend fun deleteReceipt(receiptId: String) {
    receiptDao.deleteReceiptById(receiptId)
  }

  suspend fun clearAllReceipts() {
    receiptDao.deleteAllReceipts()
  }

  suspend fun generateReceiptForMember(memberId: String, paymentType: String = "كاش") {
    val members = memberDao.getMembersSync()
    val member = members.firstOrNull { it.id == memberId } ?: return
    val currentCircle = circleDao.getCircleSync()
    val round = currentCircle?.currentCycleMonth ?: 1
    val currency = currentCircle?.currencySymbol ?: "ر.س"
    val chosenMethod = normalizePaymentType(paymentType)

    val now = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date())
    val nowAr = SimpleDateFormat("dd MMM yyyy، hh:mm a", Locale("ar")).format(Date())

    val randomCode = (10000..99999).random()
    val datePart = SimpleDateFormat("yyMMdd", Locale.US).format(Date())
    val receipt = PaymentReceiptEntity(
      receiptId = "HSL-REC-$datePart-$randomCode",
      circleId = currentCircle?.id ?: "circle_primary",
      memberId = member.id,
      memberNameEn = member.nameEn,
      memberNameAr = member.nameAr,
      amount = member.monthlyContribution,
      currencySymbol = currency,
      roundNumber = round,
      paymentMethod = chosenMethod,
      formattedDateEn = now,
      formattedDateAr = nowAr
    )
    receiptDao.insertReceipt(receipt)
  }

  suspend fun importCircle(circle: MoneyCircle, members: List<CircleMember>) {
    // 1. Replace or update circle
    val circleEntity = CircleEntity(
      id = "circle_primary",
      titleEn = circle.titleEn,
      titleAr = circle.titleAr,
      categoryEn = circle.categoryEn,
      categoryAr = circle.categoryAr,
      totalPoolAmount = if (members.isNotEmpty()) members.sumOf { it.monthlyContribution } else circle.totalPoolAmount,
      monthlyContribution = circle.monthlyContribution,
      currencySymbol = circle.currencySymbol,
      totalMonths = if (members.isNotEmpty()) maxOf(circle.totalMonths, members.size) else circle.totalMonths,
      currentCycleMonth = circle.currentCycleMonth,
      nextPayoutDateEn = circle.nextPayoutDateEn,
      nextPayoutDateAr = circle.nextPayoutDateAr,
      daysRemaining = circle.daysRemaining,
      userTurn = circle.userTurn,
      escrowGuaranteeId = circle.escrowGuaranteeId
    )
    circleDao.insertCircle(circleEntity)

    // 2. Clear old members and receipts, then insert imported members
    memberDao.deleteAllMembers()
    receiptDao.deleteAllReceipts()

    val memberEntities = members.map { m ->
      MemberEntity(
        id = m.id.ifEmpty { "m_${UUID.randomUUID()}" },
        circleId = "circle_primary",
        nameEn = m.nameEn,
        nameAr = m.nameAr,
        roleEn = m.roleEn,
        roleAr = m.roleAr,
        turnNumber = m.turnNumber,
        payoutMonthEn = m.payoutMonthEn,
        payoutMonthAr = m.payoutMonthAr,
        monthlyContribution = m.monthlyContribution,
        isPaid = m.status == MemberPaymentStatus.PAID,
        isCurrentUser = m.isCurrentUser,
        avatarInitials = m.avatarInitials,
        avatarColorHex = m.avatarColorHex,
        phone = m.phone,
        paidTimestampEn = m.paidTimestampEn,
        paidTimestampAr = m.paidTimestampAr
      )
    }
    if (memberEntities.isNotEmpty()) {
      memberDao.insertMembers(memberEntities)

      // Create authentic receipts for members that are imported as PAID
      val now = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date())
      val nowAr = SimpleDateFormat("dd MMM yyyy، hh:mm a", Locale("ar")).format(Date())
      memberEntities.filter { it.isPaid }.forEach { paidMember ->
        val randomCode = (10000..99999).random()
        val datePart = SimpleDateFormat("yyMMdd", Locale.US).format(Date())
          val chosenMethod = normalizePaymentType(paidMember.paidTimestampAr ?: paidMember.paidTimestampEn)
          val receipt = PaymentReceiptEntity(
            receiptId = "HSL-REC-$datePart-$randomCode",
            circleId = "circle_primary",
            memberId = paidMember.id,
            memberNameEn = paidMember.nameEn,
            memberNameAr = paidMember.nameAr,
            amount = paidMember.monthlyContribution,
            currencySymbol = circle.currencySymbol,
            roundNumber = circle.currentCycleMonth,
            paymentMethod = chosenMethod,
            formattedDateEn = paidMember.paidTimestampEn ?: now,
            formattedDateAr = paidMember.paidTimestampAr ?: nowAr
          )
        receiptDao.insertReceipt(receipt)
      }
    }
  }

  suspend fun advanceCycleMonth() {
    val circle = circleDao.getCircleSync() ?: return
    val nextMonth = if (circle.currentCycleMonth < circle.totalMonths) circle.currentCycleMonth + 1 else 1
    circleDao.updateCurrentMonth(circle.id, nextMonth)
  }
}
