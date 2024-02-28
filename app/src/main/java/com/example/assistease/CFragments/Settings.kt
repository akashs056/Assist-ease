package com.example.assistease.CFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.assistease.R
import com.example.assistease.databinding.FragmentSettingsBinding


class Settings : Fragment() {
    private lateinit var binding:FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSettingsBinding.inflate(inflater,container,false)
        onBackClicked()
        return binding.root
    }

    private fun onBackClicked() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_settings2_to_homeFragment2)
        }
    }


}