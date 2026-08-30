package com.example.cabegochi.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cabegochi.model.CabegochiCharacter
import com.example.cabegochi.speech.DeviceVoice
import com.example.cabegochi.ui.theme.CabegochiAmberGold
import com.example.cabegochi.ui.theme.CabegochiNeonCyan
import com.example.cabegochi.ui.theme.CabegochiNeonPink
import com.example.cabegochi.ui.theme.CabegochiPurpleLight
import com.example.cabegochi.ui.theme.CabegochiPurplePrimary

@Composable
fun OnboardingScreen(
    availableVoices: List<DeviceVoice>,
    onComplete: (
        character: CabegochiCharacter,
        cabegochiName: String,
        userNickname: String,
        voiceName: String?,
        pitch: Float,
        rate: Float
    ) -> Unit,
    onTestVoice: (sampleText: String, pitch: Float, rate: Float, voiceName: String?) -> Unit
) {
    var selectedCharacter by remember { mutableStateOf(CabegochiCharacter.TRAVIESON) }
    var cabegochiName by remember { mutableStateOf("Traviesón") }
    var userNickname by remember { mutableStateOf("Papi") }

    var selectedVoice by remember {
        mutableStateOf(availableVoices.firstOrNull { it.locale.language.startsWith("es") } ?: availableVoices.firstOrNull())
    }
    var speechPitch by remember { mutableFloatStateOf(1.0f) }
    var speechRate by remember { mutableFloatStateOf(1.05f) }
    var isVoiceDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1C1E))
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 40.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF4A4458).copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "V0.1 • Edición Cotorreo",
                        color = Color(0xFFD0BCFF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Text(
                    text = "Escoge tu desastre",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFE6E1E5),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Tu compañero virtual travieso listo para cotorrear, inventar palabras y hacerte el día.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFCAC4D0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                )
            }
        }

        // Character Selector Cards
        item {
            Text(
                text = "1. Selecciona tu personalidad",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFE6E1E5),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CabegochiCharacter.entries.forEach { character ->
                    val isSelected = selectedCharacter == character
                    val accentColor = Color(0xFFD0BCFF)
                    val cardBorderColor by animateColorAsState(
                        targetValue = if (isSelected) accentColor else Color.White.copy(alpha = 0.05f),
                        label = "card_border"
                    )

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedCharacter = character
                                cabegochiName = character.displayName
                            }
                            .testTag("character_card_${character.id}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF4A4458) else Color(0xFF2D2F33)
                        ),
                        border = BorderStroke(if (isSelected) 2.dp else 1.dp, cardBorderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFFD0BCFF).copy(alpha = 0.35f), Color.Transparent)
                                        )
                                    )
                                    .border(2.dp, if (isSelected) Color(0xFFD0BCFF) else Color(0xFF4A4458), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = character.avatarRes),
                                    contentDescription = character.displayName,
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = character.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isSelected) Color(0xFFD0BCFF) else Color(0xFFE6E1E5)
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Seleccionado",
                                        tint = Color(0xFFD0BCFF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Text(
                                text = character.tagline,
                                fontSize = 11.sp,
                                color = Color(0xFFCAC4D0),
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bautizo Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2F33)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = Color(0xFFD0BCFF),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. Bautizo & Apodo",
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 18.sp,
                            color = Color(0xFFE6E1E5)
                        )
                    }

                    OutlinedTextField(
                        value = cabegochiName,
                        onValueChange = { cabegochiName = it },
                        label = { Text("Nombre de tu Cabegochi", color = Color(0xFFCAC4D0)) },
                        placeholder = { Text("Ej: Traviesón, Chispita, El Pingo...", color = Color.White.copy(alpha = 0.35f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cabegochi_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color(0xFFE6E1E5),
                            focusedBorderColor = Color(0xFFD0BCFF),
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFF4A4458).copy(alpha = 0.45f),
                            unfocusedContainerColor = Color(0xFF4A4458).copy(alpha = 0.35f)
                        )
                    )

                    OutlinedTextField(
                        value = userNickname,
                        onValueChange = { userNickname = it },
                        label = { Text("¿Cómo quieres que te diga a ti?", color = Color(0xFFCAC4D0)) },
                        placeholder = { Text("Ej: Papi, Jefe, Carnal, Mi Lic...", color = Color.White.copy(alpha = 0.35f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("user_nickname_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color(0xFFE6E1E5),
                            focusedBorderColor = Color(0xFFD0BCFF),
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFF4A4458).copy(alpha = 0.45f),
                            unfocusedContainerColor = Color(0xFF4A4458).copy(alpha = 0.35f)
                        )
                    )
                }
            }
        }

        // Device TTS Voice Selector & Customization
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2F33)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = Color(0xFFEFB8C8),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. Voz del teléfono (TTS)",
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 18.sp,
                            color = Color(0xFFE6E1E5)
                        )
                    }

                    Text(
                        text = "Detectamos las voces gratuitas instaladas en tu Android. Elige tu favorita:",
                        fontSize = 13.sp,
                        color = Color(0xFFCAC4D0)
                    )

                    // Voice Dropdown Box
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { isVoiceDropdownExpanded = true }
                                .testTag("voice_selector_dropdown"),
                            color = Color(0xFF4A4458).copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedVoice?.displayLabel ?: "Voz estándar de Android (es-MX)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFE6E1E5)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Abrir menú de voces",
                                    tint = Color(0xFFE6E1E5)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = isVoiceDropdownExpanded,
                            onDismissRequest = { isVoiceDropdownExpanded = false }
                        ) {
                            if (availableVoices.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Voz estándar del sistema (es-MX)") },
                                    onClick = { isVoiceDropdownExpanded = false }
                                )
                            } else {
                                availableVoices.take(15).forEach { voice ->
                                    DropdownMenuItem(
                                        text = { Text(voice.displayLabel) },
                                        onClick = {
                                            selectedVoice = voice
                                            isVoiceDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Pitch & Speed Sliders
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Tono / Pitch", fontSize = 13.sp, color = Color(0xFFE6E1E5))
                            Text(
                                text = String.format("%.2fx", speechPitch),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD0BCFF)
                            )
                        }
                        Slider(
                            value = speechPitch,
                            onValueChange = { speechPitch = it },
                            valueRange = 0.6f..1.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFD0BCFF),
                                activeTrackColor = Color(0xFF381E72)
                            )
                        )
                    }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Velocidad de habla", fontSize = 13.sp, color = Color(0xFFE6E1E5))
                            Text(
                                text = String.format("%.2fx", speechRate),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEFB8C8)
                            )
                        }
                        Slider(
                            value = speechRate,
                            onValueChange = { speechRate = it },
                            valueRange = 0.7f..1.6f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFEFB8C8),
                                activeTrackColor = Color(0xFF381E72)
                            )
                        )
                    }

                    // Test Voice Button
                    OutlinedButton(
                        onClick = {
                            val sample = if (selectedCharacter == CabegochiCharacter.TRAVIESON) {
                                "¡Qué onda $userNickname! Soy $cabegochiName y ya me ando cargando de pila."
                            } else {
                                "¡Hola $userNickname! Soy $cabegochiName, ay qué emoción conocerte."
                            }
                            onTestVoice(sample, speechPitch, speechRate, selectedVoice?.name)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_voice_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFD0BCFF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFFD0BCFF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Escuchar prueba de voz",
                            color = Color(0xFFD0BCFF),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Start Button
        item {
            Button(
                onClick = {
                    onComplete(
                        selectedCharacter,
                        cabegochiName,
                        userNickname,
                        selectedVoice?.name,
                        speechPitch,
                        speechRate
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("start_cotorreo_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD0BCFF)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = "¡Empezar a cotorrear! 🚀",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF381E72)
                )
            }
        }
    }
}
