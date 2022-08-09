package com.example.android.politicalpreparedness

import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {

    private lateinit var upcomingElection:List<Election>
    suspend fun getUpcomingElectionsList():List<Election>{

        withContext(Dispatchers.IO){
            upcomingElection = CivicsApi.retrofitService.getElectionsList().elections
        }
        return upcomingElection
    }
}