package net.theluckycoder.qr.ui.tabs

import android.os.Parcelable
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import net.theluckycoder.qr.model.Reservation
import net.theluckycoder.qr.model.Room
import net.theluckycoder.qr.model.User
import net.theluckycoder.qr.ui.composable.AutoCompleteBox
import net.theluckycoder.qr.ui.composable.ReadonlyTextField
import net.theluckycoder.qr.ui.composable.Schedule
import net.theluckycoder.qr.ui.composable.asAutoCompleteEntities
import net.theluckycoder.qr.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

val LocalTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

@Parcelize
data class NewReservationScreen(
    private val selectedDate: LocalDate
) : Screen, Parcelable {

    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class
    )
    @Composable
    private fun Inputs(viewModel: MainViewModel) = Column(
        Modifier
            .clip(RoundedCornerShape(0.dp, 0.dp, 18.dp, 18.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(top = 4.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        val rooms by viewModel.rooms.collectAsState()
        val users by viewModel.users.collectAsState()

        Spacer(Modifier.height(WindowInsets.systemBars.only(WindowInsetsSides.Top).asPaddingValues().calculateTopPadding()))

        var subject by remember { mutableStateOf("") }
        var startTime by remember { mutableStateOf(LocalTime.now().format(LocalTimeFormatter)) }
        var endTime by remember { mutableStateOf(LocalTime.now().format(LocalTimeFormatter)) }
        var selectedRoom by remember { mutableStateOf<Room?>(null) }
        val guests = remember { mutableStateListOf<User>() }

        var showStartDialog by remember { mutableStateOf(false) }
        var showEndDialog by remember { mutableStateOf(false) }
        var showRoomDialog by remember { mutableStateOf(false) }

        if (showStartDialog) {
            TimePickerDialog(
                onDismissRequest = { showStartDialog = false },
                initialTime = LocalTime.parse(startTime, LocalTimeFormatter),
                onTimeChange = {
                    startTime = it.format(LocalTimeFormatter)
                    showStartDialog = false
                }
            )
        }

        if (showEndDialog) {
            TimePickerDialog(
                onDismissRequest = { showEndDialog = false },
                initialTime = LocalTime.parse(endTime, LocalTimeFormatter),
                onTimeChange = {
                    endTime = it.format(LocalTimeFormatter)
                    showEndDialog = false
                }
            )
        }

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Subiect") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(4.dp))

        Row {
            ReadonlyTextField(
                value = startTime.toString(),
                label = { Text("Început") },
                onClick = { showStartDialog = true },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(6.dp))

            ReadonlyTextField(
                value = endTime.toString(),
                label = { Text("Sfarsit") },
                onClick = { showEndDialog = true },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = showRoomDialog,
            onExpandedChange = { showRoomDialog = !it }
        ) {
            ReadonlyTextField(
                value = selectedRoom?.name ?: "Cameră",
                label = { Text("Cameră") },
                modifier = Modifier.menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = showRoomDialog
                    )
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                onClick = { showRoomDialog = true },
            )

            ExposedDropdownMenu(
                expanded = showRoomDialog,
                onDismissRequest = { showRoomDialog = false }
            ) {
                rooms.forEach { room ->
                    DropdownMenuItem(
                        text = { Text(room.name) },
                        onClick = {
                            selectedRoom = room
                            showRoomDialog = false
                        }
                    )
                }
            }
        }

        AnimatedVisibility(visible = guests.isNotEmpty()) {
            LazyRow(Modifier.fillMaxWidth()) {
                items(guests) {
                    InputChip(
                        label = { Text(it.name) },
                        modifier = Modifier.padding(horizontal = 2.dp),
                        trailingIcon = {
                            Icon(Icons.Default.Close, contentDescription = null)
                        },
                        onClick = {
                            guests.remove(it)
                        },
                        selected = false,
                        colors = InputChipDefaults.inputChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }
        }

        AutoCompleteBox(
            items = users.asAutoCompleteEntities(
                filter = { item, query ->
                    !guests.contains(item) && item.name.lowercase(Locale.getDefault())
                        .startsWith(query.lowercase(Locale.getDefault()))
                }
            ),
            itemContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(it.value.name, style = MaterialTheme.typography.bodyLarge)
                    Text(it.value.email, style = MaterialTheme.typography.bodySmall)
                }
            }
        ) {
            var value by remember { mutableStateOf("") }
            val view = LocalView.current

            onItemSelected { person ->
                value = ""
                guests.add(person.value)
                filter(value)
                view.clearFocus()
            }

            TextSearchBar(
                value = value,
                label = "Adaugă participanți",
                onDoneActionClick = {
                    view.clearFocus()
                },
                onClearClick = {
                    value = ""
                    filter(value)
                    view.clearFocus()
                },
                onFocusChanged = { focusState ->
                    isSearching = focusState.hasFocus
                },
                onValueChanged = { query ->
                    value = query
                    filter(value)
                }
            )
        }

        val ctx = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        Spacer(Modifier.height(2.dp))

        Button(modifier = Modifier.align(Alignment.End), onClick = {
            val start = LocalTime.parse(startTime, LocalTimeFormatter)
            val end = LocalTime.parse(endTime, LocalTimeFormatter)

            GlobalScope.launch {
                val message = viewModel.createReservation(
                    Reservation(
                        UUID.randomUUID().toString(),
                        selectedRoom!!.id,
                        "",
                        subject,
                        selectedDate.atStartOfDay(ZoneId.systemDefault())
                            .with(start),
                        (end.hour - start.hour) * 60 + end.minute - start.minute,
                        guests.map { it.id }
                    )
                )

                if (message == null) {
                    navigator.pop()
                } else {
                    launch(Dispatchers.Main) {
                        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }) {
            Text("Creează")
        }
    }

    @Composable
    override fun Content() = Column {
        val viewModel = viewModel<MainViewModel>()
        val reservations by viewModel.reservations.collectAsState()
        val dateReservations = remember(reservations, selectedDate) {
            reservations.filter { it.startTime.toLocalDate() == selectedDate }
        }

        SideEffect {
            viewModel.showFab.value = false
        }

        Inputs(viewModel)

        Schedule(
            events = dateReservations,
            startTime = LocalTime.parse("08:00", LocalTimeFormatter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextSearchBar(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    onDoneActionClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    onFocusChanged: (FocusState) -> Unit = {},
    onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(.9f)
            .onFocusChanged { onFocusChanged(it) },
        value = value,
        onValueChange = { query ->
            onValueChanged(query)
        },
        label = { Text(text = label) },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { onClearClick() }) {
                Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
            }
        },
        keyboardActions = KeyboardActions(onDone = { onDoneActionClick() }),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        )
    )
}
