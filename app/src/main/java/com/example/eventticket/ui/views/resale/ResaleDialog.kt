package com.example.eventticket.ui.views.resale

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.eventticket.R
import com.example.eventticket.models.dto.EventVenueDTO
import com.example.eventticket.models.dto.NFTDataDTO
import com.example.eventticket.ui.theme.EventTicketCustomerTheme
import com.example.eventticket.ui.views.common.LoadingScreen
import com.example.eventticket.utils.DataState
import org.web3j.utils.Convert
import java.math.BigInteger

@Composable
fun ResaleDialog(
    event: EventVenueDTO,
    ticket: NFTDataDTO,
    onDismissRequest: () -> Unit,
    listTicketForSale: (BigInteger, BigInteger) -> Unit,
    ticketResaleState: DataState<String>?,
    fetchNFTsAndListings: () -> Unit) {
    val nftPurchasePrice = Convert.fromWei(event.ticketPrice.toString(), Convert.Unit.ETHER)
    var resalePrice by remember { mutableStateOf(nftPurchasePrice.toString()) }
    var higherThanPurchasePrice by remember { mutableStateOf(false)}
    var lowerThanOneWei by remember { mutableStateOf(false)}

    Dialog(onDismissRequest = onDismissRequest) {
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )) {
            Box(modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()) {
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
                                    text = event.eventName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = event.eventLocation,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = event.eventDate,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = event.eventTime,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        Box(modifier = Modifier.fillMaxHeight()) {
                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Seat nr: ${ticket.seatNr}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row {
                                    Text(
                                        text = "Ticket ID: ",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = ticket.tokenId.toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ticketicon),
                        "Ticket",
                        modifier = Modifier.size(130.dp)
                    )
                    Text(
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        text = "Tickets can be put up for resale at NO MORE than the original purchase price.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = resalePrice,
                        onValueChange = {
                            if (it.matches(Regex("""^(\d+\.?\d*)?$"""))) {
                                resalePrice = it
                                if (higherThanPurchasePrice || lowerThanOneWei) {
                                    higherThanPurchasePrice = false
                                    lowerThanOneWei = false
                                }
                            }
                        },
                        label = { Text("Resale price: MATIC") },
                        trailingIcon = { Icons.Filled.Edit },
                        isError = higherThanPurchasePrice,
                        supportingText = {
                            if (higherThanPurchasePrice) {
                                Text(
                                    text = "Resale price is too high!",
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else if (lowerThanOneWei) {
                                Text(
                                    text = "Resale price is too low!",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val price =
                                Convert.toWei(resalePrice, Convert.Unit.ETHER).toBigInteger()
                            if (price > event.ticketPrice) {
                                higherThanPurchasePrice = true
                            } else if (price < BigInteger.ONE) {
                                lowerThanOneWei = true
                            } else {
                                listTicketForSale(ticket.tokenId, price)
                            }
                        }) {
                        Text(text = "Put up for resale")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onDismissRequest
                    ) {
                        Text(text = "Back")
                    }
                }
                when (ticketResaleState) {
                    is DataState.Loading -> {
                        LoadingScreen()
                    }

                    is DataState.Success -> {
                        Toast.makeText(
                            LocalContext.current,
                            "Transaction: " + ticketResaleState.data,
                            Toast.LENGTH_LONG
                        ).show()
                        fetchNFTsAndListings()
                        onDismissRequest()
                    }

                    is DataState.Error -> {
                        Toast.makeText(
                            LocalContext.current,
                            "Error: " + ticketResaleState.error,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResaleDialogPreview() {
    val ticket = NFTDataDTO(BigInteger.valueOf(1),BigInteger.valueOf(1),BigInteger.valueOf(1),false)
    val event = EventVenueDTO(
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
    )
    EventTicketCustomerTheme {
        ResaleDialog(
            event,
            ticket,
            {},
            {_,_->},
            DataState.Loading,
            {}
        )
    }
}