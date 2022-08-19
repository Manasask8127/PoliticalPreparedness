package com.example.android.politicalpreparedness.election

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.database.ElectionAndAdministrationBody
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(private val repository: Repository) : ViewModel() {

    //private val repository=Repository(applicationContext)

    //    //TODO: Add live data to hold voter info
    private val _followElectionButtonText = MutableLiveData<String>()
    val followElectionButtonText: LiveData<String>
        get() = _followElectionButtonText

    //TODO: Add var and methods to populate voter info
    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election>
        get() = _election

    //TODO: Add var and methods to support loading URLs
    private val _administrationBody = MutableLiveData<AdministrationBody>()
    val administrationBody: LiveData<AdministrationBody>
        get() = _administrationBody


    private fun getElectionInfoFromNetwork(address: String, electionId: Int) {
        viewModelScope.launch {
            val voterInfoResponse: VoterInfoResponse? =
                repository.getElectionInfo(address, electionId)
            if (voterInfoResponse != null) {
                _election.value = voterInfoResponse.election
                _administrationBody.value =
                    voterInfoResponse.state?.first()?.electionAdministrationBody
            }
        }
    }

    private fun getElectionInfoFromDB(electionId: Int) {
        viewModelScope.launch {
            val electionAndAdministrationBody: ElectionAndAdministrationBody =
                repository.getElectionAndAdministrationBody(electionId)!!
            _election.value = electionAndAdministrationBody.election
            _administrationBody.value = electionAndAdministrationBody.administrationBody
        }
    }

    fun followOrUnfollowElection(electionId: Int) {
        viewModelScope.launch {
            val electionFromDB = repository.getElectionFromDB(electionId)
            if (electionFromDB == electionId) {
                repository.deleteElectionandAdministrationBody(
                    _election.value!!,
                    _administrationBody.value!!
                )
            } else {
                repository.insertElectionAndAdministrationBody(
                    election.value!!,
                    _administrationBody.value!!
                )
            }
            updateFollowButtonText(electionId)
        }
    }

    fun getAddress(division: Division): String {
        if (division.state.isNotEmpty()) {
            return "${division.country},${division.state}"
        }
        return "America"
    }

    fun loadElectionInfo(address: String, electionId: Int, loadFromDB: Boolean) {
        if (loadFromDB) {
            getElectionInfoFromDB(electionId)
        } else {
            getElectionInfoFromNetwork(address, electionId)
        }
        updateFollowButtonText(electionId)
    }

    private fun updateFollowButtonText(electionId: Int) {
        viewModelScope.launch {
            val electionFromDB = repository.getElectionFromDB(electionId)
            if (electionFromDB == electionId) {
                _followElectionButtonText.value = "Unfollow"
            } else {
                _followElectionButtonText.value = "Follow"
            }
        }
    }
}