package net.theluckycoder.qr.model

@kotlinx.serialization.Serializable
data class Room(
    val id: String,
    val name: String,
    val minimalRole: Int,
)