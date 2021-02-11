package com.example.currencyconverter

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.example.currencyconverter.main.CurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: CurrencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConvert.setOnClickListener{
            viewModel.convert(
                binding.etAmount.text.toString(),
                binding.fromSpinner.selectedItem.toString(),
                binding.toSpinner.selectedItem.toString()
            )
        }

        // collect event from stateFlow

        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect{ event ->
                when(event){
                    is CurrencyViewModel.CurrencyEvent.Success ->{
                        binding.progressBar.isVisible = false
                        binding.tvConvertedMoney.setTextColor(Color.BLACK)
                        binding.tvConvertedMoney.text = event.resultText
                    }
                    is CurrencyViewModel.CurrencyEvent.Failure ->{
                        binding.progressBar.isVisible = false
                        binding.tvConvertedMoney.setTextColor(Color.RED)
                        binding.tvConvertedMoney.text = event.errorText
                    }
                    is CurrencyViewModel.CurrencyEvent.Loading ->{
                        binding.progressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }
    }
}