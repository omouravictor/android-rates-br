package com.omouravictor.ratesbr.presenter.stocks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.ratesbr.databinding.FragmentStocksBinding
import com.omouravictor.ratesbr.presenter.base.UiResultState
import com.omouravictor.ratesbr.presenter.stocks.model.StockUiModel

class StocksFragment : Fragment() {

    private lateinit var binding: FragmentStocksBinding
    private val stockViewModel: StocksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStocksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTryAgainButton()

        stockViewModel.stocksResult.observe(viewLifecycleOwner) {
            when (it) {
                is UiResultState.Success -> {
                    configureRecyclerView(it.data)
                    binding.progressBar.isVisible = false
                    binding.recyclerViewStocks.isVisible = true
                    binding.includeViewError.root.isVisible = false
                }
                is UiResultState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.recyclerViewStocks.isVisible = false
                    binding.includeViewError.root.isVisible = true
                    binding.includeViewError.textViewErrorMessage.text = it.e.message
                }
                is UiResultState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.recyclerViewStocks.isVisible = false
                    binding.includeViewError.root.isVisible = false
                }
            }
        }
    }

    private fun initTryAgainButton() {
        binding.includeViewError.buttonTryAgain.setOnClickListener {
            stockViewModel.getStocks()
        }
    }

    private fun configureRecyclerView(stockList: List<StockUiModel>) {
        binding.recyclerViewStocks.apply {
            adapter = StocksAdapter(stockList)
            layoutManager = LinearLayoutManager(context)
        }
    }

}