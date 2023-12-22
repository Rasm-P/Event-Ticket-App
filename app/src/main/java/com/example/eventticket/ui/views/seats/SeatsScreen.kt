package com.example.eventticket.ui.views.seats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eventticket.models.dto.EventVenueDTO
import com.example.eventticket.ui.theme.EventTicketCustomerTheme
import com.example.eventticket.ui.views.common.LoadingScreen
import com.example.eventticket.ui.views.common.NumbersBox
import com.example.eventticket.ui.views.common.TopBar
import com.example.eventticket.ui.views.purchase.PurchaseDialog
import com.example.eventticket.utils.DataState
import org.web3j.utils.Convert
import java.math.BigInteger

@Composable
fun SeatsScreen(
    event: EventVenueDTO?,
    fetchSeatsSoldForEvent: () -> Unit,
    seatsSoldForEventState: DataState<List<BigInteger>>?,
    navigateBack: () -> Unit,
    navigateEvents: () -> Unit,
    purchaseTickets: (eventId: BigInteger, seats: List<Int>, cost: BigInteger) -> Unit,
    purchaseTicketsState: DataState<String>?,
    updateWalletBalance: () -> Unit,
    ticketsOwnedByCustomer: DataState<List<BigInteger>>?,
    resetPurchaseState: () -> Unit,
    accountBalance: DataState<BigInteger>?,
    areContractsApproved: () -> Unit,
    setContractApprovals: () -> Unit,
    contractsApprovedState: DataState<Boolean>?,
    setContractsApprovedState: DataState<String>?
) {
    if (event != null) {
        LaunchedEffect(event) {
            fetchSeatsSoldForEvent()
        }
        when {
            seatsSoldForEventState is DataState.Loading || ticketsOwnedByCustomer is DataState.Loading -> {
                LoadingScreen()
            }
            seatsSoldForEventState is DataState.Success && ticketsOwnedByCustomer is DataState.Success-> {
                val selectedSeats = remember { mutableStateListOf<Int>() }
                var openPurchaseDialog by remember { mutableStateOf(false) }
                val numberOfSeats = event.numberOfTickets.toInt()
                val seatLimit = event.ticketsPrCustomer.toInt()
                val totalPrice = event.ticketPrice * selectedSeats.size.toBigInteger()
                val convertedPrice = Convert.fromWei(event.ticketPrice.toString(),Convert.Unit.ETHER)
                val convertedTotal = Convert.fromWei(totalPrice.toString(),Convert.Unit.ETHER)
                val isOverTicketLimit = event.ticketsPrCustomer.toInt() < ticketsOwnedByCustomer.data.size + selectedSeats.size

                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(text = event.eventName,
                                        style = MaterialTheme.typography.bodyMedium)
                                    Text(text = event.eventLocation,
                                        style = MaterialTheme.typography.labelSmall)
                                }
                                Column {
                                    Text(text = event.eventDate,
                                        style = MaterialTheme.typography.labelSmall)
                                    Text(text = event.eventTime,
                                        style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Row {
                                    NumbersBox(event.numberOfTickets.toString(), "Total seats")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    NumbersBox(event.ticketsLeft.toString(), "Tickets left")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    NumbersBox(convertedPrice.toString(), "Price MATIC")
                                }
                                Row {
                                    Text(
                                        text = "Ticket limit: ",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = event.ticketsPrCustomer.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Pick a seat from the venue layout")
                            Row {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Taken: ",
                                        style = MaterialTheme.typography.labelSmall)
                                    Box(modifier = Modifier
                                        .size(10.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.tertiary,
                                            shape = CircleShape
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Free: ",
                                        style = MaterialTheme.typography.labelSmall)
                                    Box(modifier = Modifier
                                        .size(10.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = CircleShape
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "You: ",
                                        style = MaterialTheme.typography.labelSmall)
                                    Box(modifier = Modifier
                                        .size(10.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        )
                                    )
                                }
                            }
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            items(count = (numberOfSeats + 5) / 6) { rowIndex ->
                                val rowStart = rowIndex * 6
                                val rowEnd = minOf(rowStart + 6, numberOfSeats)
                                val rowData = (rowStart until rowEnd).toList()
                                EventRow(
                                    rowData,
                                    selectedSeats,
                                    { int -> selectedSeats.add(int) },
                                    { int -> selectedSeats.remove(int) },
                                    seatLimit,
                                    seatsSoldForEventState.data,
                                    ticketsOwnedByCustomer.data
                                )
                            }
                        }
                        Column {
                            Row {
                                Text(text = "Tickets chosen: ",
                                    style = MaterialTheme.typography.bodyMedium)
                                Text(text = selectedSeats.size.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                            Row {
                                Text(text = "Total price: ",
                                    style = MaterialTheme.typography.bodyMedium)
                                Text(text = "$convertedTotal MATIC",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Column {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { openPurchaseDialog = true },
                                enabled = (totalPrice > BigInteger.ZERO && !isOverTicketLimit)) {
                                Text(text = if (!isOverTicketLimit) "Purchase seats" else "Limit exceeded")
                            }
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = navigateBack) {
                                Text(text = "Back")
                            }
                        }
                    }
                }
                if (openPurchaseDialog) {
                    PurchaseDialog(
                        eventName = event.eventName,
                        eventLocation = event.eventLocation,
                        eventDate = event.eventDate,
                        eventTime = event.eventTime,
                        seats = selectedSeats,
                        price = totalPrice,
                        onDismissRequest = {openPurchaseDialog = false; resetPurchaseState()},
                        purchaseTickets = { purchaseTickets(event.id,selectedSeats.toList(), totalPrice) },
                        purchaseTicketsState = purchaseTicketsState,
                        updateWalletBalance = {
                            updateWalletBalance()
                            navigateEvents()
                            resetPurchaseState()
                        },
                        accountBalance = accountBalance,
                        areContractsApproved = areContractsApproved,
                        setContractApprovals = setContractApprovals,
                        contractsApprovedState = contractsApprovedState,
                        setContractsApprovedState = setContractsApprovedState
                    )
                }
            }
            seatsSoldForEventState is DataState.Error || ticketsOwnedByCustomer is DataState.Error -> {
                Text(text = "The event seats could not be loaded!")
            }
            else -> {}
        }
    }
}

@Composable
fun EventRow(rowData: List<Int>, selectedSeats: MutableList<Int>, selectSeat: (Int) -> Unit, deSelectSeat: (Int) -> Unit, seatLimit: Int, seatsSoldForEventState: List<BigInteger>, ticketsOwnedByCustomer: List<BigInteger>) {
    Row {
        rowData.forEach { index ->
            Event(index, selectedSeats, selectSeat, deSelectSeat, seatLimit, seatsSoldForEventState, ticketsOwnedByCustomer)
        }
    }
}

@Composable
fun Event(seat: Int, selectedSeats: MutableList<Int>, selectSeat: (Int) -> Unit, deSelectSeat: (Int) -> Unit, seatLimit: Int, seatsSoldForEventState: List<BigInteger>, ticketsOwnedByCustomer: List<BigInteger>) {
    val isSelected = selectedSeats.contains(seat)
    val reachedSelectLimit = seatLimit <= selectedSeats.size
    val isTaken = seatsSoldForEventState.contains(seat.toBigInteger())
    val alreadyOwned = ticketsOwnedByCustomer.contains(seat.toBigInteger())
    Box(
        contentAlignment= Alignment.Center,
        modifier = Modifier
            .size(42.dp)
            .padding(5.dp)
            .background(
                color = if (alreadyOwned) MaterialTheme.colorScheme.primary else if (isTaken) MaterialTheme.colorScheme.tertiary else if (!isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Green,
                shape = CircleShape
            )
            .clickable {
                if (!isTaken) {
                    if (!isSelected && !reachedSelectLimit) selectSeat(seat) else deSelectSeat(seat)
                }
            },
    ){
        Text(text = seat.toString(),
            color = if (alreadyOwned || isTaken) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun SeatsScreenPreview() {
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
                topBar = { TopBar("Seats", DataState.Success(BigInteger.valueOf(1)), {}, {}) }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    SeatsScreen(
                        event,
                        {},
                        DataState.Success(listOf(BigInteger.valueOf(0), BigInteger.valueOf(1))),
                        {},
                        {},
                        { _, _, _ -> },
                        DataState.Loading,
                        {},
                        DataState.Success(listOf(BigInteger.valueOf(0))),
                        {},
                        DataState.Loading,
                        {},
                        {},
                        DataState.Loading,
                        DataState.Loading
                    )
                }
            }
        }
    }
}