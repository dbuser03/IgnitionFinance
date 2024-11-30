package com.unimib.ignitionfinance.data.remote.api_mapper

import com.unimib.ignitionfinance.data.remote.api_response.ExchangeApiResponseData
import com.unimib.ignitionfinance.domain.model.ExchangeData

object ExchangeApiMapper {

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
