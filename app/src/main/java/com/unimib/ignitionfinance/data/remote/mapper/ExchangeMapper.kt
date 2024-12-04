package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.response.exchange.ExchangeApiResponseData
import com.unimib.ignitionfinance.data.model.ExchangeData

object ExchangeMapper {

    fun mapToDomain(data: ExchangeApiResponseData): List<ExchangeData> {
        return data.dataSets.flatMap { dataSet ->
            dataSet.series.flatMap { (_, seriesData) ->
                seriesData.observations.mapNotNull { (date, values) ->
                    values.firstOrNull()?.let { rate ->
                        ExchangeData(date, rate)
                    }
                }
            }
        }
    }
}
