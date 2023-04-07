package com.example.androidproject.popups

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.androidproject.databinding.FragmentChangePfpBinding
import com.example.androidproject.databinding.FragmentChangeUsernameBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class ChangeUsernameFragment: DialogFragment() {
    private var _binding : FragmentChangeUsernameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeUsernameBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.setCanceledOnTouchOutside(true);
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val auth = Firebase.auth
        val user = auth.currentUser

        val username = binding.usernameText
        val currentUsernameConfirm = binding.reenterUsernameText
        val confirmBtn = binding.confirmBtn

        confirmBtn.setOnClickListener {
            if(username.text!!.isNotEmpty() && currentUsernameConfirm.text!!.isNotEmpty()) {
                if(currentUsernameConfirm.text.toString() == user?.displayName) {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = username.text.toString()
                    }
                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "Username changed!", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Current usernames don't match!", Toast.LENGTH_SHORT).show()

                }

            } else {
                Toast.makeText(requireContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}