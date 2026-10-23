# MediFlow — Design Showcase UI & Deep Midnight Dark Mode

Rebuild the MediFlow application interface to match the exact visual design system, layouts, components, and typography shown in the uploaded user showcase (`MediFlow_Health_App_Showcase_aec38cbc.png`), complete with Light Mode and an OLED-optimized **Deep Midnight Cyan Dark Mode**.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> Key design directions confirmed from Phase 1 interactive questions:
> - **Dark Mode**: Deep Midnight Cyan glass (`#040E14` / `#06161D`) for OLED displays with glowing cyan reflections and high-contrast typography.
> - **User Personalization**: Customizable user profile (name, email, avatar) in Profile/Settings, displayed in the home header ("Good Morning, [Name] ☀️").
> - **Medicine Details Experience**: Dedicated full-screen Medicine Details screen with large pill container, schedule breakdown, instructions, and Pause/Resume + Edit actions.

---

### 1. Overview & Visual Design System

The application will replicate the uploaded 10-screen showcase design:
1. **Splash Screen**:
   - Layered glass water drop emblem with soft glow, bold "MediFlow" branding, and tagline "Take your health, one step at a time" over a subtle atmospheric mist wave.
2. **Home Dashboard Screen**:
   - Header: Greeting ("Good Morning, Alex ☀️"), current date, notification bell with unread badge, and profile avatar.
   - Hero "Next Dose" Card: Capsule in glowing circle, medicine name, dosage, time, and prominent filled teal pill button "**✓ Mark as Taken**".
   - "Today's Progress" Card: Horizontal progress bar + pill badge ("2 of 4 doses • 50%").
   - "Hydration" Widget: Water droplet icon, intake counter ("1,200 ml / 2,000 ml"), "+ Add Water" button, and circular arc progress ring with percentage badge.
   - "Upcoming Reminders" Inset List: Grouped rows with colored dot indicators, scheduled times, medicine name, dose, and chevron `>`.
   - Translucent floating bottom navigation bar with active cyan pill indicators.
3. **Medicines List Screen**:
   - Large title "Medicines".
   - Cupertino search bar with magnifying glass icon.
   - Segmented filter chips: **[All]**, **[Active]**, **[Paused]**.
   - Medicine cards: 3D capsule icon in colored circle, Name, strength & type, time & frequency, and "Active" (green) or "Paused" (grey) badge.
   - Floating Action Button '+' in teal.
4. **Add / Edit Medicine Screen**:
   - Dedicated full screen with back arrow "< Add Medicine".
   - Large 3D Pill Preview Box in glass card with camera icon.
   - Form fields: Medicine Name, Dosage, Medicine Type dropdown, Frequency dropdown, Time picker, Start Date picker.
   - Prominent "Save Medicine" button.
5. **Medicine Details Screen**:
   - Header with "<" back and "⋮" menu.
   - Centered large pill container with "Active" badge.
   - Medicine Name and Dosage.
   - "Next Dose" card with bell icon.
   - Details rows: Schedule, Instructions (e.g. "After food"), Start Date, End Date.
   - Bottom actions: **[ Pause / Resume ]** outlined button and **[ Edit ]** filled button.
6. **Hydration Tracker Screen**:
   - Large title "Hydration".
   - Huge circular gauge: Liquid glass circle with water drop, "1,200 ml" bold, "of 2,000 ml", circular progress arc and "60%" pill badge.
   - 3 Quick Add Glass Cards: +150 ml (Cup), +250 ml (Glass), +500 ml (Bottle).
   - Action Button: "**💧 Log Water**".
   - Bottom Stat Cards: "Today's Intake: 1,200 ml", "Remaining: 800 ml".
7. **History Screen**:
   - Large title "History".
   - Date picker row: "< Tue, 29 Apr 2025 >".
   - Day of week strip: Mon, Tue (active circle pill), Wed, Thu, Fri, Sat, Sun.
   - "Medication Summary" Card: ✓ Taken, ✕ Missed, ↷ Skipped, ⏰ Late.
   - "Hydration Summary" Card: Water drop icon, Total Intake, 60% ring, "% Achieved".
8. **Reminder Settings Screen**:
   - Header "< Reminder Settings".
   - "Medicine Reminders" section with master toggle, Sound picker, Vibration toggle, and Snooze Duration.
   - "Hydration Reminders" section with master toggle, Sound picker, Vibration toggle, and Reminder Interval.
9. **Profile / Settings Screen**:
   - Header "< Profile".
   - User card: Avatar photo, "Alex Johnson", "alex@example.com".
   - List options: Personal Information, Reminder Settings, Appearance (with Light/Dark Mode toggle), Privacy & Security, Data & Export, About, and Log Out/Reset.

---

### 2. Dual-Theme Color Architecture

