package net.theluckycoder.qr.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService

class ConnectivityProvider(context: Context) {

    private val cm: ConnectivityManager = context.getSystemService()!!

    private val networkCallback = ConnectivityCallback()

    var networkState: NetworkState = NetworkState.NotConnected
        private set

    init {
        subscribe()
    }

    fun subscribe() {
        cm.registerDefaultNetworkCallback(networkCallback)
    }

    @Suppress("unused")
    fun unsubscribe() {
        cm.unregisterNetworkCallback(networkCallback)
    }

    /*fun getNetworkCapabilities(): NetworkState {
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return if (capabilities != null) {
            NetworkState.Connected(capabilities)
        } else {
            NetworkState.NotConnected
        }
    }*/

    private inner class ConnectivityCallback : ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            networkState = NetworkState.Connected(capabilities)
        }

        override fun onLost(network: Network) {
            networkState = NetworkState.NotConnected
        }
    }
}

sealed class NetworkState {
    class Connected(val capabilities: NetworkCapabilities) : NetworkState()
    object NotConnected : NetworkState()
}
