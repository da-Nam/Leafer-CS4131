package com.example.androidproject.mainFragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.androidproject.MainActivity
import com.example.androidproject.R
import com.example.androidproject.databinding.FragmentAccountBinding
import com.example.androidproject.popups.ChangePfpFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.net.URL


class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.nestedSv.setOnTouchListener { _, _ ->
            (activity as MainActivity).animateFabMenu(false)
            false
        }

        binding.changePfpText.setOnClickListener {
            handlePfpChange()
        }
        binding.pfpIm.setOnClickListener {
            handlePfpChange()
        }
        return view
    }

    private fun handlePfpChange() {
        val popup = ChangePfpFragment() {
            updatePfp()
        }
        popup.show((activity as AppCompatActivity).supportFragmentManager, "showChangePfpFragment")
    }
    private fun updatePfp() {
        val user = auth.currentUser
        if (user != null) {
            user?.let {
                // Name, email address, and profile photo Url
                binding.usernameText.text = it.displayName
                val photoUrl = it.photoUrl
                Glide
                    .with(requireContext())
                    .load(photoUrl)
                    .centerCrop()
                    .into(binding.pfpIm)
            }
        } else {
            binding.pfpIm.setImageResource(R.drawable.ic_default_pfp)
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        updatePfp()
        binding.noOfPlant.text = PlantFragment.getPlantNumber().toString()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}