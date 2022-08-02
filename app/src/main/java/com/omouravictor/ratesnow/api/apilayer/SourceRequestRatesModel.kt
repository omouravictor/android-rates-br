package com.omouravictor.ratesnow.api.apilayer

import com.google.gson.annotations.SerializedName

data class SourceRequestRatesModel(
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("rates")
    val rates: SourceRequestRatesItemModel
)