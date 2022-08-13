package com.example.android.politicalpreparedness

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionAndAdministrationBody
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class Repository(context: Context) {

    private val database=ElectionDatabase.getInstance(context)

    private lateinit var upcomingElection:List<Election>
    private lateinit var savedElection:List<Election>
    private lateinit var state:State
    private lateinit var voterInfoResponse: VoterInfoResponse
    private var returnedElectionId by Delegates.notNull<Int>()
    private lateinit var electionAndAdministrationBody: ElectionAndAdministrationBody


    suspend fun getUpcomingElectionsList():List<Election>{

        withContext(Dispatchers.IO){
            upcomingElection = CivicsApi.retrofitService.getElectionsList().elections
        }
        return upcomingElection
    }

//    suspend fun getVoterInfo(address:String,electionId:Int):State{
//        withContext(Dispatchers.IO){
//            state= CivicsApi.retrofitService.getVoterInfo(address,electionId).state?.first()!!
//        }
//        return state
//    }

    suspend fun getSavedElectionsList():List<Election>{
        withContext(Dispatchers.IO){
            savedElection=database.electionDao.getAllElections()
        }
        return savedElection
    }

    suspend fun getElectionInfo(address: String,electionId: Int):VoterInfoResponse?{
        withContext(Dispatchers.IO){
            voterInfoResponse=CivicsApi.retrofitService.getVoterInfo(electionId,address)
        }
        return voterInfoResponse
    }

    suspend fun getElectionFromDB(electionId: Int):Int{
        withContext(Dispatchers.IO){
            returnedElectionId=database.electionDao.checkElectionSaved(electionId)
        }
        return returnedElectionId
    }

    suspend fun insertElectionAndAdministrationBody(election: Election,administrationBody: AdministrationBody){
        withContext(Dispatchers.IO){
            administrationBody.adminId=election.id
            database.electionDao.insertElectionAndAdministrationBody(election,administrationBody)
        }
    }

    suspend fun deleteElectionandAdministrationBody(election: Election,administrationBody: AdministrationBody){
        withContext(Dispatchers.IO){
            administrationBody.adminId=election.id
            database.electionDao.deleteElectionAndAdministrationBody(election,administrationBody)
        }
    }

    suspend fun getElectionAndAdministrationBody(electionId: Int):ElectionAndAdministrationBody?{
        withContext(Dispatchers.IO){
            electionAndAdministrationBody=database.electionDao.getElectionAndAdministrationBody(electionId)
        }
        return electionAndAdministrationBody
    }
}