package project.casestudy2.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import project.casestudy2.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://api.x.ai/v1/"

    private fun getApiKey(): String {
        return BuildConfig.GROK_API_KEY
    }

    private val client: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val apiKey = getApiKey()
                if (apiKey.isBlank()) {
                    throw IllegalStateException("GROK_API_KEY is missing. Add it to local.properties.")
                }
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    val apiService: GrokApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GrokApiService::class.java)
    }
}