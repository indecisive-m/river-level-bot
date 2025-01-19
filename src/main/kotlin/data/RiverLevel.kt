package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RiverLevel(
    @SerialName("@context") val context: String,
    val items: List<Item>,
    val meta: Meta
) {


    @Serializable
    data class Item(
        @SerialName("@id") val id: String,
        val datumType: String,
        val label: String,
        val latestReading: LatestReading,
        val notation: String,
        val parameter: String,
        val parameterName: String,
        val period: Int,
        val qualifier: String,
        val station: String,
        val stationReference: String,
        val unit: String,
        val unitName: String,
        val valueType: String
    ) {

        @Serializable
        data class LatestReading(
            @SerialName("@id") val id: String,
            val date: String,
            val dateTime: String,
            val measure: String,
            val value: Double
        )
    }

    @Serializable
    data class Meta(
        val comment: String,
        val documentation: String,
        val hasFormat: List<String>,
        val licence: String,
        val publisher: String,
        val version: String
    )
}

@Serializable
data class Post(
    val content: String
)


enum class Urgency {
    HIGH, VERY_HIGH
}