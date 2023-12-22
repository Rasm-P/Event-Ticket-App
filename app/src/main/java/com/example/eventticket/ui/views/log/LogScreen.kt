package com.example.eventticket.ui.views.log

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.eventticket.models.LogEntry
import com.example.eventticket.navigation.MenuDestination
import com.example.eventticket.navigation.bottomNavigation
import com.example.eventticket.ui.theme.EventTicketCustomerTheme
import com.example.eventticket.ui.views.common.BottomBar
import com.example.eventticket.ui.views.common.LoadingScreen
import com.example.eventticket.ui.views.common.TopBar
import com.example.eventticket.utils.DataState
import java.math.BigInteger

@Composable
fun LogScreen(
    readFromLogFile: () -> Unit,
    logFileReadState: DataState<List<LogEntry>>?
) {
    LaunchedEffect(Unit) {
        readFromLogFile()
    }
    when(logFileReadState) {
        is DataState.Loading -> {
            LoadingScreen()
        }
        is DataState.Success -> {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(logFileReadState.data) { index, logEntry -> LogScreenEntry(index, logEntry)
                }
            }
        }
        is DataState.Error -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Text(text = logFileReadState.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        else -> {}
    }
}

@Composable
fun LogScreenEntry(index: Int, logEntry: LogEntry) {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = (index+1).toString() + ".",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Text(text = "Ticket ID: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = logEntry.ticketId.toString(),
                        style = MaterialTheme.typography.labelSmall)
                }
                Row {
                    Text(text = "Date: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = logEntry.dateTime,
                        style = MaterialTheme.typography.labelSmall)
                }
            }
            Row {
                Text(text = "Transaction: ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(modifier = Modifier.clickable {
                    val url = "https://mumbai.polygonscan.com/tx/" + logEntry.transactionHash
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)},
                    text = logEntry.transactionHash,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketsScreenPreview() {
    val logFileReadState = listOf(
        LogEntry(BigInteger.valueOf(21),"05.10.2023, 03:45pm","0xa3f10ae0a2aaeb7fce1c66338940360641eddea0816fb724ca769c618b64153a"),
        LogEntry(BigInteger.valueOf(22),"05.10.2023, 03:46pm","0xa3f10ae0a2aaeb7fce1c66338940360641eddea0816fb724ca769c618b64153a")
        )
    EventTicketCustomerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = { TopBar("Log", DataState.Success(BigInteger.valueOf(1)), {}, {}) },
                bottomBar = { BottomBar({}, MenuDestination.Tickets, bottomNavigation) }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    LogScreen(
                        {},
                        DataState.Success(logFileReadState)
                    )
                }
            }
        }
    }
}