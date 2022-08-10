package com.example.android.politicalpreparedness

import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {

    private lateinit var upcomingElection:List<Election>
    private lateinit var state:State
    suspend fun getUpcomingElectionsList():List<Election>{

        withContext(Dispatchers.IO){
            upcomingElection = CivicsApi.retrofitService.getElectionsList().elections
        }
        return upcomingElection
    }

    suspend fun getVoterInfo(address:String,electionId:Int):State{
        withContext(Dispatchers.IO){
            state= CivicsApi.retrofitService.getVoterInfo(address,electionId).state?.first()!!
        }
        return state
    }
}