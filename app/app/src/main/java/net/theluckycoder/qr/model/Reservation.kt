@file:UseSerializers(ZonedDateTimeIso8601Serializer::class)

package net.theluckycoder.qr.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.theluckycoder.qr.network.ZonedDateTimeIso8601Serializer
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
data class Reservation(
    val id: String,
    val roomId: String,
    val userId: String,
    val subject: String,
    @SerialName("from")
    val startTime: ZonedDateTime,
    val durationMinutes: Int,
    val guests: List<String> = emptyList(),
) {

    val duration: Duration
        get() = durationMinutes.minutes

    val endTime: LocalTime
        get() = startTime.toLocalTime().plusMinutes(durationMinutes.toLong())
}