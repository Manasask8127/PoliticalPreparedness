package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    //ViewBinding declaration
    private lateinit var binding: FragmentVoterInfoBinding

    //viewmodel declaration
    private lateinit var viewModel:VoterInfoViewModel

    //argumets
    private val args:VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add binding values
        binding=FragmentVoterInfoBinding.inflate(inflater)

        //TODO: Add ViewModel values and create ViewModel
        val voterInfoViewModelFactory=VoterInfoViewModelFactory(requireActivity().applicationContext)
        viewModel=ViewModelProvider(requireActivity(),voterInfoViewModelFactory).get(VoterInfoViewModel::class.java)


        binding.viewModel=viewModel

        val address=viewModel.getAddress(args.argDivision)

        viewModel.loadElectionInfo(address,args.argElectionID,args.argLoadFromDB)

        viewModel.election.observe(requireActivity(),{
            binding.election=it
        })

        viewModel.administrationBody.observe(requireActivity(),{
            binding.administrationBody=it
        })
        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //TODO: Handle loading of URLs
        binding.stateBallot.setOnClickListener {
            val adminBody=viewModel.administrationBody.value
            loadElectionInfoUrl(adminBody?.ballotInfoUrl)
        }

        binding.stateLocations.setOnClickListener {
            val adminBody=viewModel.administrationBody.value
            loadElectionInfoUrl(adminBody?.electionInfoUrl)
        }

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        viewModel.followElectionButtonText.observe(requireActivity(),{
            binding.followElectionButton.text=it
        })

        binding.followElectionButton.setOnClickListener{
            viewModel.followOrUnfollowElection(args.argElectionID)
        }

        return binding.root
    }

    //TODO: Create method to load URL intents
    private fun loadElectionInfoUrl(url:String?){
        Intent(Intent.ACTION_VIEW).run {
            data= Uri.parse(url)
            startActivity(this)
        }
    }

}