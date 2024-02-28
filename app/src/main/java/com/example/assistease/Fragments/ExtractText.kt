package com.example.assistease.Fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.assistease.R
import com.example.assistease.databinding.FragmentExtractTextBinding
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText


class ExtractText : Fragment() {
    private lateinit var binding:FragmentExtractTextBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentExtractTextBinding.inflate(inflater,container,false)
        onGetStartedButtonClicked()
        return binding.root
    }

    private fun onGetStartedButtonClicked() {
        binding.button.setOnClickListener {
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
        //Extract the image
        val bundle = data!!.extras
        val bitmap = bundle!!["data"] as Bitmap?
        binding.CapturedImage.setImageBitmap(bitmap)

        //Create a FirebaseVisionImage object from your image/bitmap.
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap!!)
        val firebaseVision = FirebaseVision.getInstance()
        val firebaseVisionTextRecognizer = firebaseVision.onDeviceTextRecognizer

        //Process the Image
        val task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage)
        task.addOnSuccessListener { firebaseVisionText: FirebaseVisionText ->
            //Set recognized text from image in our TextView
            val text = firebaseVisionText.text
            binding.textView2.text = text
        }
        task.addOnFailureListener { e: Exception -> Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show() }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

}