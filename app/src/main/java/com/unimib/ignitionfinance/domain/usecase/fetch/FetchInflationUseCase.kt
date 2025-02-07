package com.unimib.ignitionfinance.domain.usecase.fetch

import com.unimib.ignitionfinance.data.repository.interfaces.InflationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FetchInflationUseCase @Inject constructor(
    private val inflationRepository: InflationRepository
){
    private val historicalData = mapOf(
        1955 to 2.3, 1956 to 3.4, 1957 to 1.3, 1958 to 2.8, 1959 to -0.4,
        1960 to 2.3, 1961 to 2.1, 1962 to 4.7, 1963 to 7.5, 1964 to 5.9,
        1965 to 4.6, 1966 to 2.3, 1967 to 3.7, 1968 to 1.4, 1969 to 2.6,
        1970 to 5.0, 1971 to 4.8, 1972 to 5.7, 1973 to 10.8, 1974 to 19.1,
        1975 to 17.0, 1976 to 16.8, 1977 to 17.0, 1978 to 12.1, 1979 to 14.8,
        1980 to 21.2, 1981 to 17.8, 1982 to 16.5, 1983 to 14.7, 1984 to 10.8,
        1985 to 9.2, 1986 to 5.8, 1987 to 4.8
    )

    fun execute(): Flow<Result<Map<Int, Double>>> = flow {
        try {
            inflationRepository.fetchInflationData()
                .collect { result ->
                    result.fold(
                        onSuccess = { inflationList ->
                            val apiData: Map<Int, Double> = inflationList.associate { inflationData ->
                                inflationData.year.toInt() to inflationData.inflationRate
                            }
                            val combinedData: Map<Int, Double> = historicalData + apiData
                            emit(Result.success(combinedData))
                        },
                        onFailure = { exception ->
                            emit(Result.failure(exception))
                        }
                    )
                }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}