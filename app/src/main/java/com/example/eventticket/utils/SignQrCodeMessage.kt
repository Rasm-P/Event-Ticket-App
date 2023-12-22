package com.example.eventticket.utils

import com.example.eventticket.models.QrCodeMessage
import org.web3j.crypto.Credentials
import org.web3j.crypto.Hash
import org.web3j.crypto.Sign
import java.math.BigInteger

fun signQrCodeMessage(eventId: BigInteger, ticketId: BigInteger, challenge: String, credentials: Credentials): QrCodeMessage {
    val message = eventId.toString() + ticketId.toString() + challenge
    val hashBytes = Hash.sha256(message.toByteArray(Charsets.UTF_8))
    val signature = Sign.signPrefixedMessage(hashBytes, credentials.ecKeyPair)
    val r = signature.r
    val s = signature.s
    val v = BigInteger(signature.v)
    return QrCodeMessage(eventId, ticketId, challenge, hashBytes, r, s, v)
}