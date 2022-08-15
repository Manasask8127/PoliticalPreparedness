package com.example.android.politicalpreparedness.representative

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(applicationContext: Context): ViewModel() {

    private val repository=Repository(applicationContext)

    private var _networkException=MutableLiveData<String>()
    val networkException:LiveData<String>
    get() = _networkException

    //TODO: Establish live data for representatives and address
    private var _representatives=MutableLiveData<List<Representative>>()
    val representatives:LiveData<List<Representative>>
    get() = _representatives

    val addressLine1=MutableLiveData<String>()
    val addressLine2=MutableLiveData<String>()
    val city=MutableLiveData<String>()
    val state=MutableLiveData<String>()
    val zip=MutableLiveData<String>()



    //TODO: Create function to fetch representatives from API from a provided address
    fun getRepresentatives(address:String){
        try {
        viewModelScope.launch {
            val (offices,officials)=repository.getRepresentativeInfoByAddress(address)
            _representatives.value=offices.flatMap {
                it.getRepresentatives(officials)
            }

            }
        }catch (ex:Exception){
            _networkException.postValue(ex.message)
        }
        Timber.d("repres ${_representatives.value}")
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields
    fun setAddress(address: Address){
        addressLine1.value=address.line1
        addressLine2.value=address.line2
        city.value=address.city
        state.value=address.state
        zip.value=address.zip
    }

}
