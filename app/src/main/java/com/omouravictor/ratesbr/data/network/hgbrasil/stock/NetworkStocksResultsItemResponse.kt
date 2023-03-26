package com.omouravictor.ratesbr.data.network.hgbrasil.stock

import com.google.gson.annotations.SerializedName

data class NetworkStocksResultsItemResponse(
    @SerializedName("name")
    val requestStockName: String,

    @SerializedName("location")
    val requestStockLocation: String,

    @SerializedName("points")
    val requestStockPoints: Double,

    @SerializedName("variation")
    val requestStockVariation: Double
)