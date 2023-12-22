package com.example.eventticket.utils

fun isPasswordValid(password: String): Boolean {
    return password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^_&+=])(?=\\S+$).{8,}$"))
}