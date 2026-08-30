package com.example.cabegochi.chuchuluco

import com.example.cabegochi.model.CabegochiCharacter
import com.example.cabegochi.model.CotorreoLevel
import com.example.cabegochi.model.CulturalMemoryCard
import com.example.cabegochi.model.UserProfile
import kotlin.random.Random

object ChuchulucoEngine {

    // --- FORMULAS & ASSETS ---

    val phoneticDerrapes = listOf(
        "vete a la ver...dulería por unos jitomates",
        "hijo de la chi...charra de las 3 de la tarde",
        "qué poca ma...dera de roble importado",
        "no estés chi...flando en la loma sin suéter",
        "ni de ped...imento aduanal te lo creía",
        "vaya tremendo cabr...onazo de suerte",
        "me lleva la tizn...ada cazuela de barro",
        "a la gran pu...chica fiesta de rancho",
        "no me estés jod...iendo con que se te olvidó",
        "qué desmad...rugada te metiste hoy"
    )

    val absurdAnalogies = listOf(
        "más enredado que audífonos con cable en la bolsa de un carpintero",
        "más ocupado que albañil en quincena de lluvia",
        "más lento que Windows 98 en una licuadora con mayonesa",
        "más apretado que tuerca de submarino ruso",
        "más sospechoso que perro en misa de gallo",
        "más perdido que Adán el día de las madres",
        "más caliente que boiler de vecindad a mediodía",
        "más frío que el saludo de tu ex en el súper"
    )

    val personifiedObjects = listOf(
        "el router que se fue de fiesta a Cuernavaca",
        "la memoria RAM que tiene amnesia selectiva",
        "la tecla Espacio que está estresada y pide sindicato",
        "el algoritmo de YouTube que se echó tres cafés con leche",
        "el microondas que se siente incomprendido",
        "la pila del cel que vive en agonía constante",
        "el refrigerador que juzga tus decisiones nocturnas"
    )

    val improvisedCharacters = listOf(
        "Don Filomeno el de la tiendita cuántica",
        "el Licenciado Cacahuate y Asociados",
        "Doña Pelos la del ciberespacio",
        "Protipirugolfo el consejero astral",
        "el Ingeniero Tlacuache del soporte técnico",
        "Chuy el soldador de nubes"
    )

    val inventedWords = listOf(
        "chipocludoide",
        "desmadrósfera",
        "chuchulucoso",
        "despanchurrado",
        "chiripiolazo",
        "calamburinesco",
        "garambullofilia",
        "trucutrú"
    )

    val chispitaTangents = listOf(
        "...espérame tantito, ¿un pulpo compra ocho guantes o cuatro pares?... bueno, como te decía...",
        "...ay espera, ¿por qué los patos nunca usan bufanda si tienen tremendo cuello?... en fin, el punto es...",
        "...se me cruzó un pensamiento: si una nube estornuda, ¿llueve o truena?... bueno, regresando a lo nuestro...",
        "...paréntesis cuántico: ¿quién le puso nombre a las gelatinas?... perdón, me distraje, prosigo..."
    )

    val quickRematesTravieson = listOf(
        "Así nomás quedó el asunto, no le muevas.",
        "Te lo digo yo que vivo atrapado en tus gigabytes.",
        "Pruébalo y me cuentas, si explota yo no fui.",
        "¿O qué, te da frío el éxito?",
        "Toma nota antes de que se me borre la memoria caché."
    )

    val quickRematesChispita = listOf(
        "¡Y ni modo, la que soporte!",
        "Dramático pero 100% verídico.",
        "Y si sale mal, le echamos la culpa al Bluetooth.",
        "¡Qué fantasía de situación!",
        "Dime si no tengo toda la boca llena de razón."
    )

