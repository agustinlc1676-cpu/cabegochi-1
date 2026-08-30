package com.example.cabegochi.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.cabegochi.model.ChatMessage
import com.example.cabegochi.model.MessageRole
import com.example.cabegochi.model.UserProfile
import com.example.cabegochi.speech.SpeechInputState
import com.example.cabegochi.ui.components.CabegochiAvatar
import com.example.cabegochi.ui.theme.CabegochiAmberGold
import com.example.cabegochi.ui.theme.CabegochiNeonCyan
import com.example.cabegochi.ui.theme.CabegochiNeonPink
import com.example.cabegochi.ui.theme.CabegochiPurpleLight
import com.example.cabegochi.ui.theme.CabegochiPurplePrimary
import com.example.cabegochi.ui.theme.ChatBubbleCabegochi
import com.example.cabegochi.ui.theme.ChatBubbleUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userProfile: UserProfile,
    chatMessages: List<ChatMessage>,
    isOnline: Boolean,
    isSpeaking: Boolean,
    isGenerating: Boolean,
    speechInputState: SpeechInputState,
    onSendMessage: (String) -> Unit,
    onRepeatVoice: (String, Long?) -> Unit,
    onStopVoice: () -> Unit,
    onToggleAutoSpeak: () -> Unit,
    onStartVoiceInput: () -> Unit,
    onStopVoiceInput: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onStartVoiceInput()
            }
        }
    )

    // Auto-scroll to bottom on new messages or while generating
    LaunchedEffect(chatMessages.size, isGenerating) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val lastCabegochiMessage = chatMessages.lastOrNull { it.role == MessageRole.CABEGOCHI }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Connection pulse indicator
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) Color(0xFF22C55E) else Color(0xFFEF4444))
                                .testTag("connection_indicator")
                        )
                        Column {
                            Text(
                                text = userProfile.cabegochiName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.3).sp,
                                color = Color(0xFFE6E1E5)
                            )
                            Text(
                                text = "${userProfile.selectedCharacter.displayName} • ${userProfile.cotorreoLevel.title}",
                                fontSize = 11.sp,
                                color = Color(0xFFCAC4D0)
                            )
                        }
                    }
                },
                actions = {
                    // Mute / Unmute Button
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF4A4458).copy(alpha = 0.35f),
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .testTag("mute_toggle_button")
                    ) {
                        IconButton(onClick = onToggleAutoSpeak) {
                            Icon(
                                imageVector = if (userProfile.autoSpeak) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = if (userProfile.autoSpeak) "Silenciar voz automática" else "Activar voz automática",
                                tint = if (userProfile.autoSpeak) Color(0xFFD0BCFF) else Color(0xFFCAC4D0),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Settings Button
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF4A4458).copy(alpha = 0.35f),
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .testTag("settings_button")
                    ) {
                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configuración y Mi Cabegochi",
                                tint = Color(0xFFE6E1E5),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1C1E)
                )
            )
        },
        bottomBar = {
            // Chat Input Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF211F26),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    // Speech recognition listening badge
                    AnimatedVisibility(visible = speechInputState == SpeechInputState.LISTENING) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFB8C8).copy(alpha = 0.18f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFB8C8))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Escuchando tu voz... Habla ahora",
                                    color = Color(0xFFEFB8C8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Text Field
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            enabled = isOnline && !isGenerating,
                            placeholder = {
                                Text(
                                    if (!isOnline) "Sin conexión (Modo Dormido)..." else "Escríbele a ${userProfile.cabegochiName}...",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.35f)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_text_input"),
                            shape = RoundedCornerShape(28.dp),
                            singleLine = false,
                            maxLines = 4,
                            trailingIcon = {
                                val isListening = speechInputState == SpeechInputState.LISTENING
                                val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
                                val micScale by infiniteTransition.animateFloat(
                                    initialValue = 1f,
                                    targetValue = 1.2f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(durationMillis = 500),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "mic_scale"
                                )

                                IconButton(
                                    enabled = isOnline,
                                    onClick = {
                                        if (isListening) {
                                            onStopVoiceInput()
                                        } else {
                                            val hasPermission = ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.RECORD_AUDIO
                                            ) == PackageManager.PERMISSION_GRANTED
                                            if (hasPermission) {
                                                onStartVoiceInput()
                                            } else {
                                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .scale(if (isListening) micScale else 1f)
                                        .testTag("microphone_button")
                                ) {
                                    Icon(
                                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = "Entrada por voz",
                                        tint = if (!isOnline) Color.White.copy(alpha = 0.3f) else if (isListening) Color(0xFFEFB8C8) else Color(0xFFD0BCFF),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color(0xFFE6E1E5),
                                disabledTextColor = Color.White.copy(alpha = 0.35f),
                                focusedBorderColor = Color(0xFFD0BCFF),
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent,
                                focusedContainerColor = Color(0xFF4A4458).copy(alpha = 0.45f),
                                unfocusedContainerColor = Color(0xFF4A4458).copy(alpha = 0.35f),
                                disabledContainerColor = Color(0xFF2D2F33).copy(alpha = 0.4f)
                            )
                        )

                        // Send Button
                        val canSend = isOnline && inputText.isNotBlank() && !isGenerating
                        Surface(
                            shape = CircleShape,
                            color = if (canSend) Color(0xFFD0BCFF) else Color(0xFF4A4458).copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable(enabled = canSend) {
                                    onSendMessage(inputText)
                                    inputText = ""
                                }
                                .testTag("send_button"),
                            shadowElevation = if (canSend) 4.dp else 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Enviar mensaje",
                                    tint = if (canSend) Color(0xFF381E72) else Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF1A1C1E))
        ) {
            // Character Interactive Spotlight & Speech Bubble Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF4A4458),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            CabegochiAvatar(
                                character = userProfile.selectedCharacter,
                                isSpeaking = isSpeaking,
                                size = 58.dp,
                                onClick = {
                                    if (lastCabegochiMessage != null) {
                                        onRepeatVoice(lastCabegochiMessage.text, lastCabegochiMessage.id)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFD0BCFF),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Text(
                                        text = userProfile.selectedCharacter.displayName.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = Color(0xFF381E72),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                if (isSpeaking) {
                                    IconButton(
                                        onClick = onStopVoice,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stop,
                                            contentDescription = "Detener voz",
                                            tint = Color(0xFFEFB8C8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = lastCabegochiMessage?.text
                                    ?: userProfile.selectedCharacter.sampleGreeting,
                                fontSize = 14.sp,
                                color = Color(0xFFE6E1E5),
                                maxLines = 3,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    // Action Buttons inside Spotlight
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFD0BCFF),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    if (lastCabegochiMessage != null) {
                                        onRepeatVoice(lastCabegochiMessage.text, lastCabegochiMessage.id)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "🔊", fontSize = 13.sp)
                                Text(
                                    text = "Escuchar",
                                    color = Color(0xFF381E72),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (lastCabegochiMessage != null) {
                                        onRepeatVoice(lastCabegochiMessage.text, lastCabegochiMessage.id)
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🔄", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Quick Stats Row (Nivel de Cotorreo & Memoria V0.1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cotorreo Level Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF2D2F33),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "NIVEL DE COTORREO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${userProfile.cotorreoLevel.level}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD0BCFF)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(5) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (index <= userProfile.cotorreoLevel.level) Color(0xFFD0BCFF) else Color.White.copy(alpha = 0.2f)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                // Memoria Stats Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF2D2F33),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "MEMORIA V0.1",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${userProfile.interactionCount} Cotorreos",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE6E1E5)
                        )
                    }
                }
            }

            // Message History List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chatMessages, key = { it.id }) { message ->
                    ChatMessageItem(
                        message = message,
                        cabegochiName = userProfile.cabegochiName,
                        userNickname = userProfile.userNickname,
                        isSpeakingNow = isSpeaking,
                        onRepeatVoice = { onRepeatVoice(message.text, message.id) }
                    )
                }

                // AI Generating Indicator Bubble
                if (isGenerating) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 20.dp,
                                    topEnd = 20.dp,
                                    bottomEnd = 20.dp,
                                    bottomStart = 4.dp
                                ),
                                color = Color(0xFF4A4458),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color.White.copy(alpha = 0.08f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color(0xFFD0BCFF),
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "${userProfile.cabegochiName} está maquinando un remate...",
                                        fontSize = 12.sp,
                                        color = Color(0xFFD0BCFF),
                                        fontWeight = FontWeight.Medium
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

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    cabegochiName: String,
    userNickname: String,
    isSpeakingNow: Boolean,
    onRepeatVoice: () -> Unit
) {
    val isUser = message.role == MessageRole.USER
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_message_${message.id}"),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp
                ),
                color = if (isUser) Color(0xFF381E72) else Color(0xFF4A4458),
                border = if (!isUser) androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.08f)
                ) else null,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    // Header label
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUser) userNickname else cabegochiName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUser) Color(0xFFCCC2DC) else Color(0xFFD0BCFF)
                        )

                        if (!isUser) {
                            IconButton(
                                onClick = onRepeatVoice,
                                modifier = Modifier
                                    .size(20.dp)
                                    .testTag("repeat_voice_btn_${message.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Escuchar de nuevo",
                                    tint = Color(0xFFD0BCFF),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        color = Color(0xFFE6E1E5),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formattedTime,
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}
