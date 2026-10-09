package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
  @Query("SELECT * FROM circle_members ORDER BY turnNumber ASC")
  fun getMembers(): Flow<List<MemberEntity>>

  @Query("SELECT * FROM circle_members ORDER BY turnNumber ASC")
  suspend fun getMembersSync(): List<MemberEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMember(member: MemberEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMembers(members: List<MemberEntity>)

  @Update
  suspend fun updateMember(member: MemberEntity)

  @Query("UPDATE circle_members SET isPaid = :isPaid, paidTimestampEn = :timeEn, paidTimestampAr = :timeAr WHERE id = :memberId")
  suspend fun updateMemberPaymentStatus(memberId: String, isPaid: Boolean, timeEn: String?, timeAr: String?)

  @Query("UPDATE circle_members SET monthlyContribution = :newContribution")
  suspend fun updateAllMembersContribution(newContribution: Long)

  @Delete
  suspend fun deleteMember(member: MemberEntity)

  @Query("DELETE FROM circle_members WHERE id = :memberId")
  suspend fun deleteMemberById(memberId: String)

  @Query("DELETE FROM circle_members")
  suspend fun deleteAllMembers()
}
