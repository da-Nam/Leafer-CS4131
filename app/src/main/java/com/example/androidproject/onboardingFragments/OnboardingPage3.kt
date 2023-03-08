package com.example.androidproject.onboardingFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidproject.R
import com.example.androidproject.databinding.FragmentOnboardingPage3Binding


class OnboardingPage3 : Fragment() {
    private var _binding : FragmentOnboardingPage3Binding? = null
    private val binding = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingPage3Binding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}