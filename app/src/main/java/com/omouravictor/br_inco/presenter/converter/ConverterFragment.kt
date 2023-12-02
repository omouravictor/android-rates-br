package com.omouravictor.br_inco.presenter.converter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.omouravictor.br_inco.databinding.FragmentConverterBinding
import com.omouravictor.br_inco.presenter.rates.model.RateUiModel
import com.omouravictor.br_inco.util.FormatUtils
import com.omouravictor.br_inco.util.SystemServiceUtils

class ConverterFragment : Fragment() {

    private lateinit var binding: FragmentConverterBinding
    private val converterViewModel: ConverterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConverterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTextInputEditTextValueConverter()
    }

    override fun onStart() {
        super.onStart()

        converterViewModel.rate.observe(this) {
            setRateInfo(it)
        }

        converterViewModel.result.observe(this) {
            binding.textViewResultValue.text =
                FormatUtils.BrazilianFormats.brCurrencyFormat.format(it)
        }
    }

    override fun onStop() {
        super.onStop()
        SystemServiceUtils.hideKeyboard(requireActivity(), requireView())
    }

    private fun initTextInputEditTextValueConverter() {
        binding.textInputEditTextValueConverter.setText("1,00")
        binding.textInputEditTextValueConverter.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.textInputEditTextValueConverter.removeTextChangedListener(this)

                val cleanString = s.replace("[,.]".toRegex(), "")
                val value = cleanString.toDouble() / 100
                val formatted = FormatUtils.BrazilianFormats.brDecimalFormat.format(value)

                converterViewModel.calculateConversion(value)
                binding.textInputEditTextValueConverter.setText(formatted)
                binding.textInputEditTextValueConverter.setSelection(formatted.length)
                binding.textInputEditTextValueConverter.addTextChangedListener(this)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        SystemServiceUtils.showKeyboard(requireActivity(), binding.textInputEditTextValueConverter)
    }

    private fun setRateInfo(rateUiModel: RateUiModel) {
        binding.textViewCurrencyName.text = rateUiModel.currencyName
        binding.textViewCurrencyTerm.text = rateUiModel.currencyTerm
        binding.textViewUnitaryRateValue.text =
            FormatUtils.BrazilianFormats.brCurrencyFormat.format(rateUiModel.unitaryRate)
    }
}