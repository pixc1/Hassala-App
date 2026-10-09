package com.example.model

enum class MemberPaymentStatus {
  PAID,
  PENDING
}

data class CircleMember(
  val id: String,
  val nameEn: String,
  val nameAr: String,
  val roleEn: String,
  val roleAr: String,
  val turnNumber: Int,
  val payoutMonthEn: String,
  val payoutMonthAr: String,
  val monthlyContribution: Long,
  val status: MemberPaymentStatus,
  val isCurrentUser: Boolean = false,
  val avatarInitials: String,
  val avatarColorHex: Long,
  val phone: String,
  val paidTimestampEn: String? = null,
  val paidTimestampAr: String? = null
)

data class MoneyCircle(
  val id: String,
  val titleEn: String,
  val titleAr: String,
  val categoryEn: String,
  val categoryAr: String,
  val totalPoolAmount: Long,
  val monthlyContribution: Long,
  val currencySymbol: String,
  val totalMonths: Int,
  val currentCycleMonth: Int,
  val nextPayoutDateEn: String,
  val nextPayoutDateAr: String,
  val daysRemaining: Int,
  val userTurn: Int,
  val escrowGuaranteeId: String,
  val shariaCertified: Boolean = true
)

data class PaymentReceipt(
  val receiptId: String,
  val memberId: String,
  val memberNameEn: String,
  val memberNameAr: String,
  val amount: Long,
  val currencySymbol: String,
  val roundNumber: Int,
  val paymentMethod: String,
  val formattedDateEn: String,
  val formattedDateAr: String
) {
  val paymentType: PaymentType
    get() = PaymentType.fromString(paymentMethod)
}

enum class PaymentType(
  val code: String,
  val labelAr: String,
  val labelEn: String,
  val iconEmoji: String,
  val badgeDescriptionAr: String,
  val badgeDescriptionEn: String
) {
  CASH(
    code = "CASH",
    labelAr = "كاش",
    labelEn = "Cash",
    iconEmoji = "💵",
    badgeDescriptionAr = "تسليم نقدي مباشر لمدير الجمعية",
    badgeDescriptionEn = "Direct cash handover to manager"
  ),
  CLIQ(
    code = "CLIQ",
    labelAr = "كليك",
    labelEn = "CliQ",
    iconEmoji = "⚡",
    badgeDescriptionAr = "تحويل فوري عبر خدمة كليك",
    badgeDescriptionEn = "Instant transfer via CliQ"
  );

  companion object {
    fun fromString(str: String?): PaymentType {
      if (str == null) return CASH
      val lower = str.lowercase()
      return if (lower.contains("cliq") || lower.contains("كليك")) CLIQ else CASH
    }
  }
}

enum class AppLanguage {
  ENGLISH,
  ARABIC
}

enum class ActiveTab {
  DASHBOARD,
  MEMBERS,
  TIMELINE,
  SETTINGS
}

enum class MemberFilter {
  ALL,
  PAID,
  PENDING
}
