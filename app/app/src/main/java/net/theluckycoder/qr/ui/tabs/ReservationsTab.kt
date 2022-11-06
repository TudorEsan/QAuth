package net.theluckycoder.qr.ui.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import net.theluckycoder.qr.R
import net.theluckycoder.qr.ui.composable.Schedule
import net.theluckycoder.qr.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.LocalTime

object ReservationsTab : BottomTab {

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            0.toUShort(),
            "Rezervari",
            painterResource(id = R.drawable.ic_event_outlined),
        )

    override val selectedIcon: Painter
        @Composable get() = painterResource(id = R.drawable.ic_event_filled)

    @Composable
    override fun Content() {
        val viewModel = viewModel<MainViewModel>()
        val reservations by viewModel.myReservations.collectAsState()

        SideEffect {
            viewModel.showFab.value = true
        }

        val calendarState =
            rememberSelectableCalendarState(initialSelection = listOf(LocalDate.now()))

        val selectedDate = calendarState.selectionState.selection.firstOrNull()
        val dateReservations = remember(reservations, selectedDate) {
            reservations.filter { it.startTime.toLocalDate() == selectedDate }
        }

        Column(Modifier.fillMaxSize()) {
            MyCalendar(calendarState = calendarState)

            Schedule(
                events = dateReservations,
                startTime = LocalTime.parse("08:00", LocalTimeFormatter)
            )
        }
    }
}