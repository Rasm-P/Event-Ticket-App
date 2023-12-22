package com.example.eventticket.ui.views.tickets

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eventticket.R
import com.example.eventticket.models.ResaleTicketData
import com.example.eventticket.models.dto.EventVenueDTO
import com.example.eventticket.models.dto.ListedTicketDTO
import com.example.eventticket.models.dto.NFTDataDTO
import com.example.eventticket.navigation.MenuDestination
import com.example.eventticket.navigation.bottomNavigation
import com.example.eventticket.ui.theme.EventTicketCustomerTheme
import com.example.eventticket.ui.views.common.BottomBar
import com.example.eventticket.ui.views.common.LoadingScreen
import com.example.eventticket.ui.views.common.TopBar
import com.example.eventticket.ui.views.present.PresentDialog
import com.example.eventticket.ui.views.resale.ResaleDialog
import com.example.eventticket.utils.DataState
import org.web3j.crypto.Credentials
import org.web3j.utils.Convert
import java.math.BigInteger
import java.math.RoundingMode

@Composable
fun TicketsScreen(
    fetchSenderNFTs: () -> Unit,
    senderNFTsState: DataState<List<NFTDataDTO>>?,
    allEventTicketsState: DataState<List<EventVenueDTO>>?,
    listTicketForSale: (BigInteger, BigInteger) -> Unit,
    resellTicketsState: DataState<String>?,
    resetResaleState: () -> Unit,
    fetchTicketsListedBySender: () -> Unit,
    ticketsListedBySenderState: DataState<List<ListedTicketDTO>>?,
    withdrawFromResaleList: (BigInteger) -> Unit,
    withdrawFromResaleState: DataState<String>?,
    resetWithdrawResaleState: () -> Unit,
    updateWalletBalance: () -> Unit,
    credentials: Credentials?
) {
    LaunchedEffect(Unit) {
        fetchSenderNFTs()
        fetchTicketsListedBySender()
    }
    when {
        senderNFTsState is DataState.Loading || allEventTicketsState is DataState.Loading || ticketsListedBySenderState is DataState.Loading -> {
            LoadingScreen()
        }
        senderNFTsState is DataState.Success && allEventTicketsState is DataState.Success && ticketsListedBySenderState is DataState.Success -> {
            var selectedTicketForResale by remember { mutableStateOf<ResaleTicketData?>(null) }
            var selectedTicketToWithdraw by remember { mutableStateOf<BigInteger?>(null) }
            var selectedTicketForPresentation by remember { mutableStateOf<Pair<NFTDataDTO, EventVenueDTO>?>(null) }
            var isUpcomingEventsSelected by remember { mutableStateOf(true) }
            val upcomingEventTickets = senderNFTsState.data.filter { !it.usedStatus }
            val pastEventTickets = senderNFTsState.data.filter { it.usedStatus }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text(
                        modifier = Modifier.clickable { isUpcomingEventsSelected = true },
                        text = "Upcoming events",
                        color = if (isUpcomingEventsSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        modifier = Modifier.clickable { isUpcomingEventsSelected = false },
                        text = "Past events",
                        color = if (!isUpcomingEventsSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (isUpcomingEventsSelected) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f) ) {
                            if (upcomingEventTickets.isNotEmpty()) {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    items(upcomingEventTickets) { ticket ->
                                        TicketCard(
                                            ticket = ticket,
                                            events = allEventTicketsState.data,
                                            selectForResale = { eventVenue, nftData ->
                                                selectedTicketForResale =
                                                    ResaleTicketData(eventVenue, nftData)
                                            },
                                            selectForPresentation = { nftData, eventData ->
                                                selectedTicketForPresentation =
                                                    Pair(nftData, eventData)
                                            }
                                        )
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "You have no tickets for upcoming events!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        Column(modifier= Modifier.padding(top = 8.dp, bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Tickets listed for resale",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(modifier = Modifier.fillMaxWidth().weight(1f) ) {
                            if (ticketsListedBySenderState.data.isNotEmpty()) {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    items(ticketsListedBySenderState.data) { ticket ->
                                        ListingCard(
                                            ticket = ticket,
                                            withdrawFromResaleList = { listingId ->
                                                withdrawFromResaleList(listingId)
                                                selectedTicketToWithdraw = listingId
                                            },
                                            withdrawFromResaleState = withdrawFromResaleState,
                                            fetchTickets = {
                                                fetchSenderNFTs()
                                                fetchTicketsListedBySender()
                                                updateWalletBalance()
                                            },
                                            resetWithdrawResaleState = {
                                                resetWithdrawResaleState()
                                                selectedTicketToWithdraw = null
                                            },
                                            selectedTicketToWithdraw = selectedTicketToWithdraw
                                        )
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "You have no tickets listed for resale!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else {
                    if (pastEventTickets.isNotEmpty()) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(pastEventTickets) { ticket ->
                                PastTicketCard(
                                    ticket = ticket,
                                    events = allEventTicketsState.data
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "You have no past event tickets!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                if (selectedTicketForResale != null) {
                    ResaleDialog(
                        event = selectedTicketForResale!!.event,
                        ticket = selectedTicketForResale!!.nftData,
                        onDismissRequest = {
                            selectedTicketForResale = null
                            resetResaleState()
                        },
                        listTicketForSale = listTicketForSale,
                        ticketResaleState = resellTicketsState,
                        fetchNFTsAndListings = {
                            fetchSenderNFTs()
                            fetchTicketsListedBySender()
                            updateWalletBalance()
                        }
                    )
                } else if (selectedTicketForPresentation != null && credentials != null) {
                    PresentDialog(
                        onDismissRequest = {
                            selectedTicketForPresentation = null; fetchSenderNFTs()
                        },
                        eventData = selectedTicketForPresentation!!,
                        credentials = credentials
                    )
                }
            }
        }
        senderNFTsState is DataState.Error -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Text(text = senderNFTsState.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        allEventTicketsState is DataState.Error -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Text(text = allEventTicketsState.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        ticketsListedBySenderState is DataState.Error -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Text(text = ticketsListedBySenderState.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        else -> {}
    }
}

@Composable
fun TicketCard(
    ticket: NFTDataDTO,
    events: List<EventVenueDTO>,
    selectForResale: (EventVenueDTO, NFTDataDTO) -> Unit,
    selectForPresentation: (NFTDataDTO, EventVenueDTO) -> Unit
) {
    val eventData = events.find { event -> event.id == ticket.eventId }
    if (eventData != null) {
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.ticketicon),"Ticket", modifier = Modifier
                        .size(90.dp)
                        .weight(1f))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                        Text(text = eventData.eventName,
                            style = MaterialTheme.typography.bodyMedium)
                        Text(text = eventData.eventLocation,
                            style = MaterialTheme.typography.labelSmall)
                        Text(text = "Sear nr. " + ticket.seatNr.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Text(text = eventData.eventDate,
                            style = MaterialTheme.typography.labelSmall)
                        Text(text = eventData.eventTime,
                            style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(modifier = Modifier.weight(1f), onClick = { selectForResale(eventData, ticket)},
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(text = "Resell ticket",
                            style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(modifier = Modifier.weight(1f), onClick = { selectForPresentation(ticket, eventData) },
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(text = "Present ticket",
                            style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun PastTicketCard(
    ticket: NFTDataDTO,
    events: List<EventVenueDTO>
) {
    val eventData = events.find { event -> event.id == ticket.eventId }
    if (eventData != null) {
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.ticketicon),"Ticket", modifier = Modifier
                        .size(90.dp)
                        .weight(1f))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                        Text(text = eventData.eventName,
                            style = MaterialTheme.typography.bodyMedium)
                        Text(text = eventData.eventLocation,
                            style = MaterialTheme.typography.labelSmall)
                        Text(text = "Sear nr. " + ticket.seatNr.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Text(text = eventData.eventDate,
                            style = MaterialTheme.typography.labelSmall)
                        Text(text = eventData.eventTime,
                            style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun ListingCard(
    ticket: ListedTicketDTO,
    withdrawFromResaleList: (BigInteger) -> Unit,
    withdrawFromResaleState: DataState<String>?,
    fetchTickets: () -> Unit,
    resetWithdrawResaleState: () -> Unit,
    selectedTicketToWithdraw: BigInteger?,
) {
    val value = Convert.fromWei(ticket.listingPrice.toString(), Convert.Unit.ETHER)
    val threeDecimalValue = value.setScale(3, RoundingMode.HALF_UP)
    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ticketicon),
                        "Ticket",
                        modifier = Modifier
                            .size(90.dp)
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f), verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = ticket.eventName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = ticket.eventLocation,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "Sear nr. " + ticket.seatNr.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = ticket.eventDate,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = ticket.eventTime,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center) {
                        Text(
                            text = "Price: ",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$threeDecimalValue MATIC",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { withdrawFromResaleList(ticket.listingId) },
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "Withdraw",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            if (selectedTicketToWithdraw == ticket.listingId) {
                when (withdrawFromResaleState) {
                    is DataState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .width(64.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    is DataState.Success -> {
                        Toast.makeText(
                            LocalContext.current,
                            "Transaction: " + withdrawFromResaleState.data,
                            Toast.LENGTH_LONG
                        ).show()
                        fetchTickets()
                        resetWithdrawResaleState()
                    }
                    is DataState.Error -> {
                        Toast.makeText(
                            LocalContext.current,
                            "Error: " + withdrawFromResaleState.error,
                            Toast.LENGTH_LONG
                        ).show()
                        resetWithdrawResaleState()
                    }
                    else -> {}
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketsScreenPreview() {
    val senderNFTsState = listOf(NFTDataDTO(BigInteger.valueOf(1),BigInteger.valueOf(1),BigInteger.valueOf(1),false))
    val allEventTicketsState = listOf(EventVenueDTO(
        BigInteger.valueOf(1),
        "Live at The Venue1",
        "",
        BigInteger.valueOf(20),
        BigInteger.valueOf(18),
        "Copenhagen, Denmark",
        "Jan 1, 2023",
        "3:00pm to 8:00pm",
        BigInteger.valueOf(1),
        BigInteger.valueOf(3),
        "",
        ""
    ))
    val ticketsListedBySenderState = listOf(ListedTicketDTO(
        BigInteger.valueOf(1),
        "",
        BigInteger.valueOf(1),
        eventId = BigInteger.valueOf(1),
        seatNr = BigInteger.valueOf(1),
        eventName = "Live at The Venue1",
        eventLocation = "Copenhagen, Denmark",
        eventDate = "Jan 1, 2023",
        eventTime = "3:00pm to 8:00pm"
    ))
    EventTicketCustomerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = { TopBar("Tickets", DataState.Success(BigInteger.valueOf(1)), {}, {}) },
                bottomBar = { BottomBar({}, MenuDestination.Tickets, bottomNavigation) }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    TicketsScreen(
                        {},
                        DataState.Success(senderNFTsState),
                        DataState.Success(allEventTicketsState),
                        { _, _ -> },
                        DataState.Loading,
                        {},
                        {},
                        DataState.Success(ticketsListedBySenderState),
                        {},
                        DataState.Loading,
                        {},
                        {},
                        Credentials.create("1234")
                    )
                }
            }
        }
    }
}