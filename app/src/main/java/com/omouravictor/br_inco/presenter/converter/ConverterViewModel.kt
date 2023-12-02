package com.omouravictor.br_inco.presenter.converter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.omouravictor.br_inco.presenter.rates.model.RateUiModel
import com.omouravictor.br_inco.util.NumberUtils

class ConverterViewModel : ViewModel() {

    val rate = MutableLiveData<RateUiModel>()
    val result = MutableLiveData<Double>()
    private var unitaryRate: Double = 0.0

    fun setInitialValues(rateUiModel: RateUiModel) {
        rate.postValue(rateUiModel)
        result.postValue(rateUiModel.unitaryRate)
        unitaryRate = NumberUtils.getRoundedDouble(rateUiModel.unitaryRate)
    }

    fun calculateConversion(value: Double) {
        result.postValue(value * unitaryRate)
    }
}