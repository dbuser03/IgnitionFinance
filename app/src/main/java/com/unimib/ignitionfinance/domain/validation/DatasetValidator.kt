package com.unimib.ignitionfinance.domain.validation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules
import com.unimib.ignitionfinance.data.model.StockData

object DatasetValidator {

    private const val TAG = "DatasetValidator"

    @RequiresApi(Build.VERSION_CODES.O)
    fun validate(stockDataList: List<Map<String, StockData>>): DatasetValidationResult {
        Log.d(TAG, "Avvio validazione del dataset. Numero di prodotti: ${stockDataList.size}")

        for ((index, productData) in stockDataList.withIndex()) {
            Log.d(TAG, "Validazione prodotto #$index - Numero di date: ${productData.keys.size}")

            if (productData.isNotEmpty()) {
                var hasValidDate = false

                for (dateStr in productData.keys) {
                    if (dateStr.isNotBlank()) {
                        val isOlder = ValidationRules.isDateOlderThan(dateStr, 10)
                        Log.d(TAG, "Controllo data: $dateStr -> isOlderThan10Years: $isOlder")

                        if (isOlder) {
                            hasValidDate = true
                            Log.d(TAG, "Dataset valido trovato (data più vecchia di 10 anni: $dateStr)")
                            break
                        }
                    } else {
                        Log.w(TAG, "Trovata una data vuota o non valida nel dataset.")
                    }
                }

                if (hasValidDate) {
                    Log.d(TAG, "Validazione riuscita: il dataset contiene almeno una data valida.")
                    return DatasetValidationResult.Success
                } else {
                    Log.w(TAG, "Nessuna data valida trovata nel prodotto #$index.")
                }
            } else {
                Log.w(TAG, "Il prodotto #$index è vuoto.")
            }
        }

        Log.e(TAG, "Validazione fallita: nessun dataset contiene date valide.")
        return DatasetValidationResult.Failure(ValidationErrors.Input.YEARS_LIMIT)
    }
}


sealed class DatasetValidationResult {
    data object Success : DatasetValidationResult()
    data class Failure(val message: String) : DatasetValidationResult()
}