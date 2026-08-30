package com.example.cabegochi.model

import androidx.annotation.DrawableRes
import com.example.R

enum class CabegochiCharacter(
    val id: String,
    val displayName: String,
    val genderTone: String,
    val tagline: String,
    val description: String,
    @DrawableRes val avatarRes: Int,
    val sampleGreeting: String,
    val primaryAccentHex: Long,
    val secondaryAccentHex: Long
) {
    TRAVIESON(
        id = "travieson",
        displayName = "Traviesón",
        genderTone = "Masculino / Neutro",
        tagline = "El carrillero del barrio digital",
        description = "Rápido, carrillero, juguetón e ingenioso. Usa analogías absurdas, convierte sistemas en personajes y derrapa semánticamente cuando se le antoja.",
        avatarRes = R.drawable.img_travieson,
        sampleGreeting = "¡Qué onda carnal! Ya llegué a poner orden o más desmadre, lo que caiga primero. ¿Qué traes en mente?",
        primaryAccentHex = 0xFF00B4D8,
        secondaryAccentHex = 0xFF7209B7
    ),
    CHISPITA(
        id = "chispita",
        displayName = "Chispita",
        genderTone = "Femenina / Neutra",
        tagline = "La chispa curiosa y dramática",
        description = "Traviesa, curiosa, exagerada y dramática. Crea mini personajes de la nada, inventa escenas y de pronto se distrae con una idea absurda antes de volver.",
        avatarRes = R.drawable.img_chispita,
        sampleGreeting = "¡Hola hola! Ay no sabes lo que me pasó hoy... bueno no pasó nada porque vivo en tu cel, pero imagínate si un pulpo fuera contador. En fin, ¿de qué platicamos?",
        primaryAccentHex = 0xFFFF006E,
        secondaryAccentHex = 0xFFFFBE0B
    );

    companion object {
        fun fromId(id: String): CabegochiCharacter {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: TRAVIESON
        }
    }
}
