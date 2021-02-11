package com.example.currencyconverter.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.models.Rates
import com.example.currencyconverter.utils.DispatcherProvider
import com.example.currencyconverter.utils.Resource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Math.round

class CurrencyViewModel @ViewModelInject constructor(
        private val repository: MainRepository,
        private val dispatcher: DispatcherProvider
) : ViewModel() {

        sealed class CurrencyEvent{
                class Success(val resultText: String): CurrencyEvent()
                class Failure(val errorText: String): CurrencyEvent()
                object Loading : CurrencyEvent()
                object Empty : CurrencyEvent()
        }

        // StateFlow
        private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
        val conversion: StateFlow<CurrencyEvent> = _conversion

        fun convert(
                amountString: String,
                fromCurrency: String,
                toCurrency: String
        ){
                val fromAmount = amountString.toFloatOrNull()
                if(fromAmount == null){
                        _conversion.value = (CurrencyEvent.Failure("Not a valid number"))
                        return
                }

                viewModelScope.launch(dispatcher.io) {
                        _conversion.value = CurrencyEvent.Loading
                        when (val ratesResponse = repository.getRates(fromCurrency)) {
                                is Resource.Error -> _conversion.value =
                                        CurrencyEvent.Failure(ratesResponse.message!!)
                                is Resource.Success -> {
                                        val rates = ratesResponse.data!!.rates
                                        val rate = getRateForCurrency(toCurrency, rates)
                                        if(rate == null){
                                                _conversion.value = CurrencyEvent.Failure("Unexpected Error")
                                        }else{
                                                val converted = round(fromAmount * rate * 100) / 100
                                                _conversion.value = CurrencyEvent.Success(
                                                        "$fromAmount $fromCurrency = $converted $toCurrency"
                                                )
                                        }
                                }
                        }

                }
        }

        private fun getRateForCurrency(currency: String, rates: Rates) = when (currency) {
                "CAD" -> rates.CAD
                "HKD" -> rates.HKD
                "ISK" -> rates.ISK
                "EUR" -> rates.EUR
                "PHP" -> rates.PHP
                "DKK" -> rates.DKK
                "HUF" -> rates.HUF
                "CZK" -> rates.CZK
                "AUD" -> rates.AUD
                "RON" -> rates.RON
                "SEK" -> rates.SEK
                "IDR" -> rates.IDR
                "INR" -> rates.INR
                "BRL" -> rates.BRL
                "RUB" -> rates.RUB
                "HRK" -> rates.HRK
                "JPY" -> rates.JPY
                "THB" -> rates.THB
                "CHF" -> rates.CHF
                "SGD" -> rates.SGD
                "PLN" -> rates.PLN
                "BGN" -> rates.BGN
                "CNY" -> rates.CNY
                "NOK" -> rates.NOK
                "NZD" -> rates.NZD
                "ZAR" -> rates.ZAR
                "USD" -> rates.USD
                "MXN" -> rates.MXN
                "ILS" -> rates.ILS
                "GBP" -> rates.GBP
                "KRW" -> rates.KRW
                "MYR" -> rates.MYR
                else -> null
        }
}