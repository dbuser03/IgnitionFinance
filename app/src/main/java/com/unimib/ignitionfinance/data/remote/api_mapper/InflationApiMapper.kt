package com.unimib.ignitionfinance.data.remote.api_mapper

import com.unimib.ignitionfinance.data.model.InflationData
import com.unimib.ignitionfinance.data.remote.api_response.inflation.InflationApiResponseData

object InflationApiMapper {

    fun mapToDomain(inflationApiResponseData: InflationApiResponseData): List<InflationData> {
        return inflationApiResponseData.dataSets.flatMap { dataSet ->
            dataSet.series.flatMap { (_, seriesData) ->
                seriesData.observations.mapNotNull { (date, values) ->
                    values.firstOrNull()?.let { inflationRate ->
                        InflationData(date, inflationRate)
                    }
                }
            }
        }
    }
}