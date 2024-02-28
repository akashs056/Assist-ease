package com.example.assistease.CFragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.assistease.R
import com.example.assistease.databinding.FragmentObjectDetectionBinding
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.label.ImageLabeler

class ObjectDetection : Fragment() {

    private lateinit var binding: FragmentObjectDetectionBinding
    private lateinit var imageBitmap: Bitmap
    private lateinit var imageLabeler: ImageLabeler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObjectDetectionBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        binding.materialButton.setOnClickListener {
            takeImage()
        }

        binding.detect.setOnClickListener {
            if (::imageBitmap.isInitialized) {
                processImage()
            } else {
                Toast.makeText(requireContext(), "Select a photo first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_objectDetection2_to_homeFragment2)
        }
    }

    private fun takeImage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        ).apply {
            type = "image/*"
            putExtra("crop", "true")
            putExtra("scale", true)
            putExtra("return-data", true)
        }
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun processImage() {
        val localModel = LocalModel.Builder()
            .setAssetFilePath("your_local_model.tflite")
            .build()

        imageLabeler = com.google.mlkit.vision.label.ImageLabeling.getClient(
            ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f) // You can adjust this threshold as needed
                .build()
        )

        val inputImage = InputImage.fromBitmap(imageBitmap, 0)
        imageLabeler.process(inputImage).addOnSuccessListener { imageLabels ->
            var stringImageRecognition = ""
            for (imageLabel in imageLabels) {
                val stingLabel = imageLabel.text
                val floatConfidence = imageLabel.confidence
                val index = imageLabel.index
                stringImageRecognition += "$index\n $stingLabel:\n $floatConfidence "
            }
            binding.info.text = stringImageRecognition
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras: Bundle? = data?.extras
            imageBitmap = extras?.getParcelable("data") ?: return
            binding.imageView.setImageBitmap(imageBitmap)
        } else {
            Toast.makeText(requireContext(), "Image capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}
