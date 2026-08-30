package com.example.cabegochi.ai

/**
 * ARCHITECTURE EXTENSION POINT:
 * Future providers planned for Cabegochi roadmap once revenue/budget allows.
 * In accordance with V0.1 Economic Rules ($0 Budget), these remain as architectural
 * documentation / contract placeholders without incomplete chargeable SDKs.
 */

interface FutureOpenAIProvider : AIProvider {
    // Target: GPT-4o-mini / future lightweight models via Supabase Edge Functions
}

interface FutureGroqProvider : AIProvider {
    // Target: Llama / Mixtral ultra-low latency via Groq Free/Paid tiers
}

interface FutureLocalProvider : AIProvider {
    // Target: On-device quantized SLM (e.g. Gemma Nano / ONNX Runtime)
}
