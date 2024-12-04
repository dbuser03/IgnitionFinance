
package com.unimib.ignitionfinance.data.remote.response

import com.google.gson.annotations.SerializedName

// Root response class
data class InflationResponse(
    @SerializedName("header") val header: Header,
    @SerializedName("dataSets") val dataSets: List<DataSet>,
    @SerializedName("structure") val structure: Structure
)

// Header class
data class Header(
    @SerializedName("id") val id: String,
    @SerializedName("test") val test: Boolean,
    @SerializedName("prepared") val prepared: String,
    @SerializedName("sender") val sender: Sender
)

data class Sender(
    @SerializedName("id") val id: String
)

// DataSet class
data class DataSet(
    @SerializedName("action") val action: String,
    @SerializedName("validFrom") val validFrom: String,
    @SerializedName("series") val series: Map<String, Series>
)

data class Series(
    @SerializedName("attributes") val attributes: List<Any?>,
    @SerializedName("observations") val observations: Map<String, List<Any?>>
)

// Structure class
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
    @SerializedName("observation") val observation: List<Observation>
)

data class Dimension(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("values") val values: List<Value>
)

data class Observation(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String?,
    @SerializedName("values") val values: List<TimeValue>
)

data class Value(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class TimeValue(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("start") val start: String,
    @SerializedName("end") val end: String
)

data class Attributes(
    @SerializedName("series") val series: List<Attribute>,
    @SerializedName("observation") val observation: List<Attribute>
)

data class Attribute(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("values") val values: List<AttributeValue>
)

data class AttributeValue(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String
)
