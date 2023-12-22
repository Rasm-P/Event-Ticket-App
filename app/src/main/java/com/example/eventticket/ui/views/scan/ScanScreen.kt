package com.example.eventticket.ui.views.scan

import android.Manifest
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.eventticket.models.LogEntry
import com.example.eventticket.models.QrCodeMessage
import com.example.eventticket.utils.CodeAnalyser
import com.example.eventticket.utils.DataState
import com.google.gson.Gson
import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.zip.GZIPInputStream

// Adapted from Dev Genius article by Hyzam Ali: https://blog.devgenius.io/qr-code-scanner-with-jetpack-compose-camerax-and-ml-kit-8e5a1d4a2fc9 (Accessed: 25-10-2023)

@androidx.camera.core.ExperimentalGetImage
@Composable
fun ScanScreen(
    registerTicket: (QrCodeMessage) -> Unit,
    registerTicketState: DataState<String>?,
    resetState: () -> Unit,
    fetchAccountBalance: () -> Unit,
    writeToLogFile: (LogEntry) -> Unit
) {
    val context = LocalContext.current
    val currentPermission = ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA)
    var cameraPermission by remember {mutableStateOf(currentPermission == PackageManager.PERMISSION_GRANTED)}
    var qrCodeDiscovered by remember {mutableStateOf<QrCodeMessage?>(null)}
    var number by remember { mutableIntStateOf(generateRandomNumber()) }

    val launcherForActivityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {result -> cameraPermission = result}
    )

    LaunchedEffect(true) {
        launcherForActivityResult.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    val executor = Executors.newSingleThreadExecutor()
                    val previewView = PreviewView(context)
                    val providerFuture = ProcessCameraProvider.getInstance(context)
                    providerFuture.addListener({
                        val provider = providerFuture.get()
                        val preview = Preview.Builder().build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val imageCapture = ImageCapture.Builder().build()
                        val imageAnalysis = ImageAnalysis.Builder().build()
                        imageAnalysis.setAnalyzer(
                            executor,
                            CodeAnalyser { str ->
                                if (qrCodeDiscovered == null) {
                                    try {
                                        val decoded = Base64.decode(str, Base64.DEFAULT)
                                        val byteArrayInputStream = ByteArrayInputStream(decoded)
                                        val gzipInputStream =
                                            GZIPInputStream(byteArrayInputStream)
                                        val out = gzipInputStream.bufferedReader()
                                            .use { it.readText() }
                                        val qrCode =
                                            Gson().fromJson(out, QrCodeMessage::class.java)
                                        if (qrCode.c.toInt() == number) {
                                            qrCodeDiscovered = qrCode
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "The challenge number was wrong!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.d("CameraScan", e.message!!)
                                        Toast.makeText(
                                            context,
                                            "QR-code data could not be decoded!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        )
                        val selector = CameraSelector.DEFAULT_BACK_CAMERA
                        val componentActivity = context as ComponentActivity
                        try {
                            provider.unbindAll()
                            provider.bindToLifecycle(
                                componentActivity,
                                selector,
                                preview,
                                imageCapture,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            Log.d("CameraScan", e.message!!)
                        }
                    }, ContextCompat.getMainExecutor(context))
                previewView
            })
            QrScannerOverlay()
            ChallengeOverlay(number) { number = generateRandomNumber() }
            if (qrCodeDiscovered != null) {
                RegisterTicketOverlay(
                    eventId = qrCodeDiscovered!!.e,
                    ticketId = qrCodeDiscovered!!.t,
                    registerTicket = {registerTicket(qrCodeDiscovered!!)},
                    registerTicketState = registerTicketState,
                    dismissOverlay = {qrCodeDiscovered = null; resetState(); number = generateRandomNumber()},
                    fetchAccountBalance = fetchAccountBalance,
                    writeToLogFile = writeToLogFile
                )
            }
        }
    }
}

@Composable
fun QrScannerOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val rectWidth = size.width * 0.7f
        val rectHeight = size.width * 0.7f
        val left = (size.width - rectWidth) / 2
        val top = (size.height - rectHeight) / 2
        val right = left + rectWidth
        val bottom = top + rectHeight
        val alpha = 0.5f
        val borderColor = Color.White
        val borderWidth = 4.dp
        val cornerRadius = 20.dp

        clipPath(Path().apply {
            addRoundRect(RoundRect(left, top, right, bottom, cornerRadius = CornerRadius(cornerRadius.toPx())))},
            clipOp = ClipOp.Difference
        ) {
            drawRect(SolidColor(Color.Black.copy(alpha = alpha)))
        }

        drawRoundRect(
            color = borderColor,
            style = Stroke(width = borderWidth.toPx()),
            topLeft = Offset(left, top),
            size = Size(rectWidth, rectHeight),
            cornerRadius = CornerRadius(cornerRadius.toPx())
        )
    }
}

@Composable
fun RegisterTicketOverlay(
    eventId: BigInteger,
    ticketId: BigInteger,
    registerTicket: () -> Unit,
    registerTicketState: DataState<String>?,
    dismissOverlay: () -> Unit,
    fetchAccountBalance: () -> Unit,
    writeToLogFile: (LogEntry) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary))
        {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Event ID: $eventId")
                        Text(text = "Ticket ID: $ticketId")
                    }
                    Row {
                        OutlinedButton(onClick = dismissOverlay) {
                            Text(text = "Dismiss")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = registerTicket) {
                            Text(text = "Register")
                        }
                    }
                }
                when (registerTicketState) {
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
                            "Transaction: " + registerTicketState.data,
                            Toast.LENGTH_LONG
                        ).show()
                        writeToLogFile(
                            LogEntry(
                                ticketId,
                                getLocalDateTime(),
                                registerTicketState.data
                            )
                        )
                        fetchAccountBalance()
                        dismissOverlay()
                    }
                    is DataState.Error -> {
                        Toast.makeText(
                            LocalContext.current,
                            "Error: " + registerTicketState.error,
                            Toast.LENGTH_LONG
                        ).show()
                        fetchAccountBalance()
                        dismissOverlay()
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun ChallengeOverlay(number: Int, onPressed: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(modifier = Modifier
            .padding(16.dp)
            .align(Alignment.TopCenter)
            .clickable { onPressed() },
            color = MaterialTheme.colorScheme.onPrimary,
            text = "Challenge: $number",
            style = MaterialTheme.typography.displaySmall
        )
    }
}

private fun generateRandomNumber(): Int {
    return (0..99).random()
}

private fun getLocalDateTime(): String {
    val localDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")
    return localDateTime.format(formatter)
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun ScanScreenPreview() {
    RegisterTicketOverlay(
        BigInteger.valueOf(1),
        BigInteger.valueOf(1),
        {},
        DataState.Loading,
        {},
        {},
        {},
    )
}