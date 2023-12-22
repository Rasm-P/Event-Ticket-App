package com.example.eventticket

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eventticket.navigation.ApplicationFlows
import com.example.eventticket.navigation.ApplicationNavHost
import com.example.eventticket.navigation.MenuDestination
import com.example.eventticket.navigation.bottomNavigation
import com.example.eventticket.navigation.navigateSingleTopTo
import com.example.eventticket.navigation.organizerNavigation
import com.example.eventticket.navigation.partOfBottomNavigation
import com.example.eventticket.navigation.partOfOrganizerNavigation
import com.example.eventticket.ui.theme.EventTicketCustomerTheme
import com.example.eventticket.ui.views.common.BottomBar
import com.example.eventticket.ui.views.common.TopBar
import com.example.eventticket.utils.setupBouncyCastle
import com.example.eventticket.viewmodels.PurchaseViewModel
import com.example.eventticket.viewmodels.SeatsViewModel
import com.example.eventticket.viewmodels.EventsViewModel
import com.example.eventticket.viewmodels.LogViewModel
import com.example.eventticket.viewmodels.ResaleViewModel
import com.example.eventticket.viewmodels.ScanViewModel
import com.example.eventticket.viewmodels.TicketsViewModel
import com.example.eventticket.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val walletViewModel: WalletViewModel by viewModels()
    private val eventsViewModel: EventsViewModel by viewModels()
    private val seatsViewModel: SeatsViewModel by viewModels()
    private val purchaseViewModel: PurchaseViewModel by viewModels()
    private val ticketsViewModel: TicketsViewModel by viewModels()
    private val resaleViewModel: ResaleViewModel by viewModels()
    private val scanViewModel: ScanViewModel by viewModels()
    private val logViewModel: LogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setupBouncyCastle()
        setContent {
            EventTicketApp(
                walletViewModel,
                eventsViewModel,
                seatsViewModel,
                purchaseViewModel,
                ticketsViewModel,
                resaleViewModel,
                scanViewModel,
                logViewModel
            )
        }
    }
}

@Composable
fun EventTicketApp(
    walletViewModel: WalletViewModel,
    eventsViewModel:EventsViewModel,
    seatsViewModel: SeatsViewModel,
    purchaseViewModel: PurchaseViewModel,
    ticketsViewModel: TicketsViewModel,
    resaleViewModel: ResaleViewModel,
    scanViewModel: ScanViewModel,
    logViewModel: LogViewModel
) {
    EventTicketCustomerTheme {
        val navController = rememberNavController()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route ?: ApplicationFlows.WalletNavigationFlow.route
        val currentBottomBarScreen = bottomNavigation.find { it.route == currentRoute } ?: MenuDestination.Events
        val currentOrganizerBarScreen = organizerNavigation.find { it.route == currentRoute } ?: MenuDestination.Scan

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    if (currentRoute != ApplicationFlows.WalletNavigationFlow.route) {
                        TopBar(
                            currentRoute = currentRoute,
                            balance = walletViewModel.balanceState.value,
                            onBack = { navController.popBackStack() },
                            onLogout = {
                                walletViewModel.logOut()
                                navController.navigate(
                                    route = ApplicationFlows.WalletNavigationFlow.route,
                                    builder = {
                                        popUpTo(ApplicationFlows.WalletNavigationFlow.route) {
                                            inclusive = true
                                        }
                                    }
                                )
                            }
                        )
                    }
                },
                bottomBar = {
                    if (partOfBottomNavigation(currentRoute)) {
                        BottomBar(
                            onNavigate = {screen -> navController.navigateSingleTopTo(screen) },
                            currentScreen = currentBottomBarScreen,
                            navbarItems = bottomNavigation)
                    } else if (partOfOrganizerNavigation(currentRoute)) {
                        BottomBar(
                            onNavigate = {screen -> navController.navigateSingleTopTo(screen) },
                            currentScreen = currentOrganizerBarScreen,
                            navbarItems = organizerNavigation)
                    }
                }
            ) {
                padding -> ApplicationNavHost(
                    navController = navController,
                    modifier = Modifier.padding(padding),
                    walletViewModel = walletViewModel,
                    eventsViewModel = eventsViewModel,
                    seatsViewModel = seatsViewModel,
                    purchaseViewModel = purchaseViewModel,
                    ticketsViewModel = ticketsViewModel,
                    resaleViewModel = resaleViewModel,
                    scanViewModel = scanViewModel,
                    logViewModel = logViewModel
                )
            }
        }
    }
}
