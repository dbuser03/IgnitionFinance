import java.net.HttpURLConnection
import java.net.URL
import java.net.URI
import java.net.URLEncoder
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

fun main() {
    // Define the base URL and query parameters
    val baseUrl = "https://data-api.ecb.europa.eu/service/data"
    val flowRef = "ECB,ICP,1.0"  // Dataflow reference for inflation (e.g., Harmonised Index of Consumer Prices)
    val seriesKey = "ICP.A.IT.N.000000.4.AVR"  // Example Series Key for monthly inflation in italy
    val startPeriod = "2023-01"  // Define the start of the period (e.g., January 2023)
    val endPeriod = "2024-01"  // Define the end of the period (e.g., December 2023)
    val format = "jsondata"  // Set format to JSON

    // Encode query parameters
    val encodedStartPeriod = URLEncoder.encode(startPeriod, StandardCharsets.UTF_8.toString())
    val encodedEndPeriod = URLEncoder.encode(endPeriod, StandardCharsets.UTF_8.toString())
    val encodedFormat = URLEncoder.encode(format, StandardCharsets.UTF_8.toString())

    // Construct the complete URL with start and end periods
    val urlString = "$baseUrl/$flowRef/$seriesKey?startPeriod=$encodedStartPeriod&endPeriod=$encodedEndPeriod&format=$encodedFormat"

    // Print the URL for debugging
    println("Request URL: $urlString")

    // Fetch data from the API
    val jsonResponse = fetchDataFromApi(urlString)
    println("API Response: $jsonResponse")
}

fun fetchDataFromApi(urlString: String): String? {
    var result: String? = null
    try {
        // Create URL object with URI conversion
        val url = URI(urlString).toURL()  // Converts urlString to URI, then to URL
        // Open connection
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/vnd.sdmx.data+json;version=1.0.0-wd")
        connection.setRequestProperty("Accept-Encoding", "gzip")
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")

        // Get the response code
        val responseCode = connection.responseCode
        println("Response Code: $responseCode")

        // Read response
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            inputStream.useLines { lines ->
                lines.forEach { response.append(it) }
            }
            result = response.toString()
        } else {
            println("GET request failed with response code: $responseCode")
            // Print response headers for debugging
            println("Response Headers: ${connection.headerFields}")
            // Read error response (response body)
            val errorStream = BufferedReader(InputStreamReader(connection.errorStream))
            val errorResponse = StringBuilder()
            errorStream.useLines { lines ->
                lines.forEach { errorResponse.append(it) }
            }
            println("Error Response: $errorResponse")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}