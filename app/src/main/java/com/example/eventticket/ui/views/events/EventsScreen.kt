package com.example.eventticket.ui.views.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.eventticket.models.dto.EventVenueDTO
import com.example.eventticket.navigation.MenuDestination
import com.example.eventticket.navigation.bottomNavigation
import com.example.eventticket.ui.theme.EventTicketCustomerTheme
import com.example.eventticket.ui.views.common.BottomBar
import com.example.eventticket.ui.views.common.LoadingScreen
import com.example.eventticket.ui.views.common.NumbersBox
import com.example.eventticket.ui.views.common.TopBar
import com.example.eventticket.utils.DataState
import org.web3j.utils.Convert
import java.math.BigInteger

@Composable
fun EventsScreen(
    fetchAllEvents: () -> Unit,
    allEventTicketsState: DataState<List<EventVenueDTO>>?,
    chooseEvent: (EventVenueDTO) -> Unit
) {
    LaunchedEffect(Unit) {
        fetchAllEvents()
    }
    when (allEventTicketsState) {
        is DataState.Loading -> {
            LoadingScreen()
        }
        is DataState.Success -> {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allEventTicketsState.data) {
                        event -> EventCard(event) { chooseEvent(event) }
                }
            }
        }
        is DataState.Error -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Text(text = allEventTicketsState.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        else -> {}
    }
}

@Composable
fun EventCard(event: EventVenueDTO, chooseEvent: () -> Unit) {
    val ticketsLeft = event.ticketsLeft != BigInteger.ZERO
    val convertedPrice = Convert.fromWei(event.ticketPrice.toString(), Convert.Unit.ETHER)
    val context = LocalContext.current
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(data = event.imageUrl).apply(
            block = fun ImageRequest.Builder.() {
                crossfade(true)
            }
        ).build()
    )

    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row {
                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .width(120.dp)
                        .weight(1f)
                ) {
                    Image(
                        painter = imagePainter,
                        contentDescription = event.eventName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                    )
                    if (imagePainter.state !is AsyncImagePainter.State.Success) {
                        LoadingScreen()
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box {
                        Column {
                            Text(
                                text = event.eventName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = event.eventLocation,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    Box {
                        Column {
                            Text(
                                text = event.eventDate,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = event.eventTime,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom) {
                Row {
                    NumbersBox(event.numberOfTickets.toString(), "Total seats")
                    Spacer(modifier = Modifier.width(8.dp))
                    NumbersBox(event.ticketsLeft.toString(), "Tickets left")
                    Spacer(modifier = Modifier.width(8.dp))
                    NumbersBox(convertedPrice.toString(), "Price MATIC")
                }
                Button(modifier = Modifier.defaultMinSize(1.dp, 1.dp),
                    onClick = chooseEvent,
                    enabled = ticketsLeft,
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    if (ticketsLeft) {
                        Text(text = "Seats",
                            style = MaterialTheme.typography.labelMedium)
                    } else {
                        Text(text = "Sold out",
                            style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventsScreenPreview() {
    val event = EventVenueDTO(
        BigInteger.valueOf(1),
        "Live at The Venue1",
        "",
        BigInteger.valueOf(48),
        BigInteger.valueOf(2),
        "Copenhagen, Denmark",
        "Jan 1, 2023",
        "3:00pm to 8:00pm",
        BigInteger.valueOf(1),
        BigInteger.valueOf(3),
        "",
        ""
    )
    EventTicketCustomerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = { TopBar("Events", DataState.Success(BigInteger.valueOf(1)), {}, {}) },
                bottomBar = { BottomBar({}, MenuDestination.Tickets, bottomNavigation) }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    EventsScreen(
                        {},
                        DataState.Success(listOf(event)),
                        {}
                    )
                }
            }
        }
    }
}