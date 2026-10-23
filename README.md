# MediFlow — iOS Cupertino Liquid Glass Edition

**MediFlow** is a modern, privacy-focused application designed to help users take medications on time, maintain treatment schedules, and stay properly hydrated throughout the day. This edition features an authentic **iOS Cupertino Liquid Glass (Glassmorphism)** aesthetic with an Ocean Aqua and Deep Cyan palette, fluid animations, and robust local persistence.

---

## iOS Cupertino Liquid Glass Experience

### 1. Dynamic Island Interactive Pill
- Sits prominently at the top of the interface.
- **Compact Island State**: Glanceable badge displaying the upcoming dose name or daily hydration percentage with a pulsing cyan status dot.
- **Expanded Island State**: Expands smoothly via spring animations on tap into an iOS compact island view featuring dose details, a countdown badge, and instant **"Take Dose"** and **"Snooze"** action buttons.

### 2. Apple Health-Inspired Visual Architecture
- **Dual Activity & Adherence Rings**: Concentric glowing rings visualizing medication adherence (Cyan to Mint) and hydration progress (Aqua to Deep Blue) simultaneously.
- **Ultra-Thin Frosted Glass Cards (`IosGlassCard`)**: Grouped inset cards with delicate `0.5dp` specular borders, top edge light reflections, and layered ambient drop shadows.
- **Cupertino Modal Bottom Sheets**: Medication creation and editing flows rendered as native iOS modal sheets with grey top grab handles (`CupertinoDragHandle`), "Cancel / Done" navigation, and grouped inset sections.
- **iOS Translucent Tab Bar**: Floating frosted glass tab bar with SF-style icon treatments, active cyan indicator dots, and smooth spring feedback.
- **Cupertino Segmented Controls**: Smooth sliding segmented pickers with animated thumbs for category filtering and view toggling.

---

## Core Medication & Hydration Features

- **Precision Scheduling**: Once daily, twice daily, 3 times daily, custom times (e.g. 08:00, 14:00, 20:00), specific days of week, or interval-based (every N hours).
- **Medication Metadata**: Category (tablet, capsule, syrup, drops, injection, inhaler, other), strength (e.g. 500 mg), dosage amount and unit, meal instructions (before food, after food, with meals, no restriction), prescription notes, and pill inventory refill tracking.
- **Actionable Notifications**: Scheduled alarms via `AlarmManager.setExactAndAllowWhileIdle()` with notification action buttons (**"✓ Mark Taken"** and **"⏰ Snooze"**).
- **Boot Reconciliation**: Automatically re-registers scheduled alarms across device restarts (`BOOT_COMPLETED`).
- **Interactive Water Orb**: Animated sinusoidal liquid wave visualization reflecting real-time water intake.
- **100% Privacy & Local Storage**: Powered by Room SQLite with KSP and DataStore preferences. Zero cloud tracking, no accounts required, and one-tap JSON backup/restore.

---

## Building and Verification

- Build Debug APK:
  ```bash
  gradle assembleDebug
  ```
- Run Unit & Robolectric Tests:
  ```bash
  gradle :app:testDebugUnitTest
  ```
