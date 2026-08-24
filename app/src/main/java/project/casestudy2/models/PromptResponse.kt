package project.casestudy2.models

import com.google.gson.annotations.SerializedName

data class PromptResponse(
    val id: String? = null,
    @SerializedName("object")
    val objectType: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<Choice>? = null,
    val usage: Usage? = null
)

data class Choice(
    val index: Int? = null,
    val message: Message? = null,
    @SerializedName("finish_reason")
    val finishReason: String? = null
)

data class Message(
    val role: String? = null,
    val content: String? = null
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int? = null,
    @SerializedName("completion_tokens")
    val completionTokens: Int? = null,
    @SerializedName("total_tokens")
    val totalTokens: Int? = null
)