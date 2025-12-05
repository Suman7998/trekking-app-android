package com.example.trekking.ui  // match your folder path

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.example.trekking.BuildConfig
import android.util.Log

class ChatGeminiHelper {

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash",             // ✅ your choice
            apiKey = BuildConfig.GEMINI_API_KEY.trim()  // now injected from local.properties
        )
    }

    suspend fun getResponse(userInput: String): String {
        return try {
            // (one-time sanity check — remove later)
            Log.d("GeminiKeyCheck", "Key len=${BuildConfig.GEMINI_API_KEY.length}")

            val resp = model.generateContent(content { text(userInput) })
            resp.text ?: "No response from Gemini."
        } catch (e: Exception) {
            // if API returns an error JSON, you'll see it here
            "Error: ${e.message}"
        }
    }
}
