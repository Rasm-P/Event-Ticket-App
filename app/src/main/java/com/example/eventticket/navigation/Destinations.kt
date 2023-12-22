package com.example.eventticket.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MenuDestination(
    val route: String,
    val icon: ImageVector
) {
    object Events: MenuDestination (
        "Events",
        Icons.Filled.Stadium
    )
    object Resale: MenuDestination (
        "Resale",
        Icons.Filled.ShoppingBag
    )
    object Tickets: MenuDestination (
        "Tickets",
        Icons.Filled.ConfirmationNumber
    )
    object Scan: MenuDestination (
        "Scan",
        Icons.Filled.QrCodeScanner
    )
    object Log: MenuDestination (
        "Log",
        Icons.Filled.FormatListBulleted
    )
}

sealed class ApplicationFlows(
    val route: String
) {
    object BottomNavigationFlow : ApplicationFlows("BottomNav")
    object WalletNavigationFlow : ApplicationFlows("Wallet")
    object SeatsNavigationFlow : ApplicationFlows("Seats")
    object OrganizerNavigationFlow : ApplicationFlows("Organizer")
}

val bottomNavigation = listOf(MenuDestination.Events, MenuDestination.Resale, MenuDestination.Tickets)
val organizerNavigation = listOf(MenuDestination.Scan, MenuDestination.Log)

fun partOfBottomNavigation(route: String): Boolean {
    return bottomNavigation.any { i -> i.route == route }
}

fun partOfOrganizerNavigation(route: String): Boolean {
    return organizerNavigation.any { i -> i.route == route }
}