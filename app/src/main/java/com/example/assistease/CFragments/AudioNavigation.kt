package com.example.assistease.CFragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.assistease.Adapters.SpokenTextAdapter
import com.example.assistease.R
import com.example.assistease.databinding.FragmentAudioNavigationBinding
import java.util.Locale


class AudioNavigation : Fragment() {
    private lateinit var binding: FragmentAudioNavigationBinding
    private lateinit var adapter: SpokenTextAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= FragmentAudioNavigationBinding.inflate(inflater,container,false)
        onBackClicked()
        adapter= SpokenTextAdapter { position ->
            adapter.removeItem(position)
        }
        binding.textRv.adapter = adapter
        ListenSpeech()
        return binding.root
    }

    private fun onBackClicked() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_audioNavigation2_to_homeFragment2)
        }
    }
    private fun ListenSpeech() {
        binding.voice.setOnClickListener {
            startListening()
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...")

        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {
            val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (spokenText != null) {
                adapter.addSpokenText(spokenText[0])
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 100
    }



}