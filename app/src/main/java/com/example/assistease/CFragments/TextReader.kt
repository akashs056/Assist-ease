package com.example.assistease.CFragments

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.assistease.R
import com.example.assistease.databinding.FragmentTextReaderBinding
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.util.Locale

class TextReader : Fragment(), TextToSpeech.OnInitListener {
    private lateinit var binding: FragmentTextReaderBinding
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextReaderBinding.inflate(inflater, container, false)
        textToSpeech = TextToSpeech(requireContext(), this)
        onBackClicked()
        onGetStartedButtonClicked()
        return binding.root
    }

    private fun onBackClicked() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_textReader_to_homeFragment2)
        }
    }

    private fun onGetStartedButtonClicked() {
        binding.materialButton.setOnClickListener {
            chooseImage()
        }
    }

    private fun chooseImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 123)
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 123)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Extract the image
        val bundle = data!!.extras
        val bitmap = bundle!!["data"] as Bitmap?
        binding.img.setImageBitmap(bitmap)

        // Create a FirebaseVisionImage object from your image/bitmap.
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap!!)
        val firebaseVision = FirebaseVision.getInstance()
        val firebaseVisionTextRecognizer = firebaseVision.onDeviceTextRecognizer

        // Process the Image
        val task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage)
        task.addOnSuccessListener { firebaseVisionText: FirebaseVisionText ->
            // Set recognized text from image in our TextView
            val text = firebaseVisionText.text
            binding.info.text = text
            // Speak the recognized text
            speakText(text)
        }
        task.addOnFailureListener { e: Exception ->
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Select a female voice
            selectFemaleVoice()
        } else {
            Toast.makeText(requireContext(), "Initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectFemaleVoice() {
        val availableVoices = textToSpeech.voices
        var selectedVoice: Voice? = null
        for (voice in availableVoices) {
            // Check if the voice locale corresponds to a female-sounding language
            if (voice.locale.language == Locale.US.language && voice.locale.country == "US") {
                selectedVoice = voice
                break
            }
        }
        if (selectedVoice != null) {
            textToSpeech.voice = selectedVoice
        } else {
            Toast.makeText(requireContext(), "No female voices found", Toast.LENGTH_SHORT).show()
        }
    }



    private fun speakText(text: String) {
        val rate=1.3f
        // Speak the recognized text
        textToSpeech.setSpeechRate(rate)
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Don't forget to release the TextToSpeech resources when the fragment is destroyed
    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
