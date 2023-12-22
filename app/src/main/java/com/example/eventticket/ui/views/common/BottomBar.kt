package com.example.eventticket.ui.views.common

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.eventticket.navigation.MenuDestination
import com.example.eventticket.navigation.bottomNavigation
import com.example.eventticket.ui.theme.EventTicketCustomerTheme

@Composable
fun BottomBar(onNavigate: (String) -> Unit, currentScreen: MenuDestination, navbarItems: List<MenuDestination>) {

    NavigationBar() {
        navbarItems.forEach{ screen ->
            NavigationBarItem(
                selected = currentScreen.route == screen.route,
                onClick = { onNavigate(screen.route) },
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.route) },
                label = {Text(text = screen.route)})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    EventTicketCustomerTheme {
        BottomBar({}, MenuDestination.Events, bottomNavigation)
    }
}