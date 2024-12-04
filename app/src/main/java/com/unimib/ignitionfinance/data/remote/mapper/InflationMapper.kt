package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.model.InflationData
import com.unimib.ignitionfinance.data.remote.response.InflationResponse

object InflationMapper {

    fun mapToDomain(inflationResponse: InflationResponse): List<InflationData> {
        return inflationResponse.dataSets.flatMap { inflationDataSet ->
            inflationDataSet.series.flatMap { (_, inflationSeries) ->
                inflationSeries.observations.mapNotNull { (date, values) ->
                    values.firstOrNull()?.toString()?.toDoubleOrNull()?.let { inflationRate ->
                        InflationData(date = date, rate = inflationRate)
                    }
                }
            }
        }
    }
}
