package net.theluckycoder.qr.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.theluckycoder.qr.datastore.UserDataStore
import net.theluckycoder.qr.model.Reservation
import net.theluckycoder.qr.model.Room
import net.theluckycoder.qr.model.User
import net.theluckycoder.qr.network.service.ReservationService
import net.theluckycoder.qr.network.service.RoomService
import net.theluckycoder.qr.network.service.UserService
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val roomService: RoomService,
    private val reservationService: ReservationService,
    private val userService: UserService,
    val userDataStore: UserDataStore,
) : ViewModel() {

    private val _rooms = MutableStateFlow(emptyList<Room>())
    private val _myReservations = MutableStateFlow(emptyList<Reservation>())
    private val _reservations = MutableStateFlow(emptyList<Reservation>())
    private val _users = MutableStateFlow(emptyList<User>())
    val refreshing = MutableStateFlow(false)
    val rooms = _rooms.asStateFlow()
    val myReservations = _myReservations.asStateFlow()
    val reservations = _reservations.asStateFlow()
    val users = _users.asStateFlow()
    val showFab = MutableStateFlow(true)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            userDataStore.tokens.first() ?: return@launch
            refreshing.value = true

            launch {
                _rooms.value = roomService.getAll().body() ?: emptyList()
                Log.d("Rooms", rooms.value.toString())
            }

            launch {
                _myReservations.value = reservationService.getMine().body() ?: emptyList()
                Log.d("My Reservations", myReservations.value.toString())
            }

            launch {
                _reservations.value = reservationService.getAll().body() ?: emptyList()
                Log.d("Reservations", reservations.value.toString())
            }

            launch {
                _users.value = userService.getUsers().body() ?: emptyList()
                Log.d("Users", users.value.toString())
            }

            refreshing.value = false
        }
    }

    fun openDoor(roomId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            roomService.openDoor(roomId)
        }
    }

    suspend fun createReservation(reservation: Reservation) = withContext(Dispatchers.IO) {
        val r = reservationService.add(
            reservation,
            reservation.startTime.toLocalDateTime().toString() + ":00"
        )
        refresh()
        if (r.isSuccessful) null else r.message()
    }
}