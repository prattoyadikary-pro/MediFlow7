package com.example.data.model

enum class MedicineCategory(val displayName: String) {
    TABLET("Tablet"),
    CAPSULE("Capsule"),
    SYRUP("Syrup"),
    DROPS("Drops"),
    INJECTION("Injection"),
    INHALER("Inhaler"),
    OTHER("Other")
}

enum class FrequencyType(val displayName: String) {
    ONCE_DAILY("Once a day"),
    TWICE_DAILY("Twice a day"),
    THREE_TIMES_DAILY("3 times a day"),
    CUSTOM_TIMES("Custom times"),
    SPECIFIC_DAYS("Selected days"),
    EVERY_N_HOURS("Every N hours")
}

enum class MealInstruction(val displayName: String) {
    NO_INSTRUCTION("No food restriction"),
    BEFORE_FOOD("Before food (Empty stomach)"),
    AFTER_FOOD("After food"),
    WITH_FOOD("With food / meals")
}

enum class DoseStatus {
    PENDING,
    TAKEN,
    SNOOZED,
    SKIPPED,
    MISSED
}

enum class BeverageType(val displayName: String, val iconName: String) {
    WATER("Pure Water", "water_drop"),
    ELECTROLYTE("Electrolyte", "bolt"),
    TEA("Herbal Tea", "emoji_food_beverage"),
    SPARKLING("Sparkling", "bubble_chart"),
    JUICE("Fresh Juice", "local_drink"),
    OTHER("Other", "local_cafe")
}

enum class UnitSystem(val displayName: String) {
    METRIC("Metric (ml, mg)"),
    IMPERIAL("Imperial (fl oz, oz)")
}
