package com.example.android.politicalpreparedness.election

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(applicationContext:Context): ViewModel() {

    private val repository=Repository(applicationContext)

    //TODO: Create live data val for upcoming elections
    private val _upcomingElections=MutableLiveData<List<Election>>()
    val upcomingElections:LiveData<List<Election>>
    get()=_upcomingElections

    private val _savedElections=MutableLiveData<List<Election>>()
    val savedElections:LiveData<List<Election>>
    get() = _savedElections

    private fun getUpcomingElection(){
        try {
        viewModelScope.launch {
            _upcomingElections.value = repository.getUpcomingElectionsList()
        }
        }
        catch (ex:Exception){
            Timber.d("exception ${ex.message}")
        }
    }

//    fun startNavigatingToVoterInfo(election: Election){
//        _navigateToVoterInfo.value=election
//    }
//
//    fun doneNavigatingToVoterInfo(){
//        _navigateToVoterInfo.value=null
//    }
    fun getSavedElections(){
    try {
        viewModelScope.launch {
            _savedElections.value = repository.getSavedElectionsList()
        }
        }
    catch(ex:Exception){
        Timber.d("exception ${ex.message}")
    }
    }
    fun refreshElections() {
        getSavedElections()
        getUpcomingElection()
    }

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}