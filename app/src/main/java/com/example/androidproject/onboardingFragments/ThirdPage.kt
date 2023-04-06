package com.example.androidproject.onboardingFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidproject.MainActivity
import com.example.androidproject.R
import com.example.androidproject.databinding.FragmentThirdPageBinding


class ThirdPage : Fragment() {
    private var _binding : FragmentThirdPageBinding? = null;
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThirdPageBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.startButton.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}