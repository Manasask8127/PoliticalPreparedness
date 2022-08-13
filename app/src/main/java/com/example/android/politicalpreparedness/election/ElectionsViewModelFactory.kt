package com.example.android.politicalpreparedness.election

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//TODO: Create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(private val applicationContext:Context): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ElectionsViewModel::class.java)){
            return ElectionsViewModel(applicationContext) as T
        }
        throw ClassNotFoundException("viewModel class not found")
    }

}