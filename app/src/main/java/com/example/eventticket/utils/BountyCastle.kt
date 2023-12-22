package com.example.eventticket.utils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

// Adapted from Medium article by Boemo Mmopelwa: https://boemo1mmopelwa.medium.com/implementing-etherium-blockchain-in-android-with-web3j-485ea0747088 (Accessed: 08-10-2023)

fun setupBouncyCastle() {
    val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
    if (provider == null)
        return
    else if (provider.javaClass == BouncyCastleProvider::class.java) {
        return
    }
    Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
    Security.insertProviderAt(BouncyCastleProvider(), 1)
}