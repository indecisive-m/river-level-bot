package org.example

import data.Post
import data.RiverLevel
import data.Urgency
import io.github.cdimascio.dotenv.dotenv
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


val dotenv = dotenv()
val discord_webhook = dotenv["DISCORD_WEBHOOK_URL"]
val environmentAgencyUrl = dotenv["ENVIRONMENT_AGENCY_URL"]


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
            client.get(environmentAgencyUrl).body()

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
        client.post(discord_webhook) {
            contentType(ContentType.Application.Json)
            setBody(body = Post(riverLevelMessage))

        }

    println(post.bodyAsText())
}
