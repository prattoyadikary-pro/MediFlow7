package com.example.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

class MedicineAlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleDose(
        doseId: Long,
        medicineId: Long,
        medicineName: String,
        scheduledEpochMs: Long
    ) {
        if (scheduledEpochMs <= System.currentTimeMillis()) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(NotificationHelper.EXTRA_DOSE_ID, doseId)
            putExtra(NotificationHelper.EXTRA_MEDICINE_ID, medicineId)
            putExtra(NotificationHelper.EXTRA_MEDICINE_NAME, medicineName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            doseId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        scheduledEpochMs,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        scheduledEpochMs,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    scheduledEpochMs,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // In case exact alarm permission is revoked, fallback to standard
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                scheduledEpochMs,
                pendingIntent
            )
        }
    }

    fun cancelDose(doseId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            doseId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
