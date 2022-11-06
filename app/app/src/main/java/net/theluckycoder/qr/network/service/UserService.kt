package net.theluckycoder.qr.network.service

import net.theluckycoder.qr.model.Tokens
import net.theluckycoder.qr.model.User
import net.theluckycoder.qr.model.UserLoginForm
import net.theluckycoder.qr.model.UserSignupForm
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {

    @POST("/login")
    suspend fun login(@Body loginForm: UserLoginForm): Response<Tokens>

    @GET("/users")
    suspend fun getUsers(): Response<List<User>>
}