| Token | Light Mode (Showcase) | Deep Midnight Dark Mode |
|---|---|---|
| Background | Atmospheric Mint Mist (`#DFF2F1` / `#EAF5F4`) | Deep Midnight Abyss (`#040E14` / `#06161D`) |
| Surface / Cards | Frosted Glass (`#EDF7F6`, alpha 0.90) | Midnight Cyan Glass (`#0A1D26`, alpha 0.88) |
| Primary Brand | Deep Ocean Teal (`#006B76` / `#00838F`) | Vibrant Cyan (`#00E5FF` / `#26C6DA`) |
| Secondary Accent | Soft Seafoam (`#4FA89B`) | Aqua Foam (`#80DEEA`) |
| Active Pill Badge | Mint Emerald (`#34D399` / `#22C55E`) | Glowing Mint (`#10B981`) |
| Inactive / Paused | Soft Grey (`#94A3B8`) | Muted Slate (`#64748B`) |
| Text Primary | Deep Charcoal (`#0F172A`) | Crisp Snow White (`#FFFFFF`) |
| Text Secondary | Slate Cyan (`#475569`) | Soft Aqua (`#94A3B8` / `#80DEEA`) |

---

### 3. Component & Navigation Hierarchy

```
┌────────────────────────────────────────────────────────────────────────┐
│                     MediFlow Showcase Architecture                     │
└────────────────────────────────────────────────────────────────────────┘

 [ NavScreen State ]
   ├─ HOME: HomeScreen
   │    ├─ Header (Greeting, Date, Bell, Avatar)
   │    ├─ Hero Next Dose Card ("✓ Mark as Taken")
   │    ├─ Today's Progress Card
   │    ├─ Hydration Widget (Circular Gauge + "+ Add Water")
   │    └─ Upcoming Reminders List
   ├─ MEDICINES: MedicinesScreen
   │    ├─ Cupertino Search Bar
   │    ├─ Segmented Filter ([All], [Active], [Paused])
   │    ├─ Medicine Cards (Pill Graphic, Info, Status Badge)
   │    └─ Navigation to MedicineDetailsScreen or AddMedicineScreen
   ├─ HYDRATION: HydrationScreen
   │    ├─ Large Circular Liquid Gauge (Arc, Percentage Pill)
   │    ├─ 3 Quick Add Serving Cards (+150ml, +250ml, +500ml)
   │    ├─ "💧 Log Water" Action Button
   │    └─ Today's Intake & Remaining Stat Cards
   ├─ HISTORY: HistoryScreen
   │    ├─ Date Nav Strip (< Date > + Weekday Pills)
   │    ├─ Medication Summary Card (Taken, Missed, Skipped, Late)
   │    └─ Hydration Summary Card (Total Intake + % Achieved)
   └─ PROFILE / SETTINGS: ProfileScreen & ReminderSettingsScreen
        ├─ User Card (Avatar, Name, Email)
        ├─ Appearance (Interactive Light / Dark Mode Toggle)
        └─ Reminder Preferences (Sounds, Vibration, Intervals)
```

---

### Implementation Phases

1. **Phase 1: Dual-Theme Color & Glass Foundations**
   - Update `Color.kt` and `Theme.kt` with exact showcase mint-teal light palette and OLED Deep Midnight Cyan dark palette.
   - Refine `IosGlassCard` and `ShowcaseComponents.kt` with rounded pill buttons, circular badges, and glass specular borders.
2. **Phase 2: Home Dashboard & Header Rebuild**
   - Implement the exact layout: Greeting with avatar, Next Dose card with pill button, Progress card with horizontal bar + percentage badge, Hydration widget with circular gauge, and Upcoming Reminders list.
3. **Phase 3: Medicines Screen, Details Screen & Add Medicine Form**
   - Build `MedicineDetailsScreen.kt` with 3D pill illustration box, schedule details, and Pause/Edit actions.
   - Build `AddMedicineScreen.kt` (or full modal screen) matching screen 4.
   - Update `MedicinesScreen.kt` with the search bar, segmented filter, and medicine cards.
4. **Phase 4: Hydration Screen & History Screen Rebuild**
   - Replicate screen 5: large circular liquid gauge with arc, 3 quick-add cards (+150ml, +250ml, +500ml), "Log Water" button, and intake/remaining cards.
   - Replicate screen 6: date selector, day-of-week strip, and Medication & Hydration summary cards.
5. **Phase 5: Profile Screen & Reminder Settings Screen**
   - Build `ProfileScreen.kt` (screen 9) with user avatar, name, email, and navigation rows.
   - Add Appearance toggle for Light / Dark Mode with instant preview.
   - Build `ReminderSettingsScreen.kt` (screen 8) with master toggles and interval pickers.
6. **Phase 6: Verification & Compilation**
   - Verify compilation with `compile_applet` and execute unit tests.
