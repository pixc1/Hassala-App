package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CircleDao
import com.example.data.local.dao.MemberDao
import com.example.data.local.dao.PaymentReceiptDao
import com.example.data.local.entity.CircleEntity
import com.example.data.local.entity.MemberEntity
import com.example.data.local.entity.PaymentReceiptEntity

@Database(
  entities = [
    CircleEntity::class,
    MemberEntity::class,
    PaymentReceiptEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class HassalaDatabase : RoomDatabase() {
  abstract fun circleDao(): CircleDao
  abstract fun memberDao(): MemberDao
  abstract fun paymentReceiptDao(): PaymentReceiptDao

  companion object {
    @Volatile
    private var INSTANCE: HassalaDatabase? = null

    fun getDatabase(context: Context): HassalaDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          HassalaDatabase::class.java,
          "hassala_rosca_database.db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
