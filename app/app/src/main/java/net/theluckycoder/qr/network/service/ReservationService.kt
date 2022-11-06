package net.theluckycoder.qr.network.service

import kotlinx.serialization.Serializable
import net.theluckycoder.qr.model.Reservation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

@Serializable
class ErrorRes(
    val message: String
)

interface ReservationService {

    @GET("/reservations")
    suspend fun getMine(): Response<List<Reservation>>

    @GET("/allReservations")
    suspend fun getAll(): Response<List<Reservation>>

    @POST("/reservation/{date}")
    suspend fun add(@Body reservation: Reservation, @Path("date") date: String): Response<ErrorRes>
}
