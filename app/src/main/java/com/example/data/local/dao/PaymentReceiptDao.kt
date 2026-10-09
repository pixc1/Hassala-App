package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PaymentReceiptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentReceiptDao {
  @Query("SELECT * FROM payment_receipts ORDER BY timestamp DESC")
  fun getReceipts(): Flow<List<PaymentReceiptEntity>>

  @Query("SELECT * FROM payment_receipts WHERE circleId = :circleId ORDER BY timestamp DESC")
  fun getReceiptsByCircle(circleId: String = "circle_primary"): Flow<List<PaymentReceiptEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReceipt(receipt: PaymentReceiptEntity)

  @Query("DELETE FROM payment_receipts WHERE receiptId = :receiptId")
  suspend fun deleteReceiptById(receiptId: String)

  @Query("DELETE FROM payment_receipts WHERE memberId = :memberId")
  suspend fun deleteReceiptsByMemberId(memberId: String)

  @Query("DELETE FROM payment_receipts WHERE memberId = :memberId AND roundNumber = :roundNumber")
  suspend fun deleteReceiptForMemberRound(memberId: String, roundNumber: Int)

  @Query("DELETE FROM payment_receipts")
  suspend fun deleteAllReceipts()

  @Query("SELECT COUNT(*) FROM payment_receipts")
  suspend fun getReceiptCount(): Int

  @Query("SELECT * FROM payment_receipts WHERE receiptId = :receiptId LIMIT 1")
  suspend fun getReceiptById(receiptId: String): PaymentReceiptEntity?

  @Query("UPDATE payment_receipts SET paymentMethod = :newMethod WHERE receiptId = :receiptId")
  suspend fun updateReceiptPaymentMethod(receiptId: String, newMethod: String)

  @Query("UPDATE payment_receipts SET paymentMethod = :newMethod WHERE memberId = :memberId AND roundNumber = :roundNumber")
  suspend fun updateReceiptMethodForMemberRound(memberId: String, roundNumber: Int, newMethod: String)
}
