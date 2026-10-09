package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "circle_members")
data class MemberEntity(
  @PrimaryKey val id: String,
  val circleId: String = "circle_primary",
  val nameEn: String,
  val nameAr: String,
  val roleEn: String,
  val roleAr: String,
  val turnNumber: Int,
  val payoutMonthEn: String,
  val payoutMonthAr: String,
  val monthlyContribution: Long,
  val isPaid: Boolean,
  val isCurrentUser: Boolean = false,
  val avatarInitials: String,
  val avatarColorHex: Long,
  val phone: String,
  val paidTimestampEn: String? = null,
  val paidTimestampAr: String? = null
)
