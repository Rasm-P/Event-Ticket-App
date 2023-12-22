package com.example.eventticket.utils

// Built with inspiration from Medium CodeX article by Anubhav: https://medium.com/codex/kotlin-sealed-classes-for-better-handling-of-api-response-6aa1fbd23c76 (Accessed: 08-10-2023)

sealed class DataState<out T> {
    object Loading : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val error: String) : DataState<Nothing>()
}