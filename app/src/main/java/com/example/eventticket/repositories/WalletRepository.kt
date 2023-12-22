package com.example.eventticket.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.eventticket.repositories.interfaces.WalletRepositoryInterface
import com.example.eventticket.utils.DataState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.future.await
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import javax.inject.Inject
import javax.inject.Singleton
import java.io.File

@Singleton
class WalletRepository @Inject internal constructor(@ApplicationContext private val context: Context, private val web3j: Web3j) : WalletRepositoryInterface {
    @SuppressLint("SetWorldReadable", "SetWorldWritable")
    override fun unlockCreateWallet(password: String, fileName: String): DataState<Credentials> {
        val file = File(context.filesDir.toString() + "/" + fileName)
        if (!file.exists()) {
            file.mkdirs()
            file.setReadable(true,false)
            file.setWritable(true,false)
            WalletUtils.generateLightNewWalletFile(password, file)
        }
        val files = file.listFiles()
        return if (!files.isNullOrEmpty()) {
            try {
                val credentials = WalletUtils.loadCredentials(password, files[0])
                Log.d("wallet",credentials.address)
                DataState.Success(credentials)
            } catch (e: Exception) {
                DataState.Error("Password was incorrect!")
            }
        } else {
            DataState.Error("No wallet file was detected!")
        }
    }

    override suspend fun fetchAccountBalance(credentials: Credentials) = flow {
        try {
            emit(DataState.Loading)
            val response = web3j.ethGetBalance(credentials.address,
                DefaultBlockParameterName.LATEST
            ).sendAsync().await()
            val balance = response.balance
            emit(DataState.Success(balance))
        } catch (e: Exception) {
            Log.d("balance",e.message.toString())
            emit(DataState.Error("Events could not be fetched!"))
        }
    }
}