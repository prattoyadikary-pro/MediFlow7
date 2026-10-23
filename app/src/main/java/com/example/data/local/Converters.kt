package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.BeverageType
import com.example.data.model.DoseStatus
import com.example.data.model.FrequencyType
import com.example.data.model.MealInstruction
import com.example.data.model.MedicineCategory

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    @TypeConverter
    fun fromMedicineCategory(value: MedicineCategory): String = value.name

    @TypeConverter
    fun toMedicineCategory(value: String): MedicineCategory =
        try { MedicineCategory.valueOf(value) } catch (e: Exception) { MedicineCategory.TABLET }

    @TypeConverter
    fun fromFrequencyType(value: FrequencyType): String = value.name

    @TypeConverter
    fun toFrequencyType(value: String): FrequencyType =
        try { FrequencyType.valueOf(value) } catch (e: Exception) { FrequencyType.ONCE_DAILY }

    @TypeConverter
    fun fromMealInstruction(value: MealInstruction): String = value.name

    @TypeConverter
    fun toMealInstruction(value: String): MealInstruction =
        try { MealInstruction.valueOf(value) } catch (e: Exception) { MealInstruction.NO_INSTRUCTION }

    @TypeConverter
    fun fromDoseStatus(value: DoseStatus): String = value.name

    @TypeConverter
    fun toDoseStatus(value: String): DoseStatus =
        try { DoseStatus.valueOf(value) } catch (e: Exception) { DoseStatus.PENDING }

    @TypeConverter
    fun fromBeverageType(value: BeverageType): String = value.name

    @TypeConverter
    fun toBeverageType(value: String): BeverageType =
        try { BeverageType.valueOf(value) } catch (e: Exception) { BeverageType.WATER }
}
