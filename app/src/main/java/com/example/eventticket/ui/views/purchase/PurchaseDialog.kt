package com.example.eventticket.ui.views.purchase

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.eventticket.R
import com.example.eventticket.ui.theme.EventTicketCustomerTheme
import com.example.eventticket.ui.views.common.LoadingScreen
import com.example.eventticket.utils.DataState
import org.web3j.utils.Convert
import java.math.BigInteger

@Composable
fun PurchaseDialog(
    eventName: String,
    eventLocation: String,
    eventDate: String,
    eventTime: String,
    seats: List<Int>,
    price: BigInteger,
    onDismissRequest: () -> Unit,
    purchaseTickets: () -> Unit,
    purchaseTicketsState: DataState<String>?,
    updateWalletBalance: () -> Unit,
    accountBalance: DataState<BigInteger>?,
    areContractsApproved: () -> Unit,
    setContractApprovals: () -> Unit,
    contractsApprovedState: DataState<Boolean>?,
    setContractsApprovedState: DataState<String>?
) {
    Dialog(onDismissRequest = onDismissRequest) {
        LaunchedEffect(Unit) {
            areContractsApproved()
        }
        Card(modifier = Modifier
            .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )) {
            when (contractsApprovedState) {
                is DataState.Loading -> {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                is DataState.Success -> {
                    if (contractsApprovedState.data) {
                        PurchaseFlow(
                            eventName,
                            eventLocation,
                            eventDate,
                            eventTime,
                            seats,
                            price,
                            onDismissRequest,
                            purchaseTickets,
                            purchaseTicketsState,
                            updateWalletBalance,
                            accountBalance,
                        )
                    } else  {
                        AcceptContractApproval(
                            setContractApprovals,
                            setContractsApprovedState,
                            onDismissRequest
                        )
                    }
                }
                is DataState.Error -> {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        contentAlignment = Alignment.Center) {
                        Text(text = contractsApprovedState.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun PurchaseFlow(
    eventName: String,
    eventLocation: String,
    eventDate: String,
    eventTime: String,
    seats: List<Int>,
    price: BigInteger,
    onDismissRequest: () -> Unit,
    purchaseTickets: () -> Unit,
    purchaseTicketsState: DataState<String>?,
    updateWalletBalance: () -> Unit,
    accountBalance: DataState<BigInteger>?,
) {
    val joinedString = seats.joinToString(", ")
    val convertedPrice = Convert.fromWei(price.toString(), Convert.Unit.ETHER)
    val context = LocalContext.current

    Box(modifier = Modifier
        .height(IntrinsicSize.Min)
        .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    Column {
                        Text(
                            text = eventName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = eventLocation,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = eventDate,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = eventTime,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Text(
                    text = "Seat nr: $joinedString",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(
                    text = "Total price: ",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "$convertedPrice MATIC",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.ticketicon),
                "Ticket",
                modifier = Modifier.size(160.dp)
            )
            Text(
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                text = "Once you have bought a ticket, you will be able to see it on the Tickets screen.",
                style = MaterialTheme.typography.bodySmall
            )
            Button(modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (accountBalance is DataState.Success && accountBalance.data > price) {
                        purchaseTickets()
                    } else {
                        Toast.makeText(context, "Not enough funds on account!", Toast.LENGTH_LONG)
                            .show()
                    }
                }) {
                Text(text = "Make purchase")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismissRequest
            ) {
                Text(text = "Back")
            }
        }
        when (purchaseTicketsState) {
            is DataState.Loading -> {
                LoadingScreen()
            }
            is DataState.Success -> {
                Toast.makeText(
                    context,
                    "Transaction: " + purchaseTicketsState.data,
                    Toast.LENGTH_LONG
                ).show()
                updateWalletBalance()
            }
            is DataState.Error -> {
                Toast.makeText(context, "Error: " + purchaseTicketsState.error, Toast.LENGTH_LONG)
                    .show()
            }
            else -> {}
        }
    }
}

@Composable
fun AcceptContractApproval(
    setContractApprovals: () -> Unit,
    setContractsApprovedState: DataState<String>?,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier
        .height(IntrinsicSize.Min)
        .fillMaxWidth(),
        ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "In order to purchase and resell event tickets, you must first accept the following terms. "
                        + "Smart contracts deployed on the blockchain has the right to transfer event ticket tokens on my behalf. "
                        + "This is done in order to secure safe token transactions, registration and validation. "
                        + "By using the button below i approve and accept these terms."
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = setContractApprovals
            ) {
                Text(text = "Accept")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismissRequest
            ) {
                Text(text = "Back")
            }
        }
        when (setContractsApprovedState) {
            is DataState.Loading -> {
                LoadingScreen()
            }

            is DataState.Error -> {
                Toast.makeText(
                    context,
                    "Error: " + setContractsApprovedState.error,
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResaleDialogPreview() {
    EventTicketCustomerTheme {
        PurchaseDialog(
            "Live at The Venue1",
            "Copenhagen, Denmark",
            "Jan 1, 2023",
            "3:00pm to 8:00pm",
            listOf(1,2),
            BigInteger.valueOf(1),
            {},
            {},
            DataState.Loading,
            {},
            DataState.Loading,
            {},
            {},
            DataState.Success(true),
            DataState.Loading
        )
    }
}