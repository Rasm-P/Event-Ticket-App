package com.example.eventticket.ui.views.common

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eventticket.navigation.bottomNavigation
import com.example.eventticket.navigation.organizerNavigation
import com.example.eventticket.ui.theme.EventTicketCustomerTheme
import com.example.eventticket.utils.DataState
import org.web3j.utils.Convert
import java.math.BigInteger
import java.math.RoundingMode

// Built with inspiration from Android developer docs: https://developer.android.com/jetpack/compose/components/app-bars (Accessed: 09-10-2023)

@Composable
fun TopBar(currentRoute: String, balance: DataState<BigInteger>?, onBack: () -> Unit, onLogout: () -> Unit) {

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
            Text(
                currentRoute,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (bottomNavigation.any { it.route == currentRoute } || organizerNavigation.any { it.route == currentRoute }) {
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Logout"
                    )
                }
            } else {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            when (balance) {
                is DataState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.width(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
                is DataState.Success -> {
                    val value = Convert.fromWei(balance.data.toString(), Convert.Unit.ETHER)
                    val threeDecimalBalance = value.setScale(3, RoundingMode.HALF_UP)
                    Text(style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, text = "$threeDecimalBalance MATIC")
                }
                is DataState.Error -> {
                    Text(text = "Could not load balance!")
                }
                else -> {}
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    EventTicketCustomerTheme {
        TopBar("Tickets", DataState.Success(BigInteger.valueOf(1)), {}, {})
    }
}