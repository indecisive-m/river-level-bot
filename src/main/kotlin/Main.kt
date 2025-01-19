package org.example

import data.Post
import data.RiverLevel
import data.Urgency
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


val THRESHOLD = 6

val client = HttpClient(CIO) {
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.INFO
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 10000
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true

        })
    }
}

suspend fun main() {


    try {
        val response: RiverLevel =
            client.get("https://environment.data.gov.uk/flood-monitoring/id/stations/52124/measures").body()

        val measure = response.items[0].latestReading.value
        val date = response.items[0].latestReading.date
        val dateTime = response.items[0].latestReading.dateTime
        val zonedDateTime = ZonedDateTime.parse(dateTime)
        val formatter = DateTimeFormatter.ofPattern("hh:mm:ssa", Locale.UK)
        val time = zonedDateTime.format(formatter)


        when {
            measure > THRESHOLD && measure < 7 -> sendDiscordMessage(time, date, measure, Urgency.HIGH)
            measure > 7 -> sendDiscordMessage(time, date, measure, Urgency.VERY_HIGH)
            else -> println("The river level reading at $time on $date is ${measure}m")
        }

        client.close()

    } catch (e: HttpRequestTimeoutException) {
        println("Http request timeout exception: ${e.message}")
    } catch (e: ConnectTimeoutException) {
        println("Connect timeout exception: ${e.message}")
    } catch (e: SocketTimeoutException) {
        println("Socket timeout exception: ${e.message}")
    }


}


suspend fun sendDiscordMessage(time: String, date: String, measure: Double, urgency: Urgency) {

    val high = "The river level reading at $time on $date is ${measure}m, this is above the threshold of ${THRESHOLD}m"

    val veryHigh = "The river level reading at $time on $date is ${measure}m, this is very high!"

    val riverLevelMessage = if (urgency === Urgency.HIGH) high else veryHigh


    val post: HttpResponse =
        client.post("https://discord.com/api/webhooks/1323029259073093652/MCehx9lS5amarNdtg5iIdqmy9K0IkXxLnL4S0BI5GqhJsWLrRgOYMjlVoQk8x19JFF-l") {
            contentType(ContentType.Application.Json)
            setBody(body = Post(riverLevelMessage))

        }

    println(post.bodyAsText())
}
