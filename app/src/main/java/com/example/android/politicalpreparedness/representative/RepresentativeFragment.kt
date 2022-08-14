package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        //TODO: Add Constant for Location request
        const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE=555
        const val REQUEST_FOREGROUND_PERMISSION_RESULT_CODE=556
    }

    private val qosOrLater=Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q
    private lateinit var fusedLocationServices:FusedLocationProviderClient

    //TODO: Declare ViewModel
    private lateinit var representativeViewModel: RepresentativeViewModel

    private lateinit var binding:FragmentRepresentativeBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Establish bindings
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_representative,container,false)

//        binding.apply {
//            lifecycleOwner=this@DetailFragment
//          //  viewModel=representativeViewModel
//        }
        representativeViewModel= RepresentativeViewModel(requireActivity().applicationContext)
        fusedLocationServices=LocationServices.getFusedLocationProviderClient(requireActivity())


        //TODO: Define and assign Representative adapter
        val representativeListAdapter=RepresentativeListAdapter()

        //TODO: Populate Representative adapter
        binding.representatives.adapter=representativeListAdapter

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            Timber.d("find my reps clicked")
            val address=checkAddressFieldNotEmpty()
            if (address!=null){
                representativeViewModel.getRepresentatives(address.toString())
                hideKeyboard()
            }
        }

        binding.buttonLocation.setOnClickListener {
            Timber.d("locatio button clicked")
            checkLocationPermissions()
        }

        representativeViewModel.representatives.observe(requireActivity(),{
            Timber.d("repres i fragment $it")
            representativeListAdapter.submitList(it)
        })

        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted
        if (grantResults.isEmpty()|| grantResults[0]==PackageManager.PERMISSION_DENIED||
            (requestCode== REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[1]==PackageManager.PERMISSION_DENIED)||
            (requestCode== REQUEST_FOREGROUND_PERMISSION_RESULT_CODE && grantResults[1]==
                    PackageManager.PERMISSION_DENIED)){
            Timber.d("Permission denied")
            Toast.makeText(requireContext(),"Please grant permission to access location",Toast.LENGTH_SHORT).show()
        }
        else{
            Timber.d("Permission granted")
            getLocation()
        }

    }

    private fun checkAddressFieldNotEmpty():String?{
        var address:String?=null
        when{
            binding.addressLine1.text.isEmpty()->{
                binding.addressLine1.error="Please enter address line"
            }
            binding.city.text.isEmpty()->{
                binding.city.error="Please enter city"
            }
            binding.zip.text.isEmpty()->{
                binding.zip.error="Please enter zip code"
            }
            else->{
                address=binding.addressLine1.text.toString().trim().plus(",")
                    .plus(binding.addressLine2.text.toString().trim()).plus(",")
                    .plus(binding.city.text.toString().trim()).plus(",")
                    .plus(binding.state.selectedItem.toString().trim()).plus(",")
                    .plus(binding.zip.text.toString().trim())
                Timber.d("address $address")
            }
        }
        return address
    }

    private fun checkLocationPermissions() {
        if (isPermissionGranted()) {
            getLocation()
        } else {
            //TODO: Request Location permissions
            requestLocatonPermissions()
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        val foregroundLocationEnabled=(
                PackageManager.PERMISSION_GRANTED==ContextCompat.checkSelfPermission(
                    requireContext(),Manifest.permission.ACCESS_FINE_LOCATION
                ))
        val backgroundPermissionEnabled=if(qosOrLater){
            PackageManager.PERMISSION_GRANTED==ContextCompat.checkSelfPermission(
                requireContext(),Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
        else
            true
        Timber.d("f and b loc ${foregroundLocationEnabled && backgroundPermissionEnabled}")
        return foregroundLocationEnabled && backgroundPermissionEnabled
    }

    private fun requestLocatonPermissions(){
        if(isPermissionGranted())
        {
            return
        }
        var permissionArray= arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val requestCode=when{
            qosOrLater ->{
                permissionArray+=Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_PERMISSION_RESULT_CODE
        }
        requireActivity().requestPermissions(permissionArray,requestCode)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        fusedLocationServices.lastLocation.addOnSuccessListener(requireActivity()){location->
            if (location!=null){
                val address=geoCodeLocation(location)
                binding.address=address
                Timber.d("address is ${address}")
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}