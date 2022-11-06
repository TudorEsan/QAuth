package net.theluckycoder.qr.network.service

import net.theluckycoder.qr.model.Room
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RoomService {

    @GET("/room/{id}")
    suspend fun openDoor(@Path("id") roomId: String): Response<Void>

    @GET("/rooms")
    suspend fun getAll(): Response<List<Room>>
}