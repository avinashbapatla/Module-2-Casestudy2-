package project.casestudy2.api

import project.casestudy2.models.PromptRequest
import project.casestudy2.models.PromptResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers

interface GrokApiService {
    @POST("chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun getPromptResponse(
        @Body request: PromptRequest
    ): Response<PromptResponse>
}