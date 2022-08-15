package com.example.android.politicalpreparedness.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.lang.reflect.Constructor

class ElectionsNetworkManager(context: Context){
    val connectivityManager=context.getSystemService(ConnectivityManager::class.java)

    private var _connectedToNetwork=MutableLiveData<Boolean>()
    val connectedToNetwork:LiveData<Boolean>
    get() = _connectedToNetwork

    private val networkRequest=NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallbacks=object :ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            val networkCapabilities=connectivityManager.getNetworkCapabilities(network)
            val hasInternetCapability=networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            if(hasInternetCapability==true){
                _connectedToNetwork.postValue(true)
            }
        }

        override fun onLost(network: Network) {
            _connectedToNetwork.postValue(false)
        }
    }

    init {
        connectivityManager?.registerNetworkCallback(networkRequest,networkCallbacks)
    }

    companion object:SingletonHolder<ElectionsNetworkManager,Context>(::ElectionsNetworkManager)

}

open class SingletonHolder<out T,in A>(private val constructor: (A)->T){
    @Volatile
    private var instance:T?=null

    fun getInstance(arg:A):T= instance?: synchronized(this){
        instance?:constructor(arg).also { instance=it }
    }
}