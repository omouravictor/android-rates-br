package com.omouravictor.ratesnow.ui.rate

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.ratesnow.api.apilayer.SourceRequestRatesModel
import com.omouravictor.ratesnow.database.entity.RatesEntity
import com.omouravictor.ratesnow.model.Conversion
import com.omouravictor.ratesnow.repository.RatesRepository
import com.omouravictor.ratesnow.util.DispatcherProvider
import com.omouravictor.ratesnow.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class RateViewModel @ViewModelInject constructor(

    private val repository: RatesRepository,
    private val dispatchers: DispatcherProvider

) : ViewModel() {
    var conversionList = MutableLiveData<List<Conversion>>()

    sealed class ConversionEvent {
        class Failure(val errorText: String) : ConversionEvent()
        object Success : ConversionEvent()
        object Loading : ConversionEvent()
        object Empty : ConversionEvent()
    }

    private val requestCurrencies = ("BRL,USD,EUR,JPY,GBP,CAD")
    private val _conversion = MutableStateFlow<ConversionEvent>(ConversionEvent.Empty)
    val conversion: StateFlow<ConversionEvent> = _conversion

    fun getRates(selectedCurrency: Int, amountStr: String) {
        val amount = amountStr.toFloatOrNull()
        if (amount == null) {
            _conversion.value = ConversionEvent.Empty
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _conversion.value = ConversionEvent.Loading
            val fromCurrency = getCurrencySymbol(selectedCurrency)
            tryRatesFromApi(fromCurrency, amount)
        }
    }

    fun convert(amountStr: String) {
        val amount = amountStr.toFloatOrNull()
        if (amount == null) {
            _conversion.value = ConversionEvent.Empty
            return
        }

        if (conversionList.value != null) {
            conversionList.value!!.forEach { conversion -> conversion.amount = amount }
            conversionList.postValue(conversionList.value)
        } else {
            _conversion.value = ConversionEvent.Failure("Verifique sua conexão :(")
        }

        _conversion.value = ConversionEvent.Success
    }

    private suspend fun tryRatesFromApi(fromCurrency: String, amount: Float) {
        when (val request = repository.getRatesFromApi(fromCurrency, requestCurrencies)) {
            is Resource.Success -> {
                val rates = getRatesFromApi(request.data!!, Date())
                repository.insertRatesOnDb(rates)
                replaceConversionList(fromCurrency, amount, rates)
                _conversion.value = ConversionEvent.Success
            }
            is Resource.Error -> {
                val rates = repository.getRatesFromDb(fromCurrency)
                if (rates != null) {
                    replaceConversionList(fromCurrency, amount, rates)
                    _conversion.value = ConversionEvent.Failure("Verifique sua conexão :(")
                } else {
                    _conversion.value = ConversionEvent.Failure("Verifique sua conexão :(")
                }
            }
        }
    }

    private fun getRatesFromApi(
        requestData: SourceRequestRatesModel,
        ratesDate: Date
    ): RatesEntity =
        RatesEntity(
            requestData.base,
            requestData.rates.uSD,
            requestData.rates.eUR,
            requestData.rates.jPY,
            requestData.rates.gBP,
            requestData.rates.cAD,
            requestData.rates.bRL,
            ratesDate
        )

    private fun replaceConversionList(fromCurrency: String, amount: Float, rates: RatesEntity) {
        val list: MutableList<Conversion> = mutableListOf()

        arrayOf("BRL", "USD", "EUR", "JPY", "GBP", "CAD").forEach {
            if (it != fromCurrency) {
                val rate = when (it) {
                    "BRL" -> rates.bRL
                    "USD" -> rates.uSD
                    "EUR" -> rates.eUR
                    "JPY" -> rates.jPY
                    "GBP" -> rates.gBP
                    "CAD" -> rates.cAD
                    else -> 0.0
                }
                list.add(Conversion(fromCurrency, it, amount, rate, rates.date))
            }
        }

        conversionList.postValue(list)
    }

    private fun getCurrencySymbol(selectedCurrency: Int) = when (selectedCurrency) {
        0 -> "USD"
        1 -> "EUR"
        2 -> "JPY"
        3 -> "GBP"
        4 -> "CAD"
        5 -> "BRL"
        else -> ""
    }
}