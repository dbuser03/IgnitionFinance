package com.unimib.ignitionfinance.data.remote.inflation_api

import com.google.gson.annotations.SerializedName

data class InflationApiResponseData(
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
    @SerializedName("attributes") val attributes: List<Double?>,
    @SerializedName("observations") val observations: Map<String, List<Double?>>
)

data class Structure(
    @SerializedName("links") val links: List<Link>,
    @SerializedName("name") val name: String,
    @SerializedName("dimensions") val dimensions: Dimensions,
    @SerializedName("observation") val observation: List<Observation>
)

data class Link(
    @SerializedName("title") val title: String,
    @SerializedName("rel") val rel: String,
    @SerializedName("href") val href: String
)

data class Dimensions(
    @SerializedName("series") val series: List<Dimension>
)

data class Dimension(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("values") val values: List<Value>
)

data class Value(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class Observation(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("values") val values: List<Value>
)