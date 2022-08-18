//package com.example.android.politicalpreparedness
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Context
//import android.content.pm.PackageManager
//import android.location.Location
//import android.os.Build
//import androidx.core.content.ContentProviderCompat.requireContext
//import androidx.core.content.ContextCompat
//import com.example.android.politicalpreparedness.representative.DetailFragment
//import com.google.android.gms.common.ConnectionResult
//import com.google.android.gms.common.GoogleApiAvailability
//import com.google.android.gms.location.LocationServices
//import timber.log.Timber
//
//class LocationPermissionManager(private val context: Context) {
//
//    private var fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(context)
//    private val qosOrLater=Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q
//
//    fun isPermissionGranted() : Boolean {
//        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
//        val foregroundLocationEnabled=(
//                PackageManager.PERMISSION_GRANTED== ContextCompat.checkSelfPermission(
//                    context, Manifest.permission.ACCESS_FINE_LOCATION
//                ))
//        val backgroundPermissionEnabled=if(qosOrLater){
//            PackageManager.PERMISSION_GRANTED==ContextCompat.checkSelfPermission(
//                context,Manifest.permission.ACCESS_BACKGROUND_LOCATION
//            )
//        }
//        else
//            true
//
//        return foregroundLocationEnabled && backgroundPermissionEnabled
//    }
//
//    fun requestLocatonPermissions(activity: Activity){
//        var permissionArray= arrayOf(Manifest.permission.ACCESS_FINE_LOCATION
//        ,Manifest.permission.ACCESS_FINE_LOCATION)
//        val requestCode=when{
//            qosOrLater ->{
//                permissionArray+=Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                DetailFragment.REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
//            }
//            else -> DetailFragment.REQUEST_FOREGROUND_PERMISSION_RESULT_CODE
//        }
//
//    }
//
//    @SuppressLint("MissingPermission")
//    fun getLastKnownLocation(context: Context):Location?{
//        var lastLocation:Location?
//        if(GoogleApiAvailability.getInstance()
//                .isGooglePlayServicesAvailable(context)==ConnectionResult.SUCCESS){
//            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
//                if(it.isSuccessful){
//                    lastLocation=it.result
//
//                }
//            }
//        }
//        else
//            Timber.d("GPS not available")
//        return null
//    }
//}