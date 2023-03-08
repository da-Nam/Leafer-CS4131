package com.example.androidproject.onboardingFragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(FA: FragmentActivity, private val fragments:List<Fragment>) : FragmentStateAdapter(FA) {
    override fun getItemCount() : Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}