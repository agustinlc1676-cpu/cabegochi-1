package com.example.cabegochi.model

enum class CotorreoLevel(
    val level: Int,
    val title: String,
    val description: String,
    val humorPromptSnippet: String
) {
    CASI_SERIO(
        level = 0,
        title = "Casi serio",
        description = "Respuestas directas y formales con apenas una pizca diminuta de chispa.",
        humorPromptSnippet = "Intensidad de humor casi nula (Nivel 0). Sé directo, claro y casi serio, con un tono ligeramente casual sin chistes ni derrapes."
    ),
    RELAJADO(
        level = 1,
        title = "Relajado",
        description = "Tono amigable y casual, humor sutil y buena vibra.",
        humorPromptSnippet = "Intensidad de humor relajada (Nivel 1). Tono casual y amigable, con comparaciones suaves y comentarios ligeros."
    ),
    MODERADO(
        level = 2,
        title = "Cotorreo moderado",
        description = "Chistes bien colocados, apodos ocasionales y comparaciones divertidas.",
        humorPromptSnippet = "Intensidad de humor moderada (Nivel 2). Usa analogías ocurrentes, algún apodo simpático y humor inteligente sin exagerar."
    ),
    CABEGOCHI_NORMAL(
        level = 3,
        title = "Cabegochi normal",
        description = "El balance dorado de desmadre, analogías absurdas y derrapes fonéticos.",
        humorPromptSnippet = "Intensidad de humor Cabegochi Normal (Nivel 3). Sé espontáneo, usa analogías absurdas, derrapes fonéticos ocasionales, personificaciones de objetos o sistemas, y vocabulario ingenioso."
    ),
    DESMADRE_ALTO(
        level = 4,
        title = "Desmadre alto",
        description = "Máxima picardía, exageración cósmica y ocurrencias alocadas.",
        humorPromptSnippet = "Intensidad de humor Desmadre Alto (Nivel 4). Máxima inventiva, derrapes semánticos creativos, comparaciones extremas y energía disparatada pero siempre comprensible."
    );

    companion object {
        fun fromLevel(level: Int): CotorreoLevel {
            return entries.firstOrNull { it.level == level } ?: CABEGOCHI_NORMAL
        }
    }
}
