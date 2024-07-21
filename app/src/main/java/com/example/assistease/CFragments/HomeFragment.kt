package com.example.assistease.CFragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.assistease.R
import com.example.assistease.databinding.FragmentHome2Binding
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHome2Binding
    private val REQUEST_CODE_SPEECH_INPUT = 100
    private val REQUEST_CALL_PERMISSION = 101
    private val REQUEST_SMS_PERMISSION = 102
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= FragmentHome2Binding.inflate(inflater,container,false)
        voiceControl()
        objectDetection()
        textReader()
        audioNavigation()
        settings()
        feedback()
        onVoiceClicked()
        binding.imageView2.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment2_to_settings2)
        }
        return binding.root
    }

    private fun onVoiceClicked() {
        binding.voice.setOnClickListener {
            startVoiceRecognition()
        }
    }
    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    } override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == AppCompatActivity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val voiceCommand = result?.get(0)
                    executeVoiceCommand(voiceCommand)
                }
            }
        }
    }
    // Execute voice command
    private fun executeVoiceCommand(voiceCommand: String?) {
        when  {
            voiceCommand!!.startsWith("make a call to ", ignoreCase = true) -> {
                val contactName = voiceCommand.substringAfter("make a call to ").trim()
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_CONTACTS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        REQUEST_CALL_PERMISSION
                    )
                } else {
                    fetchContactAndMakeCall(contactName)
                }
            }
            voiceCommand!!.startsWith("send ", ignoreCase = true) && voiceCommand.contains(
                " to ",
                ignoreCase = true
            ) -> {
                val startIndex = voiceCommand.indexOf("send ") + "send ".length
                val endIndex = voiceCommand.indexOf(" to ", ignoreCase = true)
                val smsBody = voiceCommand.substring(startIndex, endIndex).trim()
                val contactName = voiceCommand.substring(endIndex + " to ".length).trim()
                val phoneNumber = getPhoneNumberForContact(contactName)
                if (!phoneNumber.isNullOrEmpty()) { // Check if phone number is not null or empty
                    sendSMS(phoneNumber, smsBody)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Phone number not found for $contactName",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }voiceCommand!!.startsWith("navigate to ", ignoreCase = true) -> {
            val destination = voiceCommand.substringAfter("navigate to ").trim()
            navigateToFragment(destination)
        }
            // Add more commands as needed
            else -> Toast.makeText(requireContext(), "Command not recognized", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToFragment(fragmentName: String) {
        when (fragmentName.toLowerCase(Locale.getDefault())) {
            "feedback" -> findNavController().navigate(R.id.action_homeFragment2_to_feedbackFragment)
            "settings" -> findNavController().navigate(R.id.action_homeFragment2_to_settings2)
            "audionavigation" -> findNavController().navigate(R.id.action_homeFragment2_to_audioNavigation2)
            "textreader" -> findNavController().navigate(R.id.action_homeFragment2_to_textReader)
            "objectdetection" -> findNavController().navigate(R.id.action_homeFragment2_to_objectDetection2)
            "voicecontrol" -> findNavController().navigate(R.id.action_homeFragment2_to_voiceControl2)
            else -> Toast.makeText(requireContext(), "Fragment not found: $fragmentName", Toast.LENGTH_SHORT).show()
        }
    }
    // Make a call
    private fun fetchContactAndMakeCall(contactName: String) {
        val contactId = getContactIdByName(contactName)
        if (contactId != null) {
            val phoneNumber = getPhoneNumberByContactId(contactId)
            if (!phoneNumber.isNullOrEmpty()) {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$phoneNumber")
                startActivity(callIntent)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Phone number not found for $contactName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(requireContext(), "Contact not found: $contactName", Toast.LENGTH_SHORT).show()
        }
    }
    @SuppressLint("Range")
    private fun getContactIdByName(contactName: String): String? {
        val contentResolver = requireActivity().contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} = ?",
            arrayOf(contactName),
            null
        )
        var contactId: String? = null
        cursor?.use {
            if (it.moveToFirst()) {
                contactId = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
            }
        }
        cursor?.close()
        return contactId
    }
    @SuppressLint("Range")
    private fun getPhoneNumberByContactId(contactId: String): String? {
        val contentResolver = requireActivity().contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )
        var phoneNumber: String? = null
        cursor?.use {
            if (it.moveToFirst()) {
                phoneNumber =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        }
        cursor?.close()
        return phoneNumber
    }
    @SuppressLint("Range")
    private fun getPhoneNumberForContact(contactName: String): String? {
        val contentResolver = requireActivity().contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} = ?",
            arrayOf(contactName),
            null
        )
        var phoneNumber: String? = null
        cursor?.use {
            if (it.moveToFirst()) {
                phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        }
        cursor?.close()
        return phoneNumber
    }

    private fun sendSMS(recipient: String, message: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.SEND_SMS),
                REQUEST_SMS_PERMISSION
            )
        } else {
            val smsIntent = Intent(Intent.ACTION_SENDTO)
            smsIntent.data = Uri.parse("smsto:$recipient")
            smsIntent.putExtra("sms_body", message)
            if (recipient!=null){
                startActivity(smsIntent)
            }
        }
    }

    private fun feedback() {
        binding.giveFeedback.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment2_to_feedbackFragment)
        }
    }

    private fun settings() {
        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment2_to_settings2)
        }
    }

    private fun audioNavigation() {
        binding.audioNavigation.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment2_to_audioNavigation2)
        }
    }

    private fun textReader() {
        binding.imageTextReader.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment2_to_textReader)
        }
    }

    private fun objectDetection() {
        binding.objectDetection.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment2_to_objectDetection2)
        }
    }

    private fun voiceControl() {
        binding.voiceControl.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment2_to_voiceControl2)
        }
    }



}