# Cabegochi V0.1 — Compañero Virtual de Cotorreo

Cabegochi es un compañero virtual conversacional para Android, diseñado con humor absurdo contextual, analogías ingeniosas, derrapes semánticos y aprendizaje progresivo de recuerdos culturales del usuario.

## 🎯 Principios y Regla Económica ($0 Presupuesto)
- **Cero Costos Ocultos**: No se integran servicios que requieran tarjeta o cobros automáticos.
- **Seguridad de Claves**: Las API keys se inyectan mediante variables de entorno (`.env` / `BuildConfig`), nunca quemadas en código fuente ni en el repositorio.
- **Arquitectura Extensible**: La interfaz `AIProvider` permite intercambiar proveedores de IA (Gemini Free Tier, Groq, OpenAI, o modelos locales) sin modificar el núcleo de la aplicación.
- **Voz Nativa**: La síntesis de voz (TTS) y el reconocimiento de voz (STT) utilizan los motores nativos del dispositivo Android sin costo alguno.

---

## 🚀 Características Implementadas (V0.1)

1. **Personalidades Activas**:
   - **Traviesón**: Carrillero, rápido, ingenioso, experto en analogías y derrapes fonéticos ("vete a la ver...dulería").
   - **Chispita**: Curiosa, dramática, expresiva, con desconexiones controladas y remates teatrales.
2. **Motor Chuchuluco (`ChuchulucoEngine`)**:
   - Fórmulas de analogías absurdas de la vida diaria y sistemas tecnológicos.
   - Generación estructurada de system prompts para Gemini con prioridad a respuestas útiles primero.
   - Motor local de contingencia en caso de fallos de red o límites de cuota.
3. **Memoria Cultural Viva (`Room Database`)**:
   - Aprendizaje de apodos del usuario (ej. "Papi", "Carnal", "Jefe").
   - Detección de términos favoritos, personajes recurrentes y patrones de humor.
   - Contador de interacciones y panel de inspección/borrado de recuerdos en Configuración.
4. **Selector y Personalización de Voz (TTS Android)**:
   - Detección dinámica de voces locales del sistema.
   - Sliders de velocidad (rate) y tono (pitch).
   - Botón de prueba de voz y lectura automática configurable.
5. **Entrada por Voz (Micrófono / STT)**:
   - Reconocimiento de voz en tiempo real con permisos en tiempo de ejecución.
6. **Detector de Red y Modo Vegetal**:
   - Monitoreo continuo de conexión a Internet.
   - Pantalla de suspensión cuando no hay red con frases ocurrentes y botón de reactivación.

---

## 🛠️ Configuración de API Key (Gemini Free Tier)

1. Obtén tu clave gratuita en [Google AI Studio](https://aistudio.google.com/).
2. Añade tu clave en el panel de **Secrets** de AI Studio o en el archivo `.env` en la raíz del proyecto:
   ```env
   GEMINI_API_KEY=tu_clave_de_gemini_aqui
   ```
3. Si no configuras la clave de inmediato, el motor local `LocalChuchulucoProvider` responderá de forma interactiva con el repertorio de fórmulas y memoria viva.

---

## Quickstart (Windows)

Prerequisitos:
- JDK 11 o superior (keytool debe estar disponible en PATH)
- Android SDK (platform 36, build-tools) y variables ANDROID_SDK_ROOT / ANDROID_HOME configuradas

Pasos para compilar localmente:

1. Generar debug keystore (si no existe):
   powershell -ExecutionPolicy Bypass -File .\generate-debug-keystore.ps1

2. Añadir tu Gemini API key en `.env` o dejar el placeholder para usar fallback local:
   GEMINI_API_KEY=tu_clave_de_gemini_aqui

3. Construir APK (usa gradlew si existe o el gradle incluido):
   .\build.bat

Notas:
- Si falta `gradlew` puedes generar el wrapper localmente con `gradle wrapper --gradle-version 8.2.1` (requiere Gradle instalado) o usar la distribución incluida `gradle-dist`.
- El plugin de secretos está configurado para leer `.env` y exponer variables a BuildConfig (ver app/build.gradle.kts).

---

## 🏗️ Stack Tecnológico
- **Lenguaje**: Kotlin
- **UI Toolkit**: Jetpack Compose (Material Design 3)
- **Persistencia Local**: Android Room Database (KSP)
- **Asincronía**: Coroutines & StateFlow
- **Cliente HTTP**: OkHttp3
- **Motor de Voz**: Android TextToSpeech & SpeechRecognizer
