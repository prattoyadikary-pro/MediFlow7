package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.BeverageType
import com.example.data.model.DoseStatus
import com.example.data.model.FrequencyType
import com.example.data.model.MealInstruction
import com.example.data.model.MedicineCategory

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: MedicineCategory = MedicineCategory.TABLET,
    val strength: String = "",
    val doseAmount: Double = 1.0,
    val doseUnit: String = "tablet",
    val mealInstruction: MealInstruction = MealInstruction.NO_INSTRUCTION,
    val notes: String = "",
    val prescriptionNotes: String = "",
    val remainingQuantity: Int = 30,
    val refillThreshold: Int = 5,
    val colorHex: String = "#00838F",
    val isActive: Boolean = true,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "medicine_schedules",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["medicineId"])]
)
data class MedicineScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineId: Long,
    val frequencyType: FrequencyType = FrequencyType.ONCE_DAILY,
    val scheduledTimes: List<String> = listOf("09:00"),
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    val intervalHours: Int = 8,
    val startDateEpochMs: Long = System.currentTimeMillis(),
    val endDateEpochMs: Long? = null,
    val reminderSound: String = "default",
    val vibrate: Boolean = true
)

@Entity(
    tableName = "dose_instances",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["medicineId"]),
        Index(value = ["scheduledEpochMs"]),
        Index(value = ["scheduledEpochMs", "medicineId"], unique = true)
    ]
)
data class DoseInstanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineId: Long,
    val scheduleId: Long,
    val scheduledEpochMs: Long,
    val status: DoseStatus = DoseStatus.PENDING,
    val actualActionEpochMs: Long? = null,
    val skipReason: String? = null,
    val notes: String? = null
)

@Entity(tableName = "hydration_logs")
data class HydrationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int,
    val beverageType: BeverageType = BeverageType.WATER,
    val timestampEpochMs: Long = System.currentTimeMillis(),
    val notes: String? = null
)

@Entity(tableName = "hydration_profiles")
data class HydrationProfileEntity(
    @PrimaryKey val id: Int = 1,
    val dailyGoalMl: Int = 2000,
    val wakeTimeMinutes: Int = 420, // 07:00
    val bedTimeMinutes: Int = 1380, // 23:00
    val reminderIntervalMinutes: Int = 90,
    val notificationsEnabled: Boolean = true,
    val age: Int? = null,
    val weightKg: Float? = null,
    val activityLevel: String = "MODERATE"
)
