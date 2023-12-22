package com.example.eventticket.ui.views.present

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.graphics.Color
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.eventticket.R
import com.example.eventticket.models.dto.EventVenueDTO
import com.example.eventticket.models.dto.NFTDataDTO
import com.example.eventticket.ui.theme.EventTicketCustomerTheme
import com.example.eventticket.utils.signQrCodeMessage
import com.google.gson.Gson
import org.web3j.crypto.Credentials
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.util.zip.GZIPOutputStream

@Composable
fun PresentDialog(
    onDismissRequest: () -> Unit,
    eventData: Pair<NFTDataDTO, EventVenueDTO>,
    credentials: Credentials
) {
    val (nftData, eventVenue) = eventData
    val eventId = nftData.eventId
    val ticketId = nftData.tokenId
    var challenge by remember { mutableStateOf("") }
    var showQrCode by remember { mutableStateOf(false) }
    val length = 2

    Dialog(onDismissRequest = onDismissRequest) {
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )) {
            Column(modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ticketicon),
                        "Ticket",
                        modifier = Modifier
                            .size(110.dp)
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f), verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = eventVenue.eventName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = eventVenue.eventLocation,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Sear nr. " + nftData.seatNr.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = eventVenue.eventDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = eventVenue.eventTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Ticket ID: " + nftData.tokenId,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (showQrCode) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        text = "Present the QR-code to an event organizer to they can register your ticket."
                    )
                    QrCode(
                        eventId,
                        ticketId,
                        challenge,
                        credentials,
                        widthDp = 256.dp,
                        heightDp = 256.dp
                    )
                } else {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        text = "Enter the challenge given to you in the field below and press the button in order to show the resulting QR-code."
                    )
                    BasicTextField(
                        modifier = Modifier.padding(16.dp),
                        value = challenge,
                        singleLine = true,
                        onValueChange = {
                            if (it.length <= length) {
                                challenge = it
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        decorationBox = {
                            Row(modifier = Modifier.size(width = (32.dp + 8.dp) * length, height = 32.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                repeat(length) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp, 32.dp)
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(4.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = challenge.getOrNull(index)?.toString() ?: "",
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    if (!showQrCode) {
                        Button(modifier = Modifier.fillMaxWidth(),
                            onClick = { showQrCode = true },
                            enabled = challenge.length == 2) {
                            Text(text = "Show QR-code")
                        }
                    } else {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { showQrCode = false; challenge = "" }) {
                            Text(text = "Change challenge")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onDismissRequest) {
                        Text(text = "Back")
                    }
                }
            }
        }
    }
}

// Adapted from DEV article by Addy Godwin Nii: https://dev.to/devniiaddy/qr-code-with-jetpack-compose-47e (Accessed: 26-10-2023)
@Composable
fun QrCode(
    eventId: BigInteger,
    ticketId: BigInteger,
    challenge: String,
    credentials: Credentials,
    widthDp: Dp,
    heightDp: Dp
) {
    val qrCodeMessage = signQrCodeMessage(eventId, ticketId, challenge, credentials)
    val message = Gson().toJson(qrCodeMessage)
    val byteArrayOutputStream = ByteArrayOutputStream()
    GZIPOutputStream(byteArrayOutputStream).bufferedWriter().use { it.write(message) }
    val out = byteArrayOutputStream.toByteArray()
    val encoded = Base64.encodeToString(out,Base64.DEFAULT)

    var qrCodeBitmapImage by remember { mutableStateOf<ImageBitmap?>(null) }
    val context = LocalContext.current
    val screenDensity = LocalDensity.current
    val width = with(screenDensity) {widthDp.roundToPx()}
    val height = with(screenDensity) {heightDp.roundToPx()}

    LaunchedEffect(qrCodeBitmapImage) {
        launch(Dispatchers.IO) {
            val bitMatrix = try {
                QRCodeWriter().encode(
                    encoded,
                    BarcodeFormat.QR_CODE,
                    width,
                    height
                )
            } catch (e: Exception) {
                Toast.makeText(context, "QR-code could not be presented!", Toast.LENGTH_SHORT).show()
                null
            }
            val bitMap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            )
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val color = if (bitMatrix?.get(x,y) == true)  Color.BLACK else Color.WHITE
                    bitMap.setPixel(x,y,color)
                }
            }
            qrCodeBitmapImage = bitMap.asImageBitmap()
        }
    }

    Box {
        if (qrCodeBitmapImage != null) {
            Image(
                painter = BitmapPainter(qrCodeBitmapImage!!),
                contentDescription = "Qr-Code"
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PresentDialogPreview() {
    val nftData = NFTDataDTO(BigInteger.valueOf(1),BigInteger.valueOf(1),BigInteger.valueOf(1),false)
    val eventData = EventVenueDTO(
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
        PresentDialog({},Pair(nftData, eventData), Credentials.create("1234"))
    }
}