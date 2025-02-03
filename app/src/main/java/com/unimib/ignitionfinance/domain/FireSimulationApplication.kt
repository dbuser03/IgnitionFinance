package com.unimib.ignitionfinance.domain

import com.unimib.ignitionfinance.data.model.InflationData
import com.unimib.ignitionfinance.data.repository.interfaces.InflationRepository
import com.unimib.ignitionfinance.domain.inflation.InflationDataProvider
import com.unimib.ignitionfinance.domain.inflation.InflationScenarioGenerator
import com.unimib.ignitionfinance.domain.models.FireSimulationConfig
import com.unimib.ignitionfinance.domain.portfolio.FireSimulation
import com.unimib.ignitionfinance.domain.portfolio.PortfolioReturnsStrategy
import com.unimib.ignitionfinance.domain.usecase.inflation.FetchInflationUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

suspend fun main() = runBlocking {
    // Implementazione mock di InflationRepository che continua la serie storica
    val inflationRepository = object : InflationRepository {
        override suspend fun fetchInflationData(): Flow<Result<List<InflationData>>> = flow {
            val mockInflationData = listOf(
                InflationData(1988.toString(), 4.8),
                InflationData(1989.toString(), 5.0),
                InflationData(1990.toString(), 6.1),
                InflationData(1991.toString(), 6.5),
                InflationData(1992.toString(), 5.4),
                InflationData(1993.toString(), 4.2),
                InflationData(1994.toString(), 4.0),
                InflationData(1995.toString(), 5.1),
                InflationData(1996.toString(), 3.8),
                InflationData(1997.toString(), 2.7),
                InflationData(1998.toString(), 2.0),
                InflationData(1999.toString(), 1.9),
                InflationData(2000.toString(), 2.5),
                InflationData(2001.toString(), 2.8),
                InflationData(2002.toString(), 3.2),
                InflationData(2003.toString(), 2.9),
                InflationData(2004.toString(), 2.3)
            )
            emit(Result.success(mockInflationData))
        }
    }

    // Create use case with the repository
    val fetchInflationUseCase = FetchInflationUseCase(inflationRepository)

    // Dependency setup
    val inflationDataProvider = InflationDataProvider(fetchInflationUseCase)
    val inflationScenarioGenerator = InflationScenarioGenerator(inflationDataProvider)
    val portfolioReturnsStrategy = PortfolioReturnsStrategy()

    // Create simulation with custom configuration
    val fireSimulation = FireSimulation(
        inflationScenarioGenerator,
        portfolioReturnsStrategy,
        FireSimulationConfig(
            capitale = 600000.0,
            prelievo = 2500.0 * 13
        )
    )

    // Run simulation
    val result = fireSimulation.simulate()

    // Print results
    println("Probabilità di successo: ${result.successRate}%")
    println("Media totale dopo gli anni di rendita: ${result.mediaTotale}")
    println("Deviazione standard: ${result.deviazioneStandardTotale}")
}