package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CircleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CircleDao {
  @Query("SELECT * FROM money_circles LIMIT 1")
  fun getCircle(): Flow<CircleEntity?>

  @Query("SELECT * FROM money_circles LIMIT 1")
  suspend fun getCircleSync(): CircleEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCircle(circle: CircleEntity)

  @Update
  suspend fun updateCircle(circle: CircleEntity)

  @Query("UPDATE money_circles SET currentCycleMonth = :month")
  suspend fun updateCurrentMonth(month: Int)

  @Query("UPDATE money_circles SET currentCycleMonth = :month WHERE id = :id")
  suspend fun updateCurrentMonth(id: String, month: Int)
}
