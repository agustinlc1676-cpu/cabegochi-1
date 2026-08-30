package com.example.cabegochi.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cabegochi.model.CabegochiCharacter

@Composable
fun CabegochiAvatar(
    character: CabegochiCharacter,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")

    // Idle breathing float
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Speaking pulse
    val speakingScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 260, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speaking_pulse"
    )

    val currentScale = if (isSpeaking) speakingScale else breathingScale
    val accentColor = Color(character.primaryAccentHex)
    val secondaryColor = Color(character.secondaryAccentHex)

    Box(
        modifier = modifier
            .size(size)
            .scale(currentScale)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .testTag("cabegochi_avatar"),
        contentAlignment = Alignment.Center
    ) {
        // Glowing Aura Ring
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD0BCFF).copy(alpha = if (isSpeaking) 0.5f else 0.25f),
                            Color(0xFF381E72).copy(alpha = if (isSpeaking) 0.3f else 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Avatar Image Container
        Surface(
            modifier = Modifier
                .size(size * 0.85f)
                .clip(CircleShape)
                .border(
                    width = if (isSpeaking) 3.dp else 2.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            Color(0xFFD0BCFF),
                            Color(0xFF381E72),
                            Color(0xFFD0BCFF)
                        )
                    ),
                    shape = CircleShape
                ),
            color = Color(0xFF4A4458)
        ) {
            Image(
                painter = painterResource(id = character.avatarRes),
                contentDescription = character.displayName,
                modifier = Modifier.clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        // Speaking Badge Indicator
        if (isSpeaking) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-2).dp, y = (-2).dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFD0BCFF),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "🔊 Hablando",
                    color = Color(0xFF381E72),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
