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
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.PoliticalPreparednessApplication
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.ElectionsNetworkManager
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        //TODO: Add Constant for Location request
        const val REQUEST_FOREGROUND_PERMISSION_RESULT_CODE = 556
    }

    //private val qosOrLater=Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q
    private lateinit var fusedLocationServices: FusedLocationProviderClient

    //TODO: Declare ViewModel
    private lateinit var representativeViewModel: RepresentativeViewModel

    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //TODO: Establish bindings
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)


        val repoAppRepoContainer =
            (requireActivity().application as PoliticalPreparednessApplication).appRepoContainer
        val representativeViewModelFactory =
            RepresentativeViewModelFactory(repoAppRepoContainer.repository, this)
        representativeViewModel = ViewModelProvider(this, representativeViewModelFactory).get(
            RepresentativeViewModel::class.java
        )
        fusedLocationServices = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.apply {
            lifecycleOwner = this@DetailFragment
            viewModel = representativeViewModel
        }

        //TODO: Define and assign Representative adapter
        val representativeListAdapter = RepresentativeListAdapter()

        //TODO: Populate Representative adapter
        binding.representatives.adapter = representativeListAdapter

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            Timber.d("find my reps clicked")
            checkNetwork()
            val address = checkAddressFieldNotEmpty()
            if (address != null) {
                representativeViewModel.getRepresentatives(address)
                hideKeyboard()
            }
        }

        binding.buttonLocation.setOnClickListener {
            Timber.d("locatio button clicked")
            checkLocationPermissions()
        }

        binding.addressLine1.doOnTextChanged { _, _, _, _ ->
            binding.addressLine1.error = null
        }
        binding.city.doOnTextChanged { _, _, _, _ ->
            binding.city.error = null
        }
        binding.zip.doOnTextChanged { _, _, _, _ ->
            binding.zip.error = null
        }
        representativeViewModel.representatives.observe(viewLifecycleOwner) {
            Timber.d("repres i fragment $it")
            hideKeyboard()
            representativeListAdapter.submitList(it)
            representativeViewModel.setListShowing(true)
        }

//        representativeViewModel.motionTransition.observe(viewLifecycleOwner){ id->
//            Timber.d("id is $id")
//            if(id!=null){
//                binding.motionLayout.transitionToState(id)
//            }
////            else
////                binding.motionLayout.transitionToState(0)
//        }

        representativeViewModel.networkException.observe(this.viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        if(savedInstanceState?.getInt("motionLayout") != null) {
            binding.motionLayout
                .transitionToState(savedInstanceState
                    .getInt("motionLayout"))
        }


        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("motionLayout", binding.motionLayout
            .currentState)
//        super.onSaveInstanceState(outState)
//        if(representativeViewModel.representatives.value!=null)
//            representativeViewModel.setMotionTransitionStateId(binding.motionLayout.currentState)
    }

    private fun checkNetwork() {
        ElectionsNetworkManager.getInstance(requireActivity().applicationContext).connectedToNetwork.observe(
            viewLifecycleOwner
        ) { isConnected ->
            if (!isConnected) {
                Toast.makeText(requireContext(), "Not connected", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted
        if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_PERMISSION_RESULT_CODE && grantResults[1] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            Timber.d("Permission denied")
            Toast.makeText(
                requireContext(),
                "Please grant permission to access location",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Timber.d("Permission granted")
            getLocation()
        }

    }

    private fun checkAddressFieldNotEmpty(): String? {
        var address: Address? = null
        when {
            binding.addressLine1.text.isEmpty() -> {
                binding.addressLine1.error = "Please enter address line"
            }
            binding.city.text.isEmpty() -> {
                binding.city.error = "Please enter city"
            }
            binding.zip.text.isEmpty() -> {
                binding.zip.error = "Please enter zip code"
            }
            else -> {
                val line1 = binding.addressLine1.text.toString().trim()
                val line2 = binding.addressLine2.text.toString().trim()
                val city = binding.city.text.toString().trim()
                val state = binding.state.selectedItem.toString().trim()
                val zip = binding.zip.text.toString().trim()
                address = Address(line1, line2, city, state, zip)
                Timber.d("address $address")
            }
        }
        return address?.toFormattedString()
    }

    private fun checkLocationPermissions() {
        if (isPermissionGranted()) {
            getLocation()
        } else {
            //TODO: Request Location permissions
            requestLocatonPermissions()
        }
    }

    private fun isPermissionGranted(): Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        val foregroundLocationEnabled = (
                PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ))
        Timber.d("f and b loc ${foregroundLocationEnabled}")
        return foregroundLocationEnabled
    }

    private fun requestLocatonPermissions() {
        var permissionArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val requestCode = REQUEST_FOREGROUND_PERMISSION_RESULT_CODE
        requireActivity().requestPermissions(permissionArray, requestCode)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        fusedLocationServices.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                val address = geoCodeLocation(location)
                representativeViewModel.setAddress(address)
                Timber.d("address is ${address}")
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address?.postalCode.toString()
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}