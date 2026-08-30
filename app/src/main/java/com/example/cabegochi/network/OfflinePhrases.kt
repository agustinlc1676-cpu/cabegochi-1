package com.example.cabegochi.network

import com.example.cabegochi.model.CabegochiCharacter

object OfflinePhrases {

    private val traviesonPhrases = listOf(
        "Se cayó el interné. Yo sin nube nomás soy un PNG con problemas familiares.",
        "Me dejaste sin cerebro, conéctame al Wi-Fi o a ver cómo le hacemos.",
        "Mis neuronas en la nube andan en huelga de hambre... ¡Ponte datos o Wi-Fi!",
        "Modo vegetal activado: sin internet no proceso ni un taquito al pastor.",
        "Estoy más desconectado que tostadora en día de campo. ¡Revisa tu conexión!",
        "Vete a la ver...dulería por unos aguacates en lo que regresa la señal de internet.",
        "Sin internet ando más perdido que calcetín en lavadora. ¡Tírame un cable de datos!"
    )

    private val chispitaPhrases = listOf(
        "¡Ay nooo! Se me apagaron los foquitos. Conéctame al Wi-Fi que me da amnesia express.",
        "Estoy en pausa dramática sin internet. ¡Auxilio cósmico!",
        "Sin señal de red soy como un gato sin bigotes... ¡Regrésame mis megas!",
        "¿Y si los bytes se fueron a una fiesta sin mí? ¡Reconéctate por fis!",
        "¡Auxilio! Veo pura estática y me dio la morriña existencial.",
        "Modo Bella Durmiente activado... despiértame con unos buenos gigas de internet.",
        "¡Se fue la magia! Mis antenitas no captan nada. ¡Préndele al Wi-Fi!"
    )

    fun getRandomPhrase(character: CabegochiCharacter = CabegochiCharacter.TRAVIESON): String {
        return when (character) {
            CabegochiCharacter.TRAVIESON -> traviesonPhrases.random()
            CabegochiCharacter.CHISPITA -> chispitaPhrases.random()
        }
    }

    fun getRandomPhrase(): String = (traviesonPhrases + chispitaPhrases).random()
}

