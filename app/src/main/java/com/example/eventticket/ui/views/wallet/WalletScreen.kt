package com.example.eventticket.ui.views.wallet

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eventticket.ui.theme.EventTicketCustomerTheme
import com.example.eventticket.ui.views.common.LoadingScreen
import com.example.eventticket.utils.DataState
import com.example.eventticket.utils.isPasswordValid

@Composable
fun WalletScreen(
    unlockCreateWallet: (String, String, Boolean) -> Unit,
    navigateToEvents: () -> Unit,
    navigateToScan: () -> Unit,
    credentialsState: Boolean,
    credentialsError: String?,
    resetCredentialsState: () -> Unit,
    accessRoleState: DataState<String>?
) {
    var walletName by remember { mutableStateOf("test1") }
    var password by remember { mutableStateOf("HIUHiuhsiudhfi123123_") }
    var isVisible by remember { mutableStateOf(false) }
    var useAsOrganizer by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxWidth()
        .background(color = Color.White)
        .padding(16.dp)) {
        Box(modifier = Modifier
            .weight(0.4f),
            contentAlignment = Alignment.Center) {
            Text(textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineMedium, text = "Welcome to the world of NFT based event ticketing")
        }
        Card(modifier = Modifier
            .weight(0.6f)) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(16.dp),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                Text(textAlign = TextAlign.Center, style = MaterialTheme.typography.titleLarge, text = "Unlock or create a wallet")
                Text(textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, text = "Unlock or create a new wallet by using the wallet name and password fields below.")
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Column {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = walletName,
                        onValueChange = { walletName = it },
                        singleLine = true,
                        placeholder = { Text(text = "Wallet name") },
                        trailingIcon = {
                            if (walletName != "") {
                                IconButton(onClick = {
                                    walletName = ""
                                }, content = {
                                    Icon(Icons.Filled.Clear, "Clear")
                                }
                                )
                            }
                        })
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        placeholder = { Text(text = "Password") },
                        visualTransformation = if (!isVisible) PasswordVisualTransformation() else VisualTransformation.None,
                        trailingIcon = {
                            IconButton(onClick = {
                                isVisible = !isVisible
                            }, content = {
                                if (isVisible) {
                                    Icon(Icons.Filled.Visibility, "Visible")
                                } else {
                                    Icon(Icons.Filled.VisibilityOff, "Not visible")
                                }
                            }
                            )
                        })
                }
                    Spacer(modifier = Modifier.height(16.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    if (isPasswordValid(password)) {
                        unlockCreateWallet(password, walletName, useAsOrganizer)
                    } else {
                        Toast.makeText(
                            context,
                            "Password does not meet the requirements!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }) {
                    Text(text = "Unlock/Create")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(style = MaterialTheme.typography.labelLarge,text = "Use as organizer?")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = useAsOrganizer,
                        onCheckedChange = { useAsOrganizer = !useAsOrganizer })
                }

                }
            }
        }
    }
    if (credentialsError != null) {
        Toast.makeText(context, credentialsError, Toast.LENGTH_LONG).show()
        resetCredentialsState()
    }
    if (credentialsState) {
        if (useAsOrganizer) {
            when(accessRoleState) {
                is DataState.Loading -> {
                    LoadingScreen()
                }
                is DataState.Success -> {
                    if (accessRoleState.data == "Organizer") {
                        navigateToScan()
                    } else {
                        Toast.makeText(context, "Account does not have the role of Organizer!", Toast.LENGTH_LONG).show()
                        resetCredentialsState()
                    }
                }
                is DataState.Error -> {
                    Toast.makeText(context, accessRoleState.error, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        } else {
            navigateToEvents()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalletScreenPreview() {
    EventTicketCustomerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            WalletScreen({ _, _, _ -> }, {}, {}, false, null, {}, DataState.Loading)
        }
    }
}