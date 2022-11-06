package net.theluckycoder.qr.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSignupForm(
    val name: String,
    val email: String,
    val password: String,
)

@Serializable
data class UserLoginForm(
    val email: String,
    val password: String,
)

