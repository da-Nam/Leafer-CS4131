package com.example.androidproject.mainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidproject.R
import com.example.androidproject.databinding.FragmentSettngsBinding

class SettngsFragment : Fragment() {
    private var _binding : FragmentSettngsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettngsBinding.inflate(inflater, container, false)
        val view  = binding.root
        binding.aboutApplicationBtn.setOnClickListener {

        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}