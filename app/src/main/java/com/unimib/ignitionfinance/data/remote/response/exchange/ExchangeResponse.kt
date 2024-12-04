package com.unimib.ignitionfinance.data.remote.response.exchange

import com.google.gson.annotations.SerializedName
import com.unimib.ignitionfinance.data.remote.response.inflation.DataSet
import com.unimib.ignitionfinance.data.remote.response.inflation.Dimension
import com.unimib.ignitionfinance.data.remote.response.inflation.Dimensions
import com.unimib.ignitionfinance.data.remote.response.inflation.Header
import com.unimib.ignitionfinance.data.remote.response.inflation.Link
import com.unimib.ignitionfinance.data.remote.response.inflation.Sender
import com.unimib.ignitionfinance.data.remote.response.inflation.SeriesData
import com.unimib.ignitionfinance.data.remote.response.inflation.Structure
import com.unimib.ignitionfinance.data.remote.response.inflation.Value

data class ExchangeApiResponseData(
    @SerializedName("header") val header: Header,
    @SerializedName("dataSets") val dataSets: List<DataSet>,
    @SerializedName("structure") val structure: Structure
)

data class Header(
    @SerializedName("id") val id: String,
    @SerializedName("test") val test: Boolean,
    @SerializedName("prepared") val prepared: String,
    @SerializedName("sender") val sender: Sender
)

data class Sender(
    @SerializedName("id") val id: String
)

data class DataSet(
    @SerializedName("action") val action: String,
    @SerializedName("validFrom") val validFrom: String,
    @SerializedName("series") val series: Map<String, SeriesData>
)

data class SeriesData(
    @SerializedName("attributes") val attributes: List<Int?>,
    @SerializedName("observations") val observations: Map<String, List<Double?>>
)

data class Structure(
    @SerializedName("links") val links: List<Link>,
    @SerializedName("name") val name: String,
    @SerializedName("dimensions") val dimensions: Dimensions,
    @SerializedName("attributes") val attributes: Attributes
)

data class Link(
    @SerializedName("title") val title: String,
    @SerializedName("rel") val rel: String,
    @SerializedName("href") val href: String
)

data class Dimensions(
    @SerializedName("series") val series: List<Dimension>,
    @SerializedName("observation") val observation: List<ObservationDimension>
)

data class Dimension(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("values") val values: List<Value>
)

data class ObservationDimension(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String?,
    @SerializedName("values") val values: List<ObservationValue>
)

data class Value(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class ObservationValue(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("start") val start: String,
    @SerializedName("end") val end: String
)

data class Attributes(
    @SerializedName("series") val series: List<Attribute>,
    @SerializedName("observation") val observation: List<ObservationAttribute>
)

data class Attribute(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("values") val values: List<Value?>
)

data class ObservationAttribute(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("values") val values: List<Value?>
)
