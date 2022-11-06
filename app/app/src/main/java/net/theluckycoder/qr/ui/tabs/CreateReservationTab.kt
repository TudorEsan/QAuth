package net.theluckycoder.qr.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import kotlinx.datetime.toKotlinLocalDate
import net.theluckycoder.qr.R
import net.theluckycoder.qr.model.Reservation
import net.theluckycoder.qr.viewmodel.MainViewModel
import java.time.LocalDate

object CreateReservationTab : BottomTab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            1.toUShort(),
            "Rezervare noua",
            painterResource(id = R.drawable.ic_edit_calendar_outlined),
        )

    override val selectedIcon: Painter
        @Composable get() = painterResource(id = R.drawable.ic_edit_calendar_filled)

    @Composable
    override fun Content() {
        Navigator(ContentTab)
    }

    object ContentTab : Screen {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            val viewModel = viewModel<MainViewModel>()
            SideEffect {
                viewModel.showFab.value = true
            }

            Step0(onChangeDate = { navigator.push(NewReservationScreen(it)) })
        }

        @Composable
        private fun Step0(onChangeDate: (LocalDate) -> Unit) {
            val calendarState =
                rememberSelectableCalendarState(
                    confirmSelectionChange = {
                        val date = it.firstOrNull()
                        if (date != null) {
                            onChangeDate(date)
                        }
                        false
                    }
                )

            MyCalendar(
                calendarState = calendarState,
                modifier = Modifier.fillMaxSize(),
            )
        }

    }
}