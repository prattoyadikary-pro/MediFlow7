package com.example.data.model

import com.example.data.local.entity.DoseInstanceEntity
import com.example.data.local.entity.MedicineEntity
import com.example.data.local.entity.MedicineScheduleEntity

data class MedicineWithSchedule(
    val medicine: MedicineEntity,
    val schedule: MedicineScheduleEntity?,
    val nextPendingDose: DoseInstanceEntity? = null,
    val isRefillNeeded: Boolean = medicine.remainingQuantity <= medicine.refillThreshold
)

data class DoseItem(
    val instance: DoseInstanceEntity,
    val medicineName: String,
    val medicineCategory: MedicineCategory,
    val strength: String,
    val doseAmount: Double,
    val doseUnit: String,
    val mealInstruction: MealInstruction,
    val colorHex: String,
    val remainingQuantity: Int,
    val isRefillNeeded: Boolean
)

data class DailyHydrationSummary(
    val dateEpochMs: Long,
    val totalAmountMl: Int,
    val goalMl: Int,
    val percentage: Float = if (goalMl > 0) (totalAmountMl.toFloat() / goalMl.toFloat()).coerceAtMost(2.0f) else 0f,
    val logsCount: Int
)

data class AdherenceSummary(
    val totalDue: Int,
    val takenCount: Int,
    val missedCount: Int,
    val skippedCount: Int,
    val adherencePercentage: Float = if (totalDue > 0) (takenCount.toFloat() / totalDue.toFloat() * 100f).coerceIn(0f, 100f) else 100f
)
