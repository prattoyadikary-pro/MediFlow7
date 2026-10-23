package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.DoseDao
import com.example.data.local.dao.HydrationDao
import com.example.data.local.dao.MedicineDao
import com.example.data.local.entity.DoseInstanceEntity
import com.example.data.local.entity.HydrationLogEntity
import com.example.data.local.entity.HydrationProfileEntity
import com.example.data.local.entity.MedicineEntity
import com.example.data.local.entity.MedicineScheduleEntity

@Database(
    entities = [
        MedicineEntity::class,
        MedicineScheduleEntity::class,
        DoseInstanceEntity::class,
        HydrationLogEntity::class,
        HydrationProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun doseDao(): DoseDao
    abstract fun hydrationDao(): HydrationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mediflow_database"
                )
                .fallbackToDestructiveMigration(false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
