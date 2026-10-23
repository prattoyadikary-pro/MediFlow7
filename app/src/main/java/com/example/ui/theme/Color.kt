package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Exact MediFlow Showcase Light Palette Tokens
val MediFlowTealPrimary = Color(0xFF087F82)
val MediFlowDeepTeal = Color(0xFF075B63)
val MediFlowAquaAccent = Color(0xFF27B9C0)
val MediFlowPaleAquaBg = Color(0xFFEAF7F8)
val MediFlowSoftSkyBlue = Color(0xFFDCEFF5)
val MediFlowFrostedWhite = Color(0xFFF8FCFC)
val MediFlowTextMain = Color(0xFF173A46)
val MediFlowTextSecondary = Color(0xFF66838C)
val MediFlowBorderSubtle = Color(0xFFD3E9EB)
val MediFlowSuccessGreen = Color(0xFF36A982)
val MediFlowWarningAmber = Color(0xFFE9A23B)
val MediFlowErrorRed = Color(0xFFDF6666)

// Mapped Showcase Light Palette
val ShowcaseTealPrimary = MediFlowTealPrimary
val ShowcaseTealOnPrimary = Color(0xFFFFFFFF)
val ShowcaseTealContainer = Color(0xFFD1EFEF)
val ShowcaseTealOnContainer = MediFlowDeepTeal

val ShowcaseMintSecondary = MediFlowAquaAccent
val ShowcaseMintOnSecondary = Color(0xFFFFFFFF)
val ShowcaseMintContainer = Color(0xFFD9F3F4)

val ShowcaseBackgroundLight = MediFlowPaleAquaBg
val ShowcaseSurfaceLight = Color(0xF2F8FCFC)
val ShowcaseCardLight = Color(0xCCF8FCFC)
val ShowcaseOnSurfaceLight = MediFlowTextMain
val ShowcaseBorderLight = MediFlowBorderSubtle
val ShowcaseSubtleLight = MediFlowTextSecondary

// Showcase OLED Deep Midnight Cyan Palette (Dark Mode)
val ShowcaseMidnightPrimary = Color(0xFF27B9C0)
val ShowcaseMidnightOnPrimary = Color(0xFF00363D)
val ShowcaseMidnightContainer = Color(0xFF075B63)
val ShowcaseMidnightOnContainer = Color(0xFFBCEBEF)

val ShowcaseMidnightSecondary = Color(0xFF80DEEA)
val ShowcaseMidnightOnSecondary = Color(0xFF00363D)

val ShowcaseBackgroundDark = Color(0xFF06161C)
val ShowcaseSurfaceDark = Color(0xEB0B222B)
val ShowcaseCardDark = Color(0xD90E2933)
val ShowcaseOnSurfaceDark = Color(0xFFEAF7F8)
val ShowcaseBorderDark = Color(0x4D27B9C0)
val ShowcaseSubtleDark = Color(0xFF8FB0B9)

// Status & Accent Tokens
val ShowcaseActiveGreen = MediFlowSuccessGreen
val ShowcaseActiveGreenBg = Color(0xFFE0F5EC)
val ShowcasePausedGrey = MediFlowTextSecondary
val ShowcasePausedGreyBg = Color(0xFFE9F1F3)
val ShowcaseMissedRed = MediFlowErrorRed
val ShowcaseLateAmber = MediFlowWarningAmber
val ShowcaseWaterBlue = Color(0xFF1E96C8)
val ShowcaseWaterGlow = MediFlowAquaAccent

val StatusTaken = ShowcaseActiveGreen
val StatusSnoozed = ShowcaseLateAmber
val StatusSkipped = ShowcaseWaterBlue
val StatusPending = ShowcaseTealPrimary
val StatusMissed = ShowcaseMissedRed

val GlassHighlightWhite = Color(0xB3FFFFFF)
val GlassBorderDark = ShowcaseBorderDark
val GlassBorderLight = ShowcaseBorderLight
val GlassCardDark = ShowcaseCardDark
val GlassCardLight = ShowcaseCardLight
val GlassWaterWave = MediFlowAquaAccent
val GlassWaterDepth = MediFlowTealPrimary
val OceanCyanDarkPrimary = ShowcaseMidnightPrimary
val OceanCyanLightPrimary = ShowcaseTealPrimary
val BackgroundDark = ShowcaseBackgroundDark
val BackgroundLight = ShowcaseBackgroundLight
