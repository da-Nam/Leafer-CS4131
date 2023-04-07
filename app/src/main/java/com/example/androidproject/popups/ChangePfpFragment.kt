package com.example.androidproject.popups

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.androidproject.databinding.FragmentChangePfpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class ChangePfpFragment(private val pfpHandle: (Int) -> Unit) : DialogFragment() {
    private var _binding : FragmentChangePfpBinding? = null
    private val binding get() = _binding!!

    private var pickedImage : Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePfpBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.setCanceledOnTouchOutside(true);
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        auth = Firebase.auth
        storage = Firebase.storage


        val pfpbtn = binding.pfpAdd

        pfpbtn.setOnClickListener {
            val daIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(daIntent)
        }



        /*
        confirmBtn.setOnClickListener {
            if(imageUrl.text.toString().isNotEmpty()) {
                val user = auth.currentUser
                val uri = Uri.parse(imageUrl.text.toString())
                val profileUpdates = userProfileChangeRequest {
                    photoUri = uri
                }
                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            pfpHandle(0)
                            dismiss()
                        }
                    }
            } else {
                dismiss()
                Toast.makeText(requireContext(), "No url entered!", Toast.LENGTH_SHORT).show()
            }
        }
         */
        return view
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {


            val data: Intent? = result.data
            pickedImage = data?.data
            if (pickedImage != null){
                val storageRef = storage.reference
                val currentUser = auth.currentUser

                val pfpRef = storageRef.child("pfp_images/${currentUser!!.uid}")
                val uploadTask = pfpRef.putFile(pickedImage!!)

                val urlTask = uploadTask.continueWithTask {task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    Toast.makeText(requireContext(), "Updating profile picture", Toast.LENGTH_SHORT).show()
                    pfpRef.downloadUrl
                }.addOnCompleteListener { taskst ->
                    if (taskst.isSuccessful) {
                        val downloadUri = taskst.result
                        val profileUpdates = userProfileChangeRequest {
                            photoUri = downloadUri
                        }
                        currentUser.updateProfile(profileUpdates)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    pfpHandle(0)
                                    dismiss()
                                }
                            }

                    } else {
                        Toast.makeText(requireContext(), "Failed to upload pfp image", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}