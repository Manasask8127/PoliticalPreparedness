package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import java.util.*

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel

    //ViewBinding
    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values
        binding=FragmentElectionBinding.inflate(inflater)

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters
        val upcomingElectionsListAdapter=ElectionListAdapter(ElectionListAdapter.ElectionListener{electionId ->
            Toast.makeText(requireContext(),"clicked ${electionId}",Toast.LENGTH_SHORT).show()
        })

        //TODO: Populate recycler adapters
        val division=Division("ocd-division/country:us","USA","la")
        val date= Date(2025,7,6)
        val electionList= listOf<Election>(Election(2000,"test election",date,division))
        upcomingElectionsListAdapter.submitList(electionList)

        return binding.root
    }

    //TODO: Refresh adapters when fragment loads

}