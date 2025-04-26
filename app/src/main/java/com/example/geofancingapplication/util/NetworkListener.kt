package com.example.geofancingapplication.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow

class NetworkListener : ConnectivityManager.NetworkCallback() {
    private val isNetworkAvailable = MutableStateFlow(false)

    fun startListening(context: Context): MutableStateFlow<Boolean> {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val requestNetwork = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(requestNetwork, this)
        return isNetworkAvailable
    }

    fun stopListening(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(this)

    }

    //    fun checkNetworkAvailability(context: Context): MutableStateFlow<Boolean>{
//        //        context.getSystemService(ConnectivityManager::class.java)
//        val connectivityManager =
//        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        connectivityManager.registerDefaultNetworkCallback(this)
//
//        var isConnected = false
//
//
//        // Deprecated Code
//        connectivityManager.allNetworks.forEach { network ->
//            val networkCapability = connectivityManager.getNetworkCapabilities(network)
//            networkCapability?.let {
//                if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){
//                    isConnected = true
//                    return@forEach
//                }
//            }
//        }
//        isNetworkAvailable.value = isConnected
//
//        return isNetworkAvailable
//    }
    override fun onAvailable(network: Network) {
        isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        isNetworkAvailable.value = false
    }
}