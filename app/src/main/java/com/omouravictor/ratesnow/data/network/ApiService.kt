package com.omouravictor.ratesnow.data.network

import com.omouravictor.ratesnow.data.network.hgbrasil.bitcoin.SourceRequestBitcoinModel
import com.omouravictor.ratesnow.data.network.hgbrasil.rates.SourceRequestCurrencyModel
import com.omouravictor.ratesnow.data.network.hgbrasil.stock.SourceRequestStockModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("finance")
    suspend fun getRates(
        @Query("fields") field: String
    ): SourceRequestCurrencyModel

    @GET("finance")
    suspend fun getBitCoin(
        @Query("fields") field: String
    ): Response<SourceRequestBitcoinModel>

    @GET("finance")
    suspend fun getStocks(
        @Query("fields") field: String
    ): Response<SourceRequestStockModel>
}