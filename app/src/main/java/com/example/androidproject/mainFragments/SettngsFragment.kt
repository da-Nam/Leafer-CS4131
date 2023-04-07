package com.example.androidproject.mainFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.androidproject.OnboardingActivity
import com.example.androidproject.R
import com.example.androidproject.databinding.FragmentSettngsBinding
import com.example.androidproject.popups.ChangePfpFragment
import com.example.androidproject.popups.ChangeUsernameFragment

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
            val intent = Intent(requireActivity(), OnboardingActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            requireActivity().finish()
        }
        binding.changeUsernameBtn.setOnClickListener {
            val popup = ChangeUsernameFragment()
            popup.show((activity as AppCompatActivity).supportFragmentManager, "showChangeUsernameFragment")
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}