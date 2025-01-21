package com.unimib.ignitionfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "datasets")
data class Dataset(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "dates") val dates: List<String>,
    @ColumnInfo(name = "weighted_returns") val weightedReturns: List<BigDecimal>
)
