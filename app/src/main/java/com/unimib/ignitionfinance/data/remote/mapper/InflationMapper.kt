package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.domain.model.InflationData
import com.unimib.ignitionfinance.data.remote.inflation_api.InflationApiResponseData

object InflationApiMapper {

    fun mapToDomain(inflationApiResponseData: InflationApiResponseData): List<InflationData> {
        val processedData = mutableListOf<InflationData>()

        inflationApiResponseData.dataSets.forEach { dataSet ->
            dataSet.series.forEach { (seriesKey, seriesData) ->
                seriesData.observations.forEach { (date, values) ->
                    values.firstOrNull()?.let { inflationRate ->
                        processedData.add(InflationData(date, inflationRate))
                    }
                }
            }
        }

        return processedData
    }
}
