package com.example.android.politicalpreparedness.representative

import android.content.Context
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(private val repository:Repository,
                              private val savedStateHandle: SavedStateHandle):
    ViewModel() {

    private val line1="line1"
    private val line2="line2"
    private val city="city"
    private val state="state"
    private val zip="zip"
    private val showList="list"

    //private val repository=Repository(applicationContext)

    private var _networkException=MutableLiveData<String>()
    val networkException:LiveData<String>
    get() = _networkException

    //TODO: Establish live data for representatives and address
    private var _representatives=MutableLiveData<List<Representative>>()
    val representatives:LiveData<List<Representative>>
    get() = _representatives


    var addressLine1=MutableLiveData<String>()
    set(value) {
        field=value
        savedStateHandle.set(line1,value.value)
    }

    var addressLine2=MutableLiveData<String>()
        set(value) {
            field=value
            savedStateHandle.set(line2,value.value)
        }

    var addressCity=MutableLiveData<String>()
        set(value) {
            field=value
            savedStateHandle.set(city,value.value)
        }

    var addressState=MutableLiveData<String>()
        set(value) {
            field=value
            savedStateHandle.set(state,value.value)
        }

    var addressZip=MutableLiveData<String>()
        set(value) {
            field=value
            savedStateHandle.set(zip,value.value)
        }

    var _isListShowing=MutableLiveData<Boolean>()
        set(value) {
            field=value
            savedStateHandle.set(showList,value.value)
        }

//    private var _motionTransition=MutableLiveData<Int>()
//        set(value){
//            field=value
//            savedStateHandle.set("motion",value.value)
//        }
//    val motionTransition:LiveData<Int>
//        get() = _motionTransition

    init {
        restoreAddress()
        val address:String?=getAddress()
        if(address!=null){
            getRepresentatives(address)
        }
    }

    private fun restoreAddress() {
        addressLine1=savedStateHandle.getLiveData(line1)
        addressLine2=savedStateHandle.getLiveData(line2)
        addressCity=savedStateHandle.getLiveData(city)
        addressState=savedStateHandle.getLiveData(state)
        addressZip=savedStateHandle.getLiveData(zip)
        _isListShowing=savedStateHandle.getLiveData(showList)
       // _motionTransition=savedStateHandle.getLiveData("motion")
    }



    //TODO: Create function to fetch representatives from API from a provided address
    fun getRepresentatives(address:String) {
        viewModelScope.launch {
            try {
                val (offices, officials) = repository.getRepresentativeInfoByAddress(address)
                _representatives.value = offices.flatMap {
                    it.getRepresentatives(officials)
                }

            } catch (ex: Exception) {
                _networkException.postValue(ex.message)
            }
            Timber.d("repres ${_representatives.value}")
        }
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
    fun getAddress():String?{
        if (ValidateAddress()){
            return Address(
                addressLine1.value!!,
                addressLine2.value!!,
                addressCity.value!!,
                addressState.value!!,
                addressZip.value!!).toFormattedString()
        }
        return null
    }

    private fun ValidateAddress(): Boolean {
        return (addressLine1.value?.isNotBlank()==true && addressCity.value?.isNotBlank()==true
                && addressState.value?.isNotBlank()==true && addressZip.value?.isNotBlank()==true
                && _isListShowing.value==true)

    }

    fun setAddress(address: Address){
        addressLine1.value=address.line1
        addressLine2.value=address.line2
        addressCity.postValue(address.city)
        addressState.postValue(address.state)
        addressZip.postValue(address.zip)
    }

    fun setListShowing(boolean: Boolean){
        _isListShowing.postValue(boolean)
    }

//    fun setMotionTransitionID(id:Int){
//        _motionTransition.postValue(id)
//    }

}
