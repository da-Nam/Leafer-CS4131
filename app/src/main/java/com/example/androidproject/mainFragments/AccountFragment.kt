package com.example.androidproject.mainFragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.androidproject.R
import com.example.androidproject.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.net.URL


class AccountFragment : Fragment() {
    private var _binding:FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val view = binding.root


        return view
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(user != null) {
            user?.let {
                // Name, email address, and profile photo Url
                binding.usernameText.text = it.displayName
                val photoUrl = it.photoUrl
                Glide
                    .with(requireContext())
                    .load(photoUrl)
                    .centerCrop()
                    .into(binding.pfpIm);
            }
        } else {
            binding.pfpIm.setImageResource(R.drawable.ic_default_pfp)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}