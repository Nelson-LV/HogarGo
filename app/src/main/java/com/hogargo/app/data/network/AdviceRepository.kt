package com.hogargo.app.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdviceRepository {
    private val api: AdviceApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.adviceslip.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AdviceApiService::class.java)
    }

    suspend fun fetchRandomAdvice(): String {
        return runCatching {
            val response = api.getRandomAdvice()
            response.slip.advice
        }.getOrDefault("Organizar tu hogar un poco cada día hace que la convivencia sea más feliz.")
    }
}
