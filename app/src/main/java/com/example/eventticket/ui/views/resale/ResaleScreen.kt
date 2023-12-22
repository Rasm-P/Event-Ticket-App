package com.example.eventticket.ui.views.resale

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eventticket.models.dto.ListedTicketDTO
import com.example.eventticket.navigation.MenuDestination
import com.example.eventticket.navigation.bottomNavigation
import com.example.eventticket.ui.theme.EventTicketCustomerTheme
import com.example.eventticket.ui.views.common.BottomBar
import com.example.eventticket.ui.views.common.LoadingScreen
import com.example.eventticket.ui.views.common.TopBar
import com.example.eventticket.ui.views.purchase.PurchaseDialog
import com.example.eventticket.utils.DataState
import org.web3j.utils.Convert
import java.math.BigInteger

@Composable
fun ResaleScreen(
    fetchTicketsForResale: () -> Unit,
    resaleListState: DataState<List<ListedTicketDTO>>?,
    purchaseTicketFromResale: (BigInteger, BigInteger) -> Unit,
    purchaseResaleState: DataState<String>?,
    resetPurchaseResaleState: () -> Unit,
    updateWalletBalance: () -> Unit,
    credentialsAddress: String,
    accountBalance: DataState<BigInteger>?,
    areContractsApproved: () -> Unit,
    setContractApprovals: () -> Unit,
    contractsApprovedState: DataState<Boolean>?,
    setContractsApprovedState: DataState<String>?
) {
    LaunchedEffect(Unit) {
        fetchTicketsForResale()
    }
    when (resaleListState) {
        is DataState.Loading -> {
            LoadingScreen()
        }
        is DataState.Success -> {
            val resaleTickets = resaleListState.data.filter { it.listingOwner != credentialsAddress}
            var selectedTicket by remember { mutableStateOf<ListedTicketDTO?>(null) }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(resaleTickets) { listing ->
                    ResaleTicketCard(
                        listing = listing,
                        setPurchaseTicketFromResale = {selectedTicket = listing},
                    )
                }
            }
            if (selectedTicket != null) {
                PurchaseDialog(
                    eventName = selectedTicket!!.eventName,
                    eventLocation = selectedTicket!!.eventDate,
                    eventDate = selectedTicket!!.eventDate,
                    eventTime = selectedTicket!!.eventTime,
                    seats = listOf(selectedTicket!!.seatNr.toInt()),
                    price = selectedTicket!!.listingPrice,
                    onDismissRequest = {
                        selectedTicket = null
                        resetPurchaseResaleState()
                    },
                    purchaseTickets = {purchaseTicketFromResale(selectedTicket!!.listingId, selectedTicket!!.listingPrice)},
                    purchaseTicketsState = purchaseResaleState,
                    updateWalletBalance = {
                        fetchTicketsForResale()
                        updateWalletBalance()
                        selectedTicket = null
                        resetPurchaseResaleState()
                    },
                    accountBalance = accountBalance,
                    areContractsApproved = areContractsApproved,
                    setContractApprovals = setContractApprovals,
                    contractsApprovedState = contractsApprovedState,
                    setContractsApprovedState = setContractsApprovedState
                )
            }
        }
        is DataState.Error -> {
            Text(text = resaleListState.error)
        }
        else -> {}
    }
}

@Composable
fun ResaleTicketCard(
    listing: ListedTicketDTO,
    setPurchaseTicketFromResale: () -> Unit,
) {
    val price = Convert.fromWei(listing.listingPrice.toString(), Convert.Unit.ETHER)
    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        onClick = setPurchaseTicketFromResale) {
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = "Seat nr. " + listing.seatNr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary)
                Text(text = listing.eventDate,
                    style = MaterialTheme.typography.labelSmall)
            }
            Column {
                Text(text = listing.eventName,
                    style = MaterialTheme.typography.bodyMedium)
                Text(text = listing.eventLocation,
                    style = MaterialTheme.typography.labelSmall)
            }
            Text(text = "$price MATIC",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResaleScreenPreview() {
    val resaleListState = listOf(ListedTicketDTO(
            BigInteger.valueOf(1),
            "test",
            BigInteger.valueOf(1),
            eventId = BigInteger.valueOf(1),
            seatNr = BigInteger.valueOf(1),
            eventName = "Live at The Venue1",
            eventLocation = "Copenhagen, Denmark",
            eventDate = "Jan 1, 2023",
            eventTime = "3:00pm to 8:00pm"),
        ListedTicketDTO(
            BigInteger.valueOf(1),
            "test",
            BigInteger.valueOf(1),
            eventId = BigInteger.valueOf(1),
            seatNr = BigInteger.valueOf(1),
            eventName = "Live at The Venue1",
            eventLocation = "Copenhagen, Denmark",
            eventDate = "Jan 1, 2023",
            eventTime = "3:00pm to 8:00pm"))
    EventTicketCustomerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = { TopBar("Resale", DataState.Success(BigInteger.valueOf(1)), {}, {}) },
                bottomBar = { BottomBar({}, MenuDestination.Tickets, bottomNavigation) }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    ResaleScreen(
                        {},
                        DataState.Success(resaleListState),
                        {_,_->},
                        DataState.Loading,
                        {},
                        {},
                        "",
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