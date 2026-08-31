package com.example.cabegochi.memory

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

object MemoryImporter {
    suspend fun importFromFile(context: Context, filePath: String, dao: CabegochiDao, accountId: String = "local", deviceId: String = "local") {
        withContext(Dispatchers.IO) {
            val file = File(filePath)
            if (!file.exists()) return@withContext
            importLines(file.useLines { it.toList() }, dao, accountId, deviceId)
        }
    }

    suspend fun importFromAssets(context: Context, assetName: String, dao: CabegochiDao, accountId: String = "local", deviceId: String = "local") {
        withContext(Dispatchers.IO) {
            val lines = context.assets.open(assetName).bufferedReader(Charsets.UTF_8).use { it.readLines() }
            importLines(lines, dao, accountId, deviceId)
        }
    }

    private suspend fun importLines(lines: Sequence<String>, dao: CabegochiDao, accountId: String, deviceId: String) {
        val md = MessageDigest.getInstance("SHA-256")
        lines.filter { it.isNotBlank() }.forEach { line ->
            val key = sha256(line, md).take(40)
            val existing = dao.getCulturalMemoryByKey(key)
            if (existing == null) {
                dao.insertCulturalMemory(
                    CulturalMemoryEntity(
                        accountId = accountId,
                        deviceId = deviceId,
                        category = "RAW_IMPORT",
                        key = key,
                        content = line
                    )
                )
            }
        }
    }

    private fun sha256(text: String, md: MessageDigest): String {
        val bytes = md.digest(text.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
