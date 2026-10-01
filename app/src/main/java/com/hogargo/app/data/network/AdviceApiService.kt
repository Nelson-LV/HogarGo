package com.hogargo.app.data.network

import retrofit2.http.GET

interface AdviceApiService {
    @GET("advice")
    suspend fun getRandomAdvice(): AdviceResponse
}
