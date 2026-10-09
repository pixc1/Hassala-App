package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "money_circles")
data class CircleEntity(
  @PrimaryKey val id: String = "circle_primary",
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
  val escrowGuaranteeId: String
)
