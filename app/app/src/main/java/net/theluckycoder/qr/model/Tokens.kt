package net.theluckycoder.qr.model

import kotlinx.serialization.Serializable

@Serializable
data class Tokens(val token: String, val refreshToken: String)