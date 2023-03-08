package com.example.androidproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.androidproject.databinding.ActivityOnboardingBinding
import com.example.androidproject.onboardingFragments.OnboardingAdapter
import com.example.androidproject.onboardingFragments.OnboardingPage1
import com.example.androidproject.onboardingFragments.OnboardingPage2
import com.example.androidproject.onboardingFragments.OnboardingPage3

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var fragmentList : List<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val viewPager = binding.viewPager2
        val indicator = binding.indicator
        viewPager.offscreenPageLimit = 3
        fragmentList = listOf(OnboardingPage1(), OnboardingPage2(), OnboardingPage3())
        viewPager.adapter = OnboardingAdapter(this, fragmentList)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        indicator.setViewPager(viewPager)
    }
}