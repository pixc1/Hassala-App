package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_receipts")
data class PaymentReceiptEntity(
  @PrimaryKey val receiptId: String,
  val circleId: String = "circle_primary",
  val memberId: String,
  val memberNameEn: String,
  val memberNameAr: String,
  val amount: Long,
  val currencySymbol: String,
  val roundNumber: Int,
  val paymentMethod: String,
  val timestamp: Long = System.currentTimeMillis(),
  val formattedDateEn: String,
  val formattedDateAr: String
)
