package com.example.androidproject.popups

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.androidproject.databinding.FragmentChangePfpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


class ChangePfpFragment(private val pfpHandle: (Int) -> Unit) : DialogFragment() {
    private var _binding : FragmentChangePfpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePfpBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.setCanceledOnTouchOutside(true);
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val auth = Firebase.auth

        val imageUrl = binding.imageUrlText
        val confirmBtn = binding.confirmBtn

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
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}