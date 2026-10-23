package com.example.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.MediFlowPreferences
import com.example.data.model.MealInstruction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object NotificationHelper {

    const val CHANNEL_MEDICINE_ID = "mediflow_medicine_channel"
    const val CHANNEL_HYDRATION_ID = "mediflow_hydration_channel"

    const val ACTION_MARK_TAKEN = "com.example.mediflow.ACTION_MARK_TAKEN"
    const val ACTION_SNOOZE = "com.example.mediflow.ACTION_SNOOZE"

    const val EXTRA_DOSE_ID = "extra_dose_id"
    const val EXTRA_MEDICINE_ID = "extra_medicine_id"
    const val EXTRA_MEDICINE_NAME = "extra_medicine_name"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            // 1. Medicine Channel (High Importance, Sound & Vibration)
            val medicineChannel = NotificationChannel(
                CHANNEL_MEDICINE_ID,
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts for scheduled medications and doses"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            }

            // 2. Hydration Channel (Default Importance)
            val hydrationChannel = NotificationChannel(
                CHANNEL_HYDRATION_ID,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle reminders to drink water during the day"
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }

            notificationManager.createNotificationChannel(medicineChannel)
            notificationManager.createNotificationChannel(hydrationChannel)
        }
    }

    fun showMedicineNotification(
        context: Context,
        doseId: Long,
        medicineId: Long,
        medicineName: String,
        doseInfo: String,
        mealInstruction: MealInstruction
    ) {
        val prefs = MediFlowPreferences(context)
        val hideName = runBlocking { prefs.hideNamesOnLockScreen.first() }

        val contentTitle = if (hideName) "Time for your scheduled dose" else "Time for $medicineName"
        val instructionText = if (mealInstruction != MealInstruction.NO_INSTRUCTION) {
            " (${mealInstruction.displayName})"
        } else ""
        val contentText = "$doseInfo$instructionText"

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "home")
            putExtra("highlight_dose_id", doseId)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            doseId.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Mark as Taken" action
        val takenIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MARK_TAKEN
            putExtra(EXTRA_DOSE_ID, doseId)
            putExtra(EXTRA_MEDICINE_ID, medicineId)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            (doseId * 10 + 1).toInt(),
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Snooze" action
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_DOSE_ID, doseId)
            putExtra(EXTRA_MEDICINE_ID, medicineId)
            putExtra(EXTRA_MEDICINE_NAME, medicineName)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (doseId * 10 + 2).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MEDICINE_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$contentText\nStay consistent with your treatment for optimal health."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(if (hideName) NotificationCompat.VISIBILITY_SECRET else NotificationCompat.VISIBILITY_PRIVATE)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(0, "✓ Mark Taken", takenPendingIntent)
            .addAction(0, "⏰ Snooze", snoozePendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(doseId.toInt(), notification)
        } catch (e: SecurityException) {
            // Notification permission might not be granted yet
        }
    }

    fun dismissNotification(context: Context, notificationId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }
}
