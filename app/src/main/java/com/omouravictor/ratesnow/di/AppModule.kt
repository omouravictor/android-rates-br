package com.omouravictor.ratesnow.di

import android.content.Context
import com.omouravictor.ratesnow.api.apilayer.RatesApi
import com.omouravictor.ratesnow.api.hgbrasil.bitcoin.BitCoinApi
import com.omouravictor.ratesnow.api.hgbrasil.stock.StocksApi
import com.omouravictor.ratesnow.database.AppDataBase
import com.omouravictor.ratesnow.repository.BitCoinRepository
import com.omouravictor.ratesnow.repository.RatesRepository
import com.omouravictor.ratesnow.repository.StocksRepository
import com.omouravictor.ratesnow.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val API_LAYER_BASE_URL = "https://api.apilayer.com/"
private const val HG_BRAZIL_BASE_URL = "https://api.hgbrasil.com/"

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRatesApi(): RatesApi = Retrofit.Builder()
        .baseUrl(API_LAYER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RatesApi::class.java)

    @Singleton
    @Provides
    fun provideStocksApi(): StocksApi = Retrofit.Builder()
        .baseUrl(HG_BRAZIL_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(StocksApi::class.java)

    @Singleton
    @Provides
    fun provideBitCoinsApi(): BitCoinApi = Retrofit.Builder()
        .baseUrl(HG_BRAZIL_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BitCoinApi::class.java)

    @Singleton
    @Provides
    fun provideAppDataBase(@ApplicationContext appContext: Context): AppDataBase =
        AppDataBase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideRatesRepository(api: RatesApi, dataBase: AppDataBase): RatesRepository =
        RatesRepository(api, dataBase)

    @Singleton
    @Provides
    fun provideStocksRepository(api: StocksApi, dataBase: AppDataBase): StocksRepository =
        StocksRepository(api, dataBase)

    @Singleton
    @Provides
    fun provideBitCoinsRepository(api: BitCoinApi, dataBase: AppDataBase): BitCoinRepository =
        BitCoinRepository(api, dataBase)

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}