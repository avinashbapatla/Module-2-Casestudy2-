package project.casestudy2.models

import com.google.gson.annotations.SerializedName

data class PromptRequest(
    val messages: List<Map<String, String>>,
    val model: String = "grok-beta",
    val temperature: Double = 0.7,
    @SerializedName("max_tokens")
    val maxTokens: Int = 500
)