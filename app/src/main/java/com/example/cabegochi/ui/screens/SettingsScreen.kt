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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cabegochi.model.CabegochiCharacter
import com.example.cabegochi.model.CotorreoLevel
import com.example.cabegochi.model.CulturalMemoryCard
import com.example.cabegochi.model.UserProfile
import com.example.cabegochi.speech.DeviceVoice
import com.example.cabegochi.ui.theme.CabegochiAmberGold
import com.example.cabegochi.ui.theme.CabegochiNeonCyan
import com.example.cabegochi.ui.theme.CabegochiNeonPink
import com.example.cabegochi.ui.theme.CabegochiPurpleLight
import com.example.cabegochi.ui.theme.CabegochiPurplePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userProfile: UserProfile,
    culturalMemories: List<CulturalMemoryCard>,
    availableVoices: List<DeviceVoice>,
    onBack: () -> Unit,
    onUpdateCharacter: (CabegochiCharacter) -> Unit,
    onUpdateCotorreoLevel: (CotorreoLevel) -> Unit,
    onUpdateVoiceSettings: (voiceName: String?, pitch: Float, rate: Float) -> Unit,
    onTestVoice: (sampleText: String, pitch: Float, rate: Float, voiceName: String?) -> Unit,
    onToggleAutoSpeak: () -> Unit,
    onDeleteCulturalMemory: (Long) -> Unit,
    onClearChatHistory: () -> Unit,
    onPlaySampleMusic: () -> Unit,
    onRecordDonation: (Double) -> Unit
) {
    var selectedVoice by remember(userProfile.selectedVoiceName, availableVoices) {
        mutableStateOf(
            availableVoices.firstOrNull { it.name == userProfile.selectedVoiceName }
                ?: availableVoices.firstOrNull { it.locale.language.startsWith("es") }
                ?: availableVoices.firstOrNull()
        )
    }
    var speechPitch by remember(userProfile.speechPitch) { mutableFloatStateOf(userProfile.speechPitch) }
    var speechRate by remember(userProfile.speechRate) { mutableFloatStateOf(userProfile.speechRate) }
    var isVoiceDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración & Mi Cabegochi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al chat",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1C1E)
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1C1E))
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Card 1: Mi Cabegochi Evolution & Stats Dashboard
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4A4458)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        Color(0xFFD0BCFF),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = userProfile.selectedCharacter.avatarRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = userProfile.cabegochiName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFE6E1E5)
                                )
                                Text(
                                    text = "Compañero de ${userProfile.userNickname}",
                                    fontSize = 13.sp,
                                    color = Color(0xFFCAC4D0)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox(
                                label = "Mensajes",
                                value = "${userProfile.interactionCount}",
                                color = Color(0xFFD0BCFF),
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                label = "Memoria Viva",
                                value = "${culturalMemories.size}",
                                color = Color(0xFFEFB8C8),
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                label = "Nivel Humor",
                                value = "${userProfile.cotorreoLevel.level}/4",
                                color = Color(0xFFCCC2DC),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Card 2: Cotorreo Level Slider
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2F33)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mood,
                                contentDescription = null,
                                tint = Color(0xFFD0BCFF),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Intensidad de Cotorreo",
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 17.sp,
                                color = Color(0xFFE6E1E5)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = userProfile.cotorreoLevel.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFFD0BCFF)
                            )
                            Text(
                                text = "Nivel ${userProfile.cotorreoLevel.level}",
                                fontSize = 13.sp,
                                color = Color(0xFFCAC4D0)
                            )
                        }

                        Text(
                            text = userProfile.cotorreoLevel.description,
                            fontSize = 12.sp,
                            color = Color(0xFFCAC4D0),
                            lineHeight = 16.sp
                        )

                        Slider(
                            value = userProfile.cotorreoLevel.level.toFloat(),
                            onValueChange = { floatVal ->
                                val intLevel = floatVal.toInt()
                                onUpdateCotorreoLevel(CotorreoLevel.fromLevel(intLevel))
                            },
                            valueRange = 0f..4f,
                            steps = 3,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFD0BCFF),
                                activeTrackColor = Color(0xFF381E72)
                            ),
                            modifier = Modifier.testTag("cotorreo_level_slider")
                        )
                    }
                }
            }

            // Card 3: Switch Character
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2F33)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                text = "Personalidad Activa",
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 17.sp,
                                color = Color(0xFFE6E1E5)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CabegochiCharacter.entries.forEach { character ->
                                val isSelected = userProfile.selectedCharacter == character
                                val accentColor = Color(0xFFD0BCFF)
                                val borderColor by animateColorAsState(
                                    targetValue = if (isSelected) accentColor else Color.White.copy(alpha = 0.05f),
                                    label = "border_char"
                                )

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { onUpdateCharacter(character) }
                                        .testTag("select_character_${character.id}"),
                                    color = if (isSelected) Color(0xFF4A4458) else Color(0xFF1A1C1E),
                                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = character.avatarRes),
                                            contentDescription = character.displayName,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = character.displayName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isSelected) Color(0xFFD0BCFF) else Color(0xFFE6E1E5)
                                            )
                                            if (isSelected) {
                                                Text(
                                                    text = "Activo",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFFD0BCFF)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Card 4: Voice Customization
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
                                text = "Ajustes de Voz (TTS Android)",
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 17.sp,
                                color = Color(0xFFE6E1E5)
                            )
                        }

                        // Auto-speak toggle switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Leer respuestas en voz alta",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFE6E1E5)
                                )
                                Text(
                                    text = "Habla automáticamente al contestar cada mensaje",
                                    fontSize = 11.sp,
                                    color = Color(0xFFCAC4D0)
                                )
                            }
                            Switch(
                                checked = userProfile.autoSpeak,
                                onCheckedChange = { onToggleAutoSpeak() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFD0BCFF),
                                    checkedTrackColor = Color(0xFF381E72)
                                )
                            )
                        }

                        // Voice Selector
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { isVoiceDropdownExpanded = true },
                                color = Color(0xFF4A4458).copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedVoice?.displayLabel ?: "Voz estándar de Android (es-MX)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFE6E1E5)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color(0xFFE6E1E5)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = isVoiceDropdownExpanded,
                                onDismissRequest = { isVoiceDropdownExpanded = false }
                            ) {
                                availableVoices.take(15).forEach { voice ->
                                    DropdownMenuItem(
                                        text = { Text(voice.displayLabel) },
                                        onClick = {
                                            selectedVoice = voice
                                            isVoiceDropdownExpanded = false
                                            onUpdateVoiceSettings(voice.name, speechPitch, speechRate)
                                        }
                                    )
                                }
                            }
                        }

                        // Pitch Slider
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
                                onValueChange = {
                                    speechPitch = it
                                    onUpdateVoiceSettings(selectedVoice?.name, speechPitch, speechRate)
                                },
                                valueRange = 0.6f..1.5f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFD0BCFF),
                                    activeTrackColor = Color(0xFF381E72)
                                )
                            )
                        }

                        // Speed Slider
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
                                onValueChange = {
                                    speechRate = it
                                    onUpdateVoiceSettings(selectedVoice?.name, speechPitch, speechRate)
                                },
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
                                onTestVoice(
                                    "¡Qué onda ${userProfile.userNickname}! Esta es la voz configurada.",
                                    speechPitch,
                                    speechRate,
                                    selectedVoice?.name
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
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
                                text = "Probar voz actual",
                                color = Color(0xFFD0BCFF),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Card 5: Cultural Memories List
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFEFB8C8),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Memoria Cultural Aprendida (${culturalMemories.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 16.sp,
                        color = Color(0xFFE6E1E5)
                    )
                }
            }

            if (culturalMemories.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF2D2F33),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Text(
                            text = "Aún no hay anécdotas aprendidas. Sigue cotorreando para que Cabegochi recuerde tus frases y personajes.",
                            fontSize = 13.sp,
                            color = Color(0xFFCAC4D0),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(culturalMemories, key = { it.id }) { memory ->
                    CulturalMemoryItem(
                        memory = memory,
                        onDelete = { onDeleteCulturalMemory(memory.id) }
                    )
                }
            }

            // Card 6: Music Player + Reset / Clear History
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2F33)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFD0BCFF))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Reproductor local (música del dispositivo)", color = Color(0xFFE6E1E5), fontWeight = FontWeight.Bold)
                        }

                        // Simple controls: play sample file button triggers a callback
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { onPlaySampleMusic() }, shape = RoundedCornerShape(12.dp)) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Reproducir (usar ruta del dispositivo)")
                            }

                            OutlinedButton(onClick = { /* stop playback */ }, shape = RoundedCornerShape(12.dp)) {
                                Text(text = "Detener")
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            var showConfirm by remember { mutableStateOf(false) }
                            if (showConfirm) {
                                androidx.compose.material3.AlertDialog(
                                    onDismissRequest = { showConfirm = false },
                                    confirmButton = {
                                        androidx.compose.material3.TextButton(onClick = {
                                            // Record a small default donation and hide dialog
                                            onRecordDonation(1.0)
                                            showConfirm = false
                                        }) {
                                            Text("Confirmar")
                                        }
                                    },
                                    dismissButton = {
                                        androidx.compose.material3.TextButton(onClick = { showConfirm = false }) {
                                            Text("Cancelar")
                                        }
                                    },
                                    title = { Text("Donación") },
                                    text = { Text("Vas a ser dirigido al cobro externo. ¿Confirmas la donación? (registro interno oculto)") }
                                )
                            }

                            OutlinedButton(onClick = { showConfirm = true }, shape = RoundedCornerShape(12.dp)) {
                                Text(text = "Donar")
                            }

                            Button(
                                onClick = onClearChatHistory,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("clear_history_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEFB8C8).copy(alpha = 0.15f)
                                ),
                                border = BorderStroke(1.dp, Color(0xFFEFB8C8))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    tint = Color(0xFFEFB8C8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Borrar historial de chat",
                                    color = Color(0xFFEFB8C8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1A1C1E),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFFCAC4D0)
            )
        }
    }
}

@Composable
fun CulturalMemoryItem(
    memory: CulturalMemoryCard,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF2D2F33),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = memory.category.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD0BCFF)
                )
                Text(
                    text = memory.content,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE6E1E5),
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = "Mencionado ${memory.timesUsed} veces",
                    fontSize = 10.sp,
                    color = Color(0xFFCAC4D0)
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar recuerdo",
                    tint = Color(0xFFCAC4D0),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
