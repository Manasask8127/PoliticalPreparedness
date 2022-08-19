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
class ElectionsViewModel(private val repository: Repository) : ViewModel() {

    //private val repository=Repository(applicationContext)

    //TODO: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    //TODO: Create live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections


    //TODO: Create functions to navigate to saved or upcoming election voter info

    private fun getUpcomingElection() {
        try {
            viewModelScope.launch {
                _upcomingElections.value = repository.getUpcomingElectionsList()
            }
        } catch (ex: Exception) {
            Timber.d("exception ${ex.message}")
        }
    }

    fun getSavedElections() {
        try {
            viewModelScope.launch {
                _savedElections.value = repository.getSavedElectionsList()
            }
        } catch (ex: Exception) {
            Timber.d("exception ${ex.message}")
        }
    }

    fun refreshElections() {
        getSavedElections()
        getUpcomingElection()
    }


}