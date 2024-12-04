package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.model.ExchangeData
import com.unimib.ignitionfinance.data.remote.response.ExchangeResponse  // Corrected import path

object ExchangeMapper {

    fun mapToDomain(exchangeResponse: ExchangeResponse): List<ExchangeData> {
        return exchangeResponse.dataSets.flatMap { exchangeDataSet ->
            exchangeDataSet.series.flatMap { (_, exchangeSeries) ->
                exchangeSeries.observations.mapNotNull { (date, values) ->
                    values.firstOrNull()?.toString()?.toDoubleOrNull()?.let { exchangeRate ->
                        ExchangeData(date = date, rate = exchangeRate)
                    }
                }
            }
        }
    }
}
