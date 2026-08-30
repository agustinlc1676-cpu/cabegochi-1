package com.example.cabegochi.ai

import android.util.Log
import com.example.BuildConfig
import com.example.cabegochi.chuchuluco.ChuchulucoEngine
import com.example.cabegochi.model.MessageRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiProvider(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .writeTimeout(12, TimeUnit.SECONDS)
        .build()
) : AIProvider {

    override val providerId: String = "gemini_free_tier"
    override val providerDisplayName: String = "Gemini API (Free Tier)"
    override val isFreeTier: Boolean = true

    companion object {
        private const val TAG = "GeminiProvider"
        private const val MODEL_NAME = "gemini-3.5-flash"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }

    override suspend fun generateReply(request: AIRequest): AIResponse = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If no API key configured or placeholder, gracefully use local Chuchuluco engine
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "Gemini API key is blank or default. Using local Chuchuluco fallback.")
            val fallback = ChuchulucoEngine.generateLocalFallbackResponse(
                prompt = request.userMessage,
                character = request.userProfile.selectedCharacter,
                cotorreoLevel = request.userProfile.cotorreoLevel,
                userProfile = request.userProfile,
                culturalMemories = request.culturalMemories
            )
            return@withContext AIResponse.Success(fallback)
        }

        try {
            val systemInstructionText = ChuchulucoEngine.buildSystemInstruction(
                character = request.userProfile.selectedCharacter,
                cotorreoLevel = request.userProfile.cotorreoLevel,
                userProfile = request.userProfile,
                culturalMemories = request.culturalMemories
            )

            // Construct JSON payload
            val rootJson = JSONObject()

            // System Instruction
            val systemInstObj = JSONObject()
            val systemParts = JSONArray().put(JSONObject().put("text", systemInstructionText))
            systemInstObj.put("parts", systemParts)
            rootJson.put("systemInstruction", systemInstObj)

            // Contents array (recent turns)
            val contentsArray = JSONArray()
            val recentTurns = request.recentHistory.takeLast(6)
            for (msg in recentTurns) {
                if (msg.role == MessageRole.SYSTEM) continue
                val roleStr = if (msg.role == MessageRole.USER) "user" else "model"
                val contentObj = JSONObject()
                contentObj.put("role", roleStr)
                contentObj.put("parts", JSONArray().put(JSONObject().put("text", msg.text)))
                contentsArray.put(contentObj)
            }

            // Add current message
            val currentContentObj = JSONObject()
            currentContentObj.put("role", "user")
            currentContentObj.put("parts", JSONArray().put(JSONObject().put("text", request.userMessage)))
            contentsArray.put(currentContentObj)

            rootJson.put("contents", contentsArray)

            // Generation config calibrated for full comic remates and complete ideas
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.9)
            genConfig.put("topP", 0.95)
            genConfig.put("topK", 40)
            genConfig.put("maxOutputTokens", 350)
            rootJson.put("generationConfig", genConfig)

            val endpoint = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val httpRequest = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val httpResponse = client.newCall(httpRequest).execute()
            val responseCode = httpResponse.code
            val responseBody = httpResponse.body?.string() ?: ""

            if (responseCode == 429) {
                Log.w(TAG, "Rate limit reached (429) on free tier.")
                return@withContext AIResponse.QuotaExceeded(
                    "Límite de cuota gratuita alcanzado temporalmente. Tu Cabegochi está tomando una siesta de neuronas."
                )
            }

            if (!httpResponse.isSuccessful) {
                Log.e(TAG, "Gemini API failed with code $responseCode: $responseBody")
                val localFallback = ChuchulucoEngine.generateLocalFallbackResponse(
                    prompt = request.userMessage,
                    character = request.userProfile.selectedCharacter,
                    cotorreoLevel = request.userProfile.cotorreoLevel,
                    userProfile = request.userProfile,
                    culturalMemories = request.culturalMemories
                )
                return@withContext AIResponse.Error(
                    errorMessage = "Error $responseCode en servicio de IA",
                    fallbackText = localFallback
                )
            }

            // Parse response JSON
            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) {
                        return@withContext AIResponse.Success(text.trim())
                    }
                }
            }

            // If empty text returned
            val fallback = ChuchulucoEngine.generateLocalFallbackResponse(
                prompt = request.userMessage,
                character = request.userProfile.selectedCharacter,
                cotorreoLevel = request.userProfile.cotorreoLevel,
                userProfile = request.userProfile,
                culturalMemories = request.culturalMemories
            )
            AIResponse.Success(fallback)
        } catch (e: Exception) {
            Log.e(TAG, "Exception calling Gemini API: ${e.message}", e)
            val fallback = ChuchulucoEngine.generateLocalFallbackResponse(
                prompt = request.userMessage,
                character = request.userProfile.selectedCharacter,
                cotorreoLevel = request.userProfile.cotorreoLevel,
                userProfile = request.userProfile,
                culturalMemories = request.culturalMemories
            )
            AIResponse.Error(
                errorMessage = e.localizedMessage ?: "Error de conexión",
                fallbackText = fallback
            )
        }
    }
}
