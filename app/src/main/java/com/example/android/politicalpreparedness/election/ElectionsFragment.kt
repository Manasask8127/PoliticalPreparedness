package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import java.util.*

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    private lateinit var viewModel: ElectionsViewModel

    //ViewBinding
    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel
        val electionsViewModelFactory=ElectionsViewModelFactory()
        viewModel=ViewModelProvider(this,electionsViewModelFactory).get(ElectionsViewModel::class.java)
        //TODO: Add binding values
        binding=FragmentElectionBinding.inflate(inflater)

        binding.viewModel=viewModel

        binding.lifecycleOwner=this

        //TODO: Create ElectionViewHolder
        viewModel.navigateToVoterInfo.observe(requireActivity()) { election ->
            election?.let {
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id, election.division
                    )
                )
                viewModel.doneNavigatingToVoterInfo()
            }
        }


        //TODO: Initiate recycler adapters
        val upcomingElectionsListAdapter=ElectionListAdapter(ElectionListAdapter.ElectionListener{election ->
            viewModel.startNavigatingToVoterInfo(election)
        })

        //TODO: Populate recycler adapters
        binding.upcomingElections.adapter=upcomingElectionsListAdapter
//        val division=Division("ocd-division/country:us","USA","la")
//        val date= Date(2025,7,6)
//        val electionList= listOf<Election>(Election(2000,"test election",date,division))

        viewModel.upcomingElections.observe(requireActivity()) { electionList ->
            upcomingElectionsListAdapter.submitList(electionList)
        }


        return binding.root
    }

    //TODO: Refresh adapters when fragment loads

}