package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalMediFlowDarkTheme = compositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = ShowcaseMidnightPrimary,
    onPrimary = ShowcaseMidnightOnPrimary,
    primaryContainer = ShowcaseMidnightContainer,
    onPrimaryContainer = ShowcaseMidnightOnContainer,
    secondary = ShowcaseMidnightSecondary,
    onSecondary = ShowcaseMidnightOnSecondary,
    background = ShowcaseBackgroundDark,
    surface = ShowcaseSurfaceDark,
    onBackground = ShowcaseOnSurfaceDark,
    onSurface = ShowcaseOnSurfaceDark,
    outline = ShowcaseBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = ShowcaseTealPrimary,
    onPrimary = ShowcaseTealOnPrimary,
    primaryContainer = ShowcaseTealContainer,
    onPrimaryContainer = ShowcaseTealOnContainer,
    secondary = ShowcaseMintSecondary,
    onSecondary = ShowcaseMintOnSecondary,
    background = ShowcaseBackgroundLight,
    surface = ShowcaseSurfaceLight,
    onBackground = ShowcaseOnSurfaceLight,
    onSurface = ShowcaseOnSurfaceLight,
    outline = ShowcaseBorderLight
)

@Composable
fun MediFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalMediFlowDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
