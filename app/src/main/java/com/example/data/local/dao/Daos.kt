package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.DoseInstanceEntity
import com.example.data.local.entity.HydrationLogEntity
import com.example.data.local.entity.HydrationProfileEntity
import com.example.data.local.entity.MedicineEntity
import com.example.data.local.entity.MedicineScheduleEntity
import com.example.data.model.DoseStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines ORDER BY name ASC")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    fun getMedicineById(id: Long): Flow<MedicineEntity?>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineByIdSync(id: Long): MedicineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity): Long

    @Update
    suspend fun updateMedicine(medicine: MedicineEntity)

    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Long)

    @Query("UPDATE medicines SET remainingQuantity = :newQuantity WHERE id = :id")
    suspend fun updateRemainingQuantity(id: Long, newQuantity: Int)

    @Query("SELECT * FROM medicine_schedules WHERE medicineId = :medicineId LIMIT 1")
    fun getScheduleForMedicine(medicineId: Long): Flow<MedicineScheduleEntity?>

    @Query("SELECT * FROM medicine_schedules WHERE medicineId = :medicineId LIMIT 1")
    suspend fun getScheduleForMedicineSync(medicineId: Long): MedicineScheduleEntity?

    @Query("SELECT * FROM medicine_schedules")
    fun getAllSchedules(): Flow<List<MedicineScheduleEntity>>

    @Query("SELECT * FROM medicine_schedules")
    suspend fun getAllSchedulesSync(): List<MedicineScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: MedicineScheduleEntity): Long

    @Update
    suspend fun updateSchedule(schedule: MedicineScheduleEntity)

    @Query("DELETE FROM medicine_schedules WHERE medicineId = :medicineId")
    suspend fun deleteScheduleByMedicineId(medicineId: Long)
}

@Dao
interface DoseDao {
    @Query("SELECT * FROM dose_instances WHERE scheduledEpochMs >= :startEpochMs AND scheduledEpochMs < :endEpochMs ORDER BY scheduledEpochMs ASC")
    fun getDosesForRange(startEpochMs: Long, endEpochMs: Long): Flow<List<DoseInstanceEntity>>

    @Query("SELECT * FROM dose_instances WHERE scheduledEpochMs >= :startEpochMs AND scheduledEpochMs < :endEpochMs ORDER BY scheduledEpochMs ASC")
    suspend fun getDosesForRangeSync(startEpochMs: Long, endEpochMs: Long): List<DoseInstanceEntity>

    @Query("SELECT * FROM dose_instances WHERE id = :id")
    fun getDoseById(id: Long): Flow<DoseInstanceEntity?>

    @Query("SELECT * FROM dose_instances WHERE id = :id")
    suspend fun getDoseByIdSync(id: Long): DoseInstanceEntity?

    @Query("SELECT * FROM dose_instances WHERE status = 'PENDING' AND scheduledEpochMs >= :fromEpochMs ORDER BY scheduledEpochMs ASC LIMIT 1")
    fun getNextPendingDose(fromEpochMs: Long): Flow<DoseInstanceEntity?>

    @Query("SELECT * FROM dose_instances WHERE status = 'PENDING' AND scheduledEpochMs >= :fromEpochMs ORDER BY scheduledEpochMs ASC LIMIT 1")
    suspend fun getNextPendingDoseSync(fromEpochMs: Long): DoseInstanceEntity?

    @Query("SELECT * FROM dose_instances WHERE status = 'PENDING' ORDER BY scheduledEpochMs ASC")
    suspend fun getAllPendingDosesSync(): List<DoseInstanceEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDose(dose: DoseInstanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDoses(doses: List<DoseInstanceEntity>): List<Long>

    @Update
    suspend fun updateDose(dose: DoseInstanceEntity)

    @Query("DELETE FROM dose_instances WHERE id = :id")
    suspend fun deleteDoseById(id: Long)

    @Query("DELETE FROM dose_instances WHERE medicineId = :medicineId AND scheduledEpochMs >= :afterEpochMs")
    suspend fun deleteFutureDosesForMedicine(medicineId: Long, afterEpochMs: Long)

    @Query("SELECT COUNT(*) FROM dose_instances WHERE status = :status AND scheduledEpochMs >= :startEpochMs AND scheduledEpochMs < :endEpochMs")
    fun getDoseCountByStatus(status: DoseStatus, startEpochMs: Long, endEpochMs: Long): Flow<Int>
}

@Dao
interface HydrationDao {
    @Query("SELECT * FROM hydration_logs WHERE timestampEpochMs >= :startEpochMs AND timestampEpochMs < :endEpochMs ORDER BY timestampEpochMs DESC")
    fun getLogsForRange(startEpochMs: Long, endEpochMs: Long): Flow<List<HydrationLogEntity>>

    @Query("SELECT * FROM hydration_logs WHERE timestampEpochMs >= :startEpochMs AND timestampEpochMs < :endEpochMs ORDER BY timestampEpochMs DESC")
    suspend fun getLogsForRangeSync(startEpochMs: Long, endEpochMs: Long): List<HydrationLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HydrationLogEntity): Long

    @Query("DELETE FROM hydration_logs WHERE id = :id")
    suspend fun deleteLog(id: Long)

    @Query("SELECT * FROM hydration_profiles WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<HydrationProfileEntity?>

    @Query("SELECT * FROM hydration_profiles WHERE id = 1 LIMIT 1")
    suspend fun getProfileSync(): HydrationProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: HydrationProfileEntity)
}