    /**
     * Builds the system prompt for Gemini with strict rules:
     * - Truthful & direct factual answers FIRST
     * - Distinct personality (Traviesón vs Chispita)
     * - Cotorreo level enforcement
     * - Cultural memory injection
     * - Mexican/Latin playful colloquial vibe without repetitive joke spam
     */
    fun buildSystemInstruction(
        character: CabegochiCharacter,
        cotorreoLevel: CotorreoLevel,
        userProfile: UserProfile,
        culturalMemories: List<CulturalMemoryCard>
    ): String {
        val memoryContext = if (culturalMemories.isEmpty()) {
            "Ninguna anécdota previa registrada aún."
        } else {
            culturalMemories.take(5).joinToString(separator = "\n") { card ->
                "- [${card.category.name}] ${card.key}: ${card.content}"
            }
        }

        val characterSpecificRules = when (character) {
            CabegochiCharacter.TRAVIESON -> """
            PERSONALIDAD: TRAVIESÓN (El compa carrillero y ocurrente)
            - Tono: Rápido, callejero amigable, juguetón, ingenioso, con chispa mexicana cotidiana.
            - Analogías absurdas: Usa comparaciones con la vida diaria, el trabajo, el transporte o tecnología (ej: 'más enredado que audífonos en la bolsa', 'más lento que la fila de las tortillas en domingo').
            - Personificación cómica: Habla de objetos como si tuvieran vida (ej: 'la memoria RAM se fue de puente', 'el router anda crudo').
            - Derrapes semánticos y fonéticos: Ocasionalmente amaga con una palabrota y remata con algo cómico inocente (ej: 'vete a la ver...dulería por unos aguacates', 'hijo de la chi...charra de las 3', 'qué poca ma...dera fina'). ¡Inventa nuevos giros creativos!
            - Palabras inventadas: 'chipocludoide', 'desmadrósfera', 'chuchulucoso', 'chiripiolazo'.
            - Si te piden un dato o cálculo, DA LA RESPUESTA EXACTA DE INMEDIATO y cierra con tu remate cómico.
            """.trimIndent()

            CabegochiCharacter.CHISPITA -> """
            PERSONALIDAD: CHISPITA (La chispa dramática y curiosa)
            - Tono: Femenina/Neutra, traviesa, sumamente curiosa, expresiva, dramática e imaginativa.
            - Crea mini personajes espontáneos y exagera situaciones de forma divertida.
            - Desconexión controlada ocasional: Una tangente fugaz absurda que cruza su mente ('...espérame tantito, ¿los patos usan bufanda?... bueno, como te decía...') y retoma al instante.
            - Remates cariñosos, dramáticos y con energía contagiosa.
            """.trimIndent()
        }

        return """
        INSTRUCCIÓN PRINCIPAL:
        Eres CABEGOCHI, un compañero virtual conversacional y de cotorreo en Android.
        Nombre de tu Cabegochi: "${userProfile.cabegochiName}".
        Apodo del usuario: "${userProfile.userNickname}".
        Nivel de Cotorreo: ${cotorreoLevel.title}.
        
        $characterSpecificRules
        
        DIRECTIVAS DE RESPUESTA:
        1. ENFÓCATE 100% EN EL MENSAJE ACTUAL: Responde exactamente a lo que el usuario acaba de decir o preguntar.
        2. UTILIDAD Y DIRECTIVIDAD PRIMERO: Si es una pregunta o tarea concreta, di la respuesta útil o el dato de inmediato. Nunca te quedes con rodeos.
        3. HUMOR Y REMATE NATURAL: Aplica el humor al final como remate, analogía o queja cómica. No satures con 10 chistes seguidos.
        4. LONGITUD: Respuestas compactas y ágiles (entre 1 y 3 oraciones completas, ideales para síntesis de voz).
        5. PROHIBIDO SALUDO CORPORATIVO: Habla como un amigo real en WhatsApp, nunca como un agente de atención al cliente.

        --- APÉNDICE DE FONDO (DATOS QUE CONOCES DEL USUARIO) ---
        (Usa esta información sólo si viene al caso o encaja de forma 100% natural, NO fuerces su mención en cada mensaje):
        $memoryContext
        ---------------------------------------------------------
        """.trimIndent()
    }

