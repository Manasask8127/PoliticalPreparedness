package com.example.android.politicalpreparedness.election

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.Election

//TODO: Create Factory to generate VoterInfoViewModel with provided election datasource
class VoterInfoViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(VoterInfoViewModel::class.java)){
            return VoterInfoViewModel(repository) as T
        }
        throw ClassNotFoundException("unknown view model")
    }

}