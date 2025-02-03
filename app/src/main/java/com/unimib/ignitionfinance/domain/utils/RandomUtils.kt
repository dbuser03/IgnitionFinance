package com.unimib.ignitionfinance.domain.utils

import kotlin.math.*
import kotlin.random.Random

object RandomUtils {
    fun nextGaussian(): Double {
        val u1 = Random.nextDouble()
        val u2 = Random.nextDouble()
        return sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
    }
}