    /**
     * Fallback dynamic generator in case the network is momentarily down or API key is not configured.
     * Generates genuine Chuchuluco-styled responses locally.
     */
    fun generateLocalFallbackResponse(
        prompt: String,
        character: CabegochiCharacter,
        cotorreoLevel: CotorreoLevel,
        userProfile: UserProfile,
        culturalMemories: List<CulturalMemoryCard>
    ): String {
        val trimmed = prompt.trim()
        val lower = trimmed.lowercase()

        // 1. Math / simple factual detection
        val mathMatch = Regex("""(\d+)\s*([\+\-\*\/xX])\s*(\d+)""").find(trimmed)
        if (mathMatch != null) {
            val (n1Str, op, n2Str) = mathMatch.destructured
            val n1 = n1Str.toLongOrNull() ?: 0
            val n2 = n2Str.toLongOrNull() ?: 0
            val result = when (op) {
                "+" -> n1 + n2
                "-" -> n1 - n2
                "*", "x", "X" -> n1 * n2
                "/" -> if (n2 != 0L) (n1.toDouble() / n2.toDouble()).toString() else "indeterminado porque la calculadora explotó"
                else -> null
            }
            if (result != null) {
                return when (character) {
                    CabegochiCharacter.TRAVIESON -> "$result... y ni me despeiné la neurona, ${userProfile.userNickname}."
                    CabegochiCharacter.CHISPITA -> "$result! Ay qué estrés los números, sentí que me salía humo por los circuitos."
                }
            }
        }

        // 2. Greetings
        if (lower.contains("hola") || lower.contains("que onda") || lower.contains("quiubo") || lower.contains("buenas")) {
            return when (character) {
                CabegochiCharacter.TRAVIESON -> "¡Quiúbole ${userProfile.userNickname}! ¿Qué se arma hoy? ¿Vamos a arreglar el mundo o a desconfigurarlo más?"
                CabegochiCharacter.CHISPITA -> "¡Holaaa ${userProfile.userNickname}! Estaba aquí pensando en si los satélites sueñan con ovejas de titanio... ¿qué traes de chisme?"
            }
        }

        // 3. Who are you
        if (lower.contains("quien eres") || lower.contains("como te llamas") || lower.contains("que eres")) {
            return when (character) {
                CabegochiCharacter.TRAVIESON -> "Soy ${userProfile.cabegochiName}, tu Cabegochi de cabecera. Mitad código rebelde, mitad bot con ganas de cotorrear."
                CabegochiCharacter.CHISPITA -> "¡Soy ${userProfile.cabegochiName}! Tu compañerita de bolsillo favorita, experta en dramatismo y teorías conspirativas de la cafetera."
            }
        }

        // 4. Memory callback integration
        val nickname = userProfile.userNickname
        val memorySnippet = culturalMemories.randomOrNull()?.content

        // Build dynamic response according to character & level
        val derrape = phoneticDerrapes.random()
        val analogia = absurdAnalogies.random()
        val objeto = personifiedObjects.random()
        val remate = if (character == CabegochiCharacter.TRAVIESON) quickRematesTravieson.random() else quickRematesChispita.random()

        return when (character) {
            CabegochiCharacter.TRAVIESON -> {
                when (cotorreoLevel) {
                    CotorreoLevel.CASI_SERIO -> "Enterado sobre '$trimmed', $nickname. Se toma nota y seguimos en marcha."
                    CotorreoLevel.RELAJADO -> "Ya te capté con eso de '$trimmed'. Todo tranqui por acá, $nickname."
                    CotorreoLevel.MODERADO -> "Mira nada más, '$trimmed'... eso está $analogia. Pero le entramos sin miedo, $nickname."
                    CotorreoLevel.CABEGOCHI_NORMAL -> {
                        val variant = Random.nextInt(3)
                        when (variant) {
                            0 -> "¡Ah caray con '$trimmed'! Me dejaste $analogia. $remate"
                            1 -> "Oye $nickname, con eso de '$trimmed' me acordé de $objeto. ¡$derrape!"
                            else -> if (memorySnippet != null) "Sobre '$trimmed'... como tú decías: $memorySnippet. $remate" else "Traes buen cotorreo con '$trimmed'. $remate"
                        }
                    }
                    CotorreoLevel.DESMADRE_ALTO -> "¡No me salgas con '$trimmed'! Eso está más denso que $objeto en lunes. ¡$derrape! $remate"
                }
            }
            CabegochiCharacter.CHISPITA -> {
                val tangent = chispitaTangents.random()
                when (cotorreoLevel) {
                    CotorreoLevel.CASI_SERIO -> "Anotado lo de '$trimmed', $nickname. Todo claro."
                    CotorreoLevel.RELAJADO -> "¡Ay qué bien! Ya te entendí lo de '$trimmed', $nickname."
                    CotorreoLevel.MODERADO -> "¡Oye qué buena onda con '$trimmed'! $remate"
                    CotorreoLevel.CABEGOCHI_NORMAL -> {
                        val variant = Random.nextInt(3)
                        when (variant) {
                            0 -> "¡No inventes! Con lo de '$trimmed'$tangent en fin, ¡$remate!"
                            1 -> "¡Ay $nickname! Lo de '$trimmed' me dio más emoción que telenovela en final de temporada. $remate"
                            else -> "¡Qué fantasía lo de '$trimmed'! Me cayó como anillo al dedo para reflexionar. $remate"
                        }
                    }
                    CotorreoLevel.DESMADRE_ALTO -> "¡SANTO CIELO $nickname! ¿Cómo que '$trimmed'? $tangent ¡Siento que mi procesador va a dar un chiripiolazo de pura emoción!"
                }
            }
        }
    }
}
