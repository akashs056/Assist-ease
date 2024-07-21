package com.example.assistease.CFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.assistease.R
import com.example.assistease.databinding.FragmentFeedbackBinding


class FeedbackFragment : Fragment() {
    private lateinit var binding:FragmentFeedbackBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentFeedbackBinding.inflate(inflater,container,false)
        onBackClicked()
        onSubmitClicked()
        onCancelledClicked()
        return binding.root
    }

    private fun onCancelledClicked() {
        binding.cancel.setOnClickListener {
            binding.edFeedback.text=null
        }
    }

    private fun onSubmitClicked() {
        binding.submit.setOnClickListener {
            binding.edFeedback.text=null
            Toast.makeText(requireContext(),"Feedback Submitted",Toast.LENGTH_SHORT).show()
        }
    }

    private fun onBackClicked() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_feedbackFragment_to_homeFragment2)
        }
    }


}