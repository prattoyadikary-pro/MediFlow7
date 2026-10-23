package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AdherenceSummary
import com.example.data.model.DailyHydrationSummary
import com.example.data.model.DoseStatus
import com.example.data.model.FrequencyType
import com.example.data.model.MedicineCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches MediFlow`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MediFlow", appName)
    }

    @Test
    fun `adherence percentage calculation is accurate`() {
        val summary1 = AdherenceSummary(
            totalDue = 4,
            takenCount = 3,
            missedCount = 1,
            skippedCount = 0
        )
        assertEquals(75.0f, summary1.adherencePercentage, 0.01f)

        val summaryZero = AdherenceSummary(
            totalDue = 0,
            takenCount = 0,
            missedCount = 0,
            skippedCount = 0
        )
        assertEquals(100.0f, summaryZero.adherencePercentage, 0.01f)
    }

    @Test
    fun `daily hydration progress percentage calculation`() {
        val hydration = DailyHydrationSummary(
            dateEpochMs = System.currentTimeMillis(),
            totalAmountMl = 1500,
            goalMl = 2000,
            logsCount = 3
        )
        assertEquals(0.75f, hydration.percentage, 0.01f)

        val overHydrated = DailyHydrationSummary(
            dateEpochMs = System.currentTimeMillis(),
            totalAmountMl = 3000,
            goalMl = 2000,
            logsCount = 5
        )
        assertTrue(overHydrated.percentage >= 1.0f)
    }

    @Test
    fun `medicine category and frequency enum display names`() {
        assertEquals("Tablet", MedicineCategory.TABLET.displayName)
        assertEquals("Capsule", MedicineCategory.CAPSULE.displayName)
        assertEquals("Once a day", FrequencyType.ONCE_DAILY.displayName)
        assertEquals("Twice a day", FrequencyType.TWICE_DAILY.displayName)
    }
}
