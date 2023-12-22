package com.example.eventticket.navigation

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.eventticket.ui.views.events.EventsScreen
import com.example.eventticket.ui.views.log.LogScreen
import com.example.eventticket.ui.views.resale.ResaleScreen
import com.example.eventticket.ui.views.scan.ScanScreen
import com.example.eventticket.ui.views.seats.SeatsScreen
import com.example.eventticket.ui.views.tickets.TicketsScreen
import com.example.eventticket.ui.views.wallet.WalletScreen
import com.example.eventticket.viewmodels.PurchaseViewModel
import com.example.eventticket.viewmodels.SeatsViewModel
import com.example.eventticket.viewmodels.EventsViewModel
import com.example.eventticket.viewmodels.LogViewModel
import com.example.eventticket.viewmodels.ResaleViewModel
import com.example.eventticket.viewmodels.ScanViewModel
import com.example.eventticket.viewmodels.TicketsViewModel
import com.example.eventticket.viewmodels.WalletViewModel

// Inspired by Android Codelabs https://developer.android.com/codelabs/jetpack-compose-navigation#0 (Accessed: 09-10-2023)

@OptIn(ExperimentalGetImage::class) @Composable
fun ApplicationNavHost(
    navController: NavHostController,
    modifier: Modifier,
    walletViewModel: WalletViewModel,
    eventsViewModel: EventsViewModel,
    seatsViewModel: SeatsViewModel,
    purchaseViewModel: PurchaseViewModel,
    ticketsViewModel: TicketsViewModel,
    resaleViewModel: ResaleViewModel,
    scanViewModel: ScanViewModel,
    logViewModel: LogViewModel
) {
    val startDestination = ApplicationFlows.WalletNavigationFlow.route
    val credentials = walletViewModel.credentialsState.value

    NavHost(navController = navController, startDestination = startDestination, modifier = modifier ) {
        navigation(route = ApplicationFlows.BottomNavigationFlow.route, startDestination = MenuDestination.Events.route) {
            composable(route = MenuDestination.Events.route) {
                EventsScreen(fetchAllEvents = {eventsViewModel.fetchAllEvents(credentials)},
                    allEventTicketsState = eventsViewModel.allEventTicketsState.value,
                    chooseEvent = {
                        event -> seatsViewModel.chooseEvent(event)
                        navController.navigateSingleTopTo(ApplicationFlows.SeatsNavigationFlow.route)
                    }
                )
            }
            composable(route = MenuDestination.Resale.route) {
                ResaleScreen(
                    fetchTicketsForResale = {resaleViewModel.fetchTicketsForResale(credentials)},
                    resaleListState = resaleViewModel.resaleListState.value,
                    purchaseTicketFromResale = {listingId, price -> resaleViewModel.purchaseTicketFromResale(credentials,listingId, price)},
                    purchaseResaleState = resaleViewModel.purchaseResaleState.value,
                    resetPurchaseResaleState = {resaleViewModel.resetPurchaseResaleState()},
                    updateWalletBalance = {walletViewModel.fetchAccountBalance()},
                    credentialsAddress = credentials!!.address,
                    accountBalance = walletViewModel.balanceState.value,
                    areContractsApproved = {purchaseViewModel.fetchContractsApproved(credentials)},
                    setContractApprovals = {purchaseViewModel.setContractApprovals(credentials)},
                    contractsApprovedState = purchaseViewModel.contractsApprovedState.value,
                    setContractsApprovedState = purchaseViewModel.setContractsApprovedState.value
                )
            }
            composable(route = MenuDestination.Tickets.route) {
                TicketsScreen(
                    fetchSenderNFTs = { ticketsViewModel.fetchSenderNFTs(credentials) },
                    senderNFTsState = ticketsViewModel.senderNFTsState.value,
                    allEventTicketsState = eventsViewModel.allEventTicketsState.value,
                    listTicketForSale = {ticketId, resalePrice -> resaleViewModel.listTicketForSale(credentials, ticketId, resalePrice)},
                    resellTicketsState = resaleViewModel.resellTicketsState.value,
                    resetResaleState = {resaleViewModel.resetListingState()},
                    fetchTicketsListedBySender = {resaleViewModel.fetchTicketsListedBySender(credentials)},
                    ticketsListedBySenderState = resaleViewModel.ticketsListedBySenderState.value,
                    withdrawFromResaleList = {listingId -> resaleViewModel.withdrawFromResaleList(credentials, listingId)},
                    withdrawFromResaleState = resaleViewModel.withdrawFromResaleState.value,
                    resetWithdrawResaleState = {resaleViewModel.resetWithdrawResaleState()},
                    updateWalletBalance = {walletViewModel.fetchAccountBalance()},
                    credentials = credentials,
                )
            }
        }
        composable(route = ApplicationFlows.WalletNavigationFlow.route) {
            WalletScreen(
                unlockCreateWallet = {password, fileName, asOrganizer -> walletViewModel.unlockCreateWallet(password, fileName, asOrganizer)},
                navigateToEvents = {navController.navigateSingleTopTo(ApplicationFlows.BottomNavigationFlow.route)},
                navigateToScan = {navController.navigateSingleTopTo(ApplicationFlows.OrganizerNavigationFlow.route)},
                credentialsState = credentials != null,
                credentialsError = walletViewModel.credentialsError.value,
                resetCredentialsState = {walletViewModel.resetCredentialsState()},
                accessRoleState = walletViewModel.accessRoleState.value
            )
        }
        composable(route = ApplicationFlows.SeatsNavigationFlow.route) {
            SeatsScreen(event = seatsViewModel.chosenEvent.value,
                fetchSeatsSoldForEvent = {seatsViewModel.fetchSeatsSoldForEvent(credentials)},
                seatsSoldForEventState = seatsViewModel.seatsSoldForEventState.value,
                navigateBack = {navController.popBackStack()},
                navigateEvents = {navController.navigateSingleTopTo(ApplicationFlows.BottomNavigationFlow.route)},
                purchaseTickets = {eventId, seats, price -> purchaseViewModel.purchaseTickets(credentials, eventId, seats, price)},
                purchaseTicketsState = purchaseViewModel.purchaseTicketsState.value,
                updateWalletBalance = {walletViewModel.fetchAccountBalance()},
                ticketsOwnedByCustomer = seatsViewModel.customerTicketsForEventState.value,
                resetPurchaseState = {purchaseViewModel.resetState()},
                accountBalance = walletViewModel.balanceState.value,
                areContractsApproved = {purchaseViewModel.fetchContractsApproved(credentials)},
                setContractApprovals = {purchaseViewModel.setContractApprovals(credentials)},
                contractsApprovedState = purchaseViewModel.contractsApprovedState.value,
                setContractsApprovedState = purchaseViewModel.setContractsApprovedState.value
            )
        }
        navigation(route = ApplicationFlows.OrganizerNavigationFlow.route, startDestination = MenuDestination.Scan.route) {
            composable(route = MenuDestination.Scan.route) {
                ScanScreen(registerTicket = {qrCodeMessage -> scanViewModel.registerTicket(credentials, qrCodeMessage)},
                    registerTicketState = scanViewModel.registerTicketState.value,
                    resetState = {scanViewModel.resetState()},
                    fetchAccountBalance = {walletViewModel.fetchAccountBalance()},
                    writeToLogFile = {logEntry -> logViewModel.writeToLogFile(logEntry)}
                )
            }
            composable(route = MenuDestination.Log.route) {
                LogScreen(readFromLogFile = {logViewModel.readFromLogFile()},
                    logFileReadState = logViewModel.logFileReadState.value)
            }
        }
    }
}

// Inspired by Android developer docs: https://developer.android.com/codelabs/jetpack-compose-navigation#4 (Accessed: 09-10-2023)
fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }