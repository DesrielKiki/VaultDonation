package com.desrielkiki.vaultdonation.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.desrielkiki.vaultdonation.data.entity.DonationData
import com.desrielkiki.vaultdonation.data.entity.MemberData

@Database(entities = [MemberData::class, DonationData::class], version = 1, exportSchema = false)
abstract class DonationDatabase: RoomDatabase() {
    abstract fun donationDao(): DonationDao

    companion object {
        @Volatile
        private var INSTANCE: DonationDatabase? = null

        fun getDatabase(context: Context): DonationDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DonationDatabase::class.java,
                    "donation_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}