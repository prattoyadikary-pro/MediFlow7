package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.ui.theme.LocalMediFlowDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MediFlowBorderSubtle
import com.example.ui.theme.MediFlowTealPrimary
import com.example.ui.theme.MediFlowTextSecondary
import com.example.ui.theme.OceanCyanDarkPrimary

enum class NavScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    MEDICINES("Medicines", Icons.Filled.Medication, Icons.Outlined.Medication, "nav_medicines"),
    HYDRATION("Hydration", Icons.Filled.WaterDrop, Icons.Outlined.WaterDrop, "nav_hydration"),
    HISTORY("History", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "nav_history")
}

@Composable
fun GlassBottomNavBar(
    currentScreen: NavScreen,
    onScreenSelected: (NavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val navBackground = if (isDark) Color(0xEB0A2028) else Color(0xF0F9FDFD)

    val specularBorder = Brush.horizontalGradient(
        colors = if (isDark) listOf(
            Color(0x6627B9C0),
            Color(0x26087F82),
            Color(0x4027B9C0)
        ) else listOf(
            Color.White,
            MediFlowBorderSubtle,
            Color.White
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(30.dp),
                ambientColor = if (isDark) Color(0x66000000) else Color(0x1F075B63),
                spotColor = if (isDark) Color(0x4027B9C0) else Color(0x26087F82)
            )
            .clip(RoundedCornerShape(30.dp))
            .background(navBackground)
            .border(1.dp, specularBorder, RoundedCornerShape(30.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavScreen.entries.forEach { screen ->
                val isSelected = screen == currentScreen
                val primaryColor = if (isDark) OceanCyanDarkPrimary else MediFlowTealPrimary
                val unselectedColor = if (isDark) Color(0xFF7FA3AD) else MediFlowTextSecondary
                val targetColor = if (isSelected) primaryColor else unselectedColor

                val animatedColor by animateColorAsState(
                    targetValue = targetColor,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "tab_color"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            color = if (isSelected) {
                                if (isDark) Color(0x3327B9C0) else Color(0xFFDDF2F2)
                            } else {
                                Color.Transparent
                            },
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onScreenSelected(screen) }
                        )
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                        .testTag(screen.testTag)
                ) {
                    Icon(
                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.title,
                        tint = animatedColor,
                        modifier = Modifier.size(23.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = screen.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = animatedColor
                    )
                }
            }
        }
    }
}
