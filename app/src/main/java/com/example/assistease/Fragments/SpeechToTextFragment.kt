package com.example.assistease.Fragments

import android.app.Activity.RESULT_OK
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
import com.example.assistease.databinding.FragmentSpeechToTextBinding
import java.util.Locale

class SpeechToTextFragment : Fragment() {
    private lateinit var binding: FragmentSpeechToTextBinding
    private lateinit var adapter: SpokenTextAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeechToTextBinding.inflate(inflater, container, false)

        adapter= SpokenTextAdapter { position ->
            adapter.removeItem(position)
        }
        binding.textRv.adapter = adapter
        ListenSpeech()
        backBtnClicked()

        return binding.root
    }

    private fun backBtnClicked() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_speechToTextFragment_to_homeFragment)
        }
    }

    private fun ListenSpeech() {
        binding.listen.setOnClickListener {
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
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
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
