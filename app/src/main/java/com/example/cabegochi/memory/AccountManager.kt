package com.example.cabegochi.memory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Simple account manager for local accounts with email/phone and OTP verification.
 * This is intentionally minimal: OTPs are stored locally and verification is simulated.
 */
class AccountManager(private val dao: CabegochiDao) {

    data class Account(val id: String, val email: String?, val phone: String?, val verified: Boolean)

    suspend fun createAccount(email: String?, phone: String?): Account {
        val id = UUID.randomUUID().toString()
        // Store as a cultural memory entry (lightweight approach) to avoid new schema entities now
        val summary = "account:$id"
        dao.insertCulturalMemory(
            CulturalMemoryEntity(
                accountId = id,
                deviceId = "local",
                category = "ACCOUNT",
                key = summary,
                content = "email=${email ?: ""};phone=${phone ?: ""};verified=false"
            )
        )
        return Account(id = id, email = email, phone = phone, verified = false)
    }

    suspend fun requestOtpFor(accountId: String): String {
        // create a 6-digit OTP and store it in a cultural memory record (simulated delivery)
        val otp = (100000..999999).random().toString()
        val key = "otp_$accountId"
        dao.insertCulturalMemory(
            CulturalMemoryEntity(
                accountId = accountId,
                deviceId = "local",
                category = "OTP",
                key = key,
                content = otp
            )
        )
        return otp
    }

    suspend fun verifyOtp(accountId: String, otp: String): Boolean {
        val key = "otp_$accountId"
        val mem = dao.getCulturalMemoryByKey(key)
        if (mem != null && mem.content == otp) {
            // mark account as verified by updating account memory
            val accKey = "account:$accountId"
            val acc = dao.getCulturalMemoryByKey(accKey)
            if (acc != null) {
                val parts = acc.content.split(';').toMutableList()
                val updated = parts.map { p -> if (p.startsWith("verified")) "verified=true" else p }.joinToString(";")
                dao.updateCulturalMemory(acc.copy(content = updated))
            }
            return true
        }
        return false
    }
}
