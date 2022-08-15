package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.ElectionsNetworkManager
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import java.util.*

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    private lateinit var electionViewModel: ElectionsViewModel

    //ViewBinding
    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        //TODO: Add binding values
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_election,container,false)

        //TODO: Add ViewModel values and create ViewModel
        val electionsViewModelFactory=ElectionsViewModelFactory(requireActivity().applicationContext)
        electionViewModel=ViewModelProvider(this,electionsViewModelFactory).get(ElectionsViewModel::class.java)

        binding.viewModel=electionViewModel

        binding.apply {
            lifecycleOwner = this@ElectionsFragment
            viewModel=electionViewModel
        }

            //TODO: Create ElectionViewHolder
            val upcomingElectionListAdapter=ElectionListAdapter(ElectionListAdapter.ElectionListener{
                    election ->
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id,election.division,false
                    )
                )
            })

            binding.upcomingElections.adapter=upcomingElectionListAdapter

            electionViewModel.upcomingElections.observe(viewLifecycleOwner,{
                    electionList-> upcomingElectionListAdapter.submitList(electionList)
            })


            //TODO: Initiate recycler adapters
            val savedElectionListAdapter=
                ElectionListAdapter(ElectionListAdapter.ElectionListener{election ->
                    //viewModel.startNavigatingToVoterInfo(election)
                    findNavController().navigate(
                        ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                            election.id,election.division,true
                        )
                    )
                })

            //TODO: Populate recycler adapters
            binding.savedElections.adapter=savedElectionListAdapter
//        val division=Division("ocd-division/country:us","USA","la")
//        val date= Date(2025,7,6)
//        val electionList= listOf<Election>(Election(2000,"test election",date,division))

            electionViewModel.savedElections.observe(viewLifecycleOwner, { electionList ->
                savedElectionListAdapter.submitList(electionList)
            })

            //electionViewModel.loadElections()
        refreshElections()

        binding.refreshUpcomingElections.setOnRefreshListener {
            refreshElections()
        }
            return binding.root
        }


    //TODO: Refresh adapters when fragment loads
    private fun refreshElections(){
        val networkManager= ElectionsNetworkManager.getInstance(requireActivity().applicationContext)
        networkManager.connectedToNetwork.observe(viewLifecycleOwner,{isNetWorkAvailable ->
            if(isNetWorkAvailable){
                showUpcomingElections()
            }
            else{
                showNotConnectedToNetwork()
            }
        })
        electionViewModel.getSavedElections()
    }

    private fun showNotConnectedToNetwork() {
        binding.upcomingElections.visibility=View.GONE
        binding.error.visibility=View.VISIBLE
        binding.error.setImageResource(R.drawable.ic_no_network)
        binding.refreshUpcomingElections.isRefreshing=false
    }

    private fun showUpcomingElections(){
        binding.error.visibility=View.GONE
        binding.upcomingElections.visibility=View.VISIBLE
        electionViewModel.refreshElections()
        binding.refreshUpcomingElections.isRefreshing=false
    }


}