package net.theluckycoder.qr.ui.composable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.theluckycoder.qr.model.Reservation
import net.theluckycoder.qr.model.Room
import net.theluckycoder.qr.model.User
import net.theluckycoder.qr.viewmodel.MainViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt


private val EventTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
private fun BasicEvent(
    event: Reservation,
    users: List<User>,
    rooms: List<Room>,
) {
    val backgroundColor = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 2.dp, bottom = 2.dp)
            .background(backgroundColor, shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        val room = rooms.find { it.id == event.roomId }?.name
        Text(
            text = event.subject,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = contentColorFor(backgroundColor),
        )

        Text(
            text = "${event.startTime.format(EventTimeFormatter)} - ${
                event.endTime.format(
                    EventTimeFormatter
                )
            } => $room",
            style = MaterialTheme.typography.bodySmall,
            color = contentColorFor(backgroundColor)
        )

        Text(
            text = "Participanți: " + event.guests.mapNotNull { v -> users.find { it.id == v }?.name }
                .joinToString(),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = contentColorFor(backgroundColor)
        )
    }
}


@Composable
fun Schedule(
    events: List<Reservation>,
    modifier: Modifier = Modifier,
    startTime: LocalTime = LocalTime.MIN,
) {
    val viewModel = viewModel<MainViewModel>()
    val users by viewModel.users.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val hourHeight = 64.dp
    val scrollState = rememberScrollState()
    Row(modifier = Modifier.fillMaxWidth()) {
        ScheduleSidebar(
            hourHeight = hourHeight,
            modifier = Modifier.verticalScroll(scrollState),
            startTime = startTime,
        )
        BasicSchedule(events, {
            BasicEvent(event = it, users, rooms)
        }, modifier, scrollState, hourHeight, startTime)
    }
}

@Composable
private fun BasicSchedule(
    events: List<Reservation>,
    eventContent: @Composable (event: Reservation) -> Unit,
    modifier: Modifier,
    scrollState: ScrollState,
    hourHeight: Dp,
    startTime: LocalTime
) {
    val dividerColor = Color.DarkGray
    Layout(
        content = {
            events.sortedBy(Reservation::startTime).forEach { event ->
                Box(modifier = Modifier.eventData(event)) {
                    eventContent(event)
                }
            }
        },
        modifier = modifier
            .verticalScroll(scrollState)
            .drawBehind {
                repeat(26 - startTime.hour) {
                    drawLine(
                        dividerColor,
                        start = Offset(0f, (it + 1) * hourHeight.toPx()),
                        end = Offset(size.width, (it + 1) * hourHeight.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            },
    ) { measureables, constraints ->
        val height = hourHeight.roundToPx() * 24
        val placeablesWithEvents = measureables.map { measurable ->
            val event = measurable.parentData as Reservation
            val eventDurationMinutes =
                ChronoUnit.MINUTES.between(event.startTime.toLocalTime(), event.endTime)
            val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(
                constraints.copy(
                    minHeight = eventHeight,
                    maxHeight = eventHeight
                )
            )
            Pair(placeable, event)
        }

        layout(constraints.maxWidth, height) {
            placeablesWithEvents.forEach { (placeable, event) ->
                val eventOffsetMinutes =
                    ChronoUnit.MINUTES.between(startTime, event.startTime.toLocalTime())
                val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
                placeable.place(0, eventY)
            }
        }
    }
}

private val HourFormatter = DateTimeFormatter.ofPattern("HH")

@Composable
private fun BasicSidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time.format(HourFormatter),
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
    )
}

@Composable
private fun ScheduleSidebar(
    hourHeight: Dp,
    modifier: Modifier = Modifier,
    startTime: LocalTime = LocalTime.MIN,
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
) {
    Column(modifier = modifier) {
        repeat(24 - startTime.hour) { i ->
            Box(modifier = Modifier.height(hourHeight)) {
                label(startTime.plusHours(i.toLong()))
            }
        }
    }
}

private class EventDataModifier(
    val event: Reservation,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = event
}

private fun Modifier.eventData(event: Reservation) = this.then(EventDataModifier(event))
