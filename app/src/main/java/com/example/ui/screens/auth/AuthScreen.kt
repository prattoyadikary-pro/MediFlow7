package com.example.ui.screens.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.example.ui.theme.LocalMediFlowDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.theme.ShowcaseTealPrimary
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.MediFlowViewModel

fun signOutUser(
    onSignOutComplete: () -> Unit
) {
    try {
        FirebaseAuth.getInstance().signOut()
    } catch (e: Exception) {
        Log.e("Auth", "Error during sign out", e)
    } finally {
        onSignOutComplete()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    viewModel: MediFlowViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var usernameInput by remember(userProfile.displayName) {
        mutableStateOf(userProfile.displayName.ifBlank { "Alex Johnson" })
    }
    var ageInput by remember(userProfile.age) {
        mutableStateOf(userProfile.age.ifBlank { "28" })
    }
    var weightInput by remember(userProfile.weightKg) {
        mutableStateOf(userProfile.weightKg.ifBlank { "70 kg" })
    }
    var genderInput by remember(userProfile.gender) {
        mutableStateOf(userProfile.gender.ifBlank { "Male" })
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val genderOptions = listOf("Male", "Female", "Non-binary", "Prefer not to say")

    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val waveShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_shift"
    )

    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF061820),
                Color(0xFF0A2732),
                Color(0xFF05151C)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFCDE9F0),
                Color(0xFFE7F6F7),
                Color(0xFFD6EEF0),
                Color(0xFF88C2CA)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Serene mountain and lake water reflection canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val farMountainPath = Path().apply {
                moveTo(0f, h * 0.63f)
                cubicTo(w * 0.22f, h * 0.55f, w * 0.48f, h * 0.65f, w * 0.76f, h * 0.56f)
                cubicTo(w * 0.88f, h * 0.52f, w * 0.95f, h * 0.57f, w, h * 0.54f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = farMountainPath,
                brush = Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(Color(0x440E4B56), Color(0x77072B33))
                    } else {
                        listOf(Color(0x6698C7D0), Color(0x996BA6B0))
                    }
                )
            )

            val midMountainPath = Path().apply {
                moveTo(0f, h * 0.70f)
                cubicTo(w * 0.30f, h * 0.64f, w * 0.62f, h * 0.73f, w, h * 0.65f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = midMountainPath,
                brush = Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(Color(0x55125B67), Color(0x8806232B))
                    } else {
                        listOf(Color(0x887EB4BD), Color(0xBB4B8D96))
                    }
                )
            )

            val wave1Path = Path().apply {
                val yOffset = (waveShift - 0.5f) * 14f
                moveTo(0f, h * 0.79f + yOffset)
                cubicTo(
                    w * 0.35f, h * 0.74f - yOffset,
                    w * 0.65f, h * 0.84f + yOffset,
                    w, h * 0.77f - yOffset
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = wave1Path,
                brush = Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(Color(0x77167985), Color(0xCC041B22))
                    } else {
                        listOf(Color(0xBB6CB2BA), Color(0xEE1F6E75))
                    }
                )
            )

            val wave2Path = Path().apply {
                val yOffset = (0.5f - waveShift) * 10f
                moveTo(0f, h * 0.86f + yOffset)
                cubicTo(
                    w * 0.3f, h * 0.81f + yOffset,
                    w * 0.7f, h * 0.90f - yOffset,
                    w, h * 0.84f + yOffset
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = wave2Path,
                brush = Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(Color(0x5527B9C0), Color(0xEE031217))
                    } else {
                        listOf(Color(0x99B6E8EA), Color(0xEE0D5860))
                    }
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Centerpiece Logo & Branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MediFlowDropletLeafLogo(size = 96.dp)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "MediFlow",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0D3B44),
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Set up your profile to start tracking your health",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFFBFEBEF) else Color(0xFF426770),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Frosted Liquid Glass Card for User Profile Form (Username, Age, Weight, Gender)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = Color(0x33075B63),
                        spotColor = Color(0x33087F82)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        if (isDark) Color(0xD90A222B) else Color(0xD9F8FCFC)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isDark) Color(0x5527B9C0) else Color(0xCCFFFFFF),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Personal Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0D3B44)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Username / Display Name Input
                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = {
                        usernameInput = it
                        errorMessage = null
                    },
                    label = { Text("Username / Full Name") },
                    placeholder = { Text("e.g. Alex Johnson") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ShowcaseTealPrimary,
                        unfocusedBorderColor = if (isDark) Color(0x4427B9C0) else Color(0x664B8D96)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_auth_username")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Age and Weight Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = ageInput,
                        onValueChange = { ageInput = it },
                        label = { Text("Age") },
                        placeholder = { Text("28") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ShowcaseTealPrimary,
                            unfocusedBorderColor = if (isDark) Color(0x4427B9C0) else Color(0x664B8D96)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_auth_age")
                    )

                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight") },
                        placeholder = { Text("70 kg") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ShowcaseTealPrimary,
                            unfocusedBorderColor = if (isDark) Color(0x4427B9C0) else Color(0x664B8D96)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_auth_weight")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Gender Section
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Gender",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFD0F0F3) else Color(0xFF1D4D56)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        genderOptions.forEach { option ->
                            val isSelected = genderInput.equals(option, ignoreCase = true)
                            Surface(
                                color = if (isSelected) ShowcaseTealPrimary else if (isDark) Color(0x3327B9C0) else Color(0x336BA6B0),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .clickable { genderInput = option }
                                    .testTag("gender_chip_$option")
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else if (isDark) Color(0xFFD0F0F3) else Color(0xFF1D4D56),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage!!,
                        fontSize = 12.sp,
                        color = Color(0xFFDF6666),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val name = usernameInput.trim()
                        if (name.isBlank()) {
                            errorMessage = "Please enter your username / full name"
                        } else {
                            viewModel.saveUserProfile(
                                displayName = name,
                                age = ageInput.trim(),
                                weightKg = weightInput.trim(),
                                gender = genderInput.trim()
                            )
                            onAuthSuccess()
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShowcaseTealPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_save_profile_start")
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * 3D Glossy Water Droplet with inner organic leaf matching Showcase Screen 1.
 */
@Composable
fun MediFlowDropletLeafLogo(
    size: Dp = 104.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Outer water droplet path
        val dropPath = Path().apply {
            moveTo(w * 0.5f, h * 0.06f)
            cubicTo(
                w * 0.86f, h * 0.45f,
                w * 0.90f, h * 0.76f,
                w * 0.5f, h * 0.94f
            )
            cubicTo(
                w * 0.10f, h * 0.76f,
                w * 0.14f, h * 0.45f,
                w * 0.5f, h * 0.06f
            )
            close()
        }

        drawPath(
            path = dropPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF7CE8E6),
                    Color(0xFF1B9AAA),
                    Color(0xFF005B66)
                ),
                start = Offset(w * 0.25f, h * 0.1f),
                end = Offset(w * 0.8f, h * 0.9f)
            )
        )

        // Inner organic leaf curve (right petal)
        val leafPath = Path().apply {
            moveTo(w * 0.5f, h * 0.92f)
            cubicTo(
                w * 0.48f, h * 0.62f,
                w * 0.68f, h * 0.44f,
                w * 0.76f, h * 0.52f
            )
            cubicTo(
                w * 0.82f, h * 0.68f,
                w * 0.68f, h * 0.88f,
                w * 0.5f, h * 0.92f
            )
            close()
        }

        drawPath(
            path = leafPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFE0FBF8),
                    Color(0xFF56C5B6),
                    Color(0xFF0F767E)
                ),
                start = Offset(w * 0.5f, h * 0.45f),
                end = Offset(w * 0.75f, h * 0.9f)
            )
        )

        // Left specular highlight
        val highlightPath = Path().apply {
            moveTo(w * 0.45f, h * 0.18f)
            cubicTo(
                w * 0.26f, h * 0.46f,
                w * 0.24f, h * 0.68f,
                w * 0.36f, h * 0.80f
            )
            cubicTo(
                w * 0.29f, h * 0.64f,
                w * 0.33f, h * 0.44f,
                w * 0.45f, h * 0.18f
            )
            close()
        }
        drawPath(
            path = highlightPath,
            color = Color.White.copy(alpha = 0.65f)
        )
    }
}
