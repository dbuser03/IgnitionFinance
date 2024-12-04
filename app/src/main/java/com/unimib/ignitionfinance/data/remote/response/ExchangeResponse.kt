package com.unimib.ignitionfinance.data.remote.response

import com.google.gson.annotations.SerializedName

data class ExchangeResponse(
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
    @SerializedName("series") val series: Map<String, Series>
)

data class Series(
    @SerializedName("attributes") val attributes: List<Any?>,
    @SerializedName("observations") val observations: Map<String, List<Any?>>
)

data class Structure(
    @SerializedName("links") val links: List<Link>,
    @SerializedName("name") val name: String,
    @SerializedName("dimensions") val dimensions: Dimensions
)

data class Link(
    @SerializedName("title") val title: String,
    @SerializedName("rel") val rel: String,
    @SerializedName("href") val href: String
)

data class Dimensions(
    @SerializedName("series") val series: List<SeriesDimension>,
    @SerializedName("observation") val observation: List<ObservationDimension>
)

data class SeriesDimension(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("values") val values: List<Value>
)

data class ObservationDimension(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("values") val values: List<Value>
)

data class Value(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)
