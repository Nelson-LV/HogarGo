package com.hogargo.app.data.network

import com.google.gson.annotations.SerializedName

data class AdviceResponse(
    @SerializedName("slip") val slip: AdviceSlip,
)

data class AdviceSlip(
    @SerializedName("id") val id: Int,
    @SerializedName("advice") val advice: String,
)
