package com.example.androidproject

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.androidproject.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var fabOpened = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        setContentView(view)

      //if(sharedPreferences.getBoolean("first_time", true)) {
      //     sharedPreferences.edit().putBoolean("first_time", false).commit()
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
        //}
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        setSupportActionBar(binding.bottomAppToolBar)
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.fab.setOnClickListener {
            animateFabMenu(true)
        }
        binding.root.setOnClickListener {
            animateFabMenu(false)
        }
    }
    private fun animateFabMenu(b : Boolean) {
        val rotationAnimator = ObjectAnimator.ofFloat(binding.fab, "rotation", 0f, 45f)
        rotationAnimator.duration = 250L
        val addPlantAnimator = ObjectAnimator.ofFloat(binding.addPlant, "translationY", 0f, -360f)
        addPlantAnimator.duration = 350L
        val scanPlantAnimator = ObjectAnimator.ofFloat(binding.scanPlant, "translationY", 0f, -550f)
        scanPlantAnimator.duration = 350L
        val animatorSet = AnimatorSet()
        animatorSet.play(addPlantAnimator).with(scanPlantAnimator).after(rotationAnimator)

        if(!fabOpened && b) {
            animatorSet.start()

            fabOpened = true

            Handler(Looper.getMainLooper()).postDelayed({
                binding.addPlant.visibility = View.VISIBLE
                binding.scanPlant.visibility = View.VISIBLE
            }, 200)
        } else if(fabOpened){
                animatorSet.reverse()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.addPlant.visibility = View.GONE
                    binding.scanPlant.visibility = View.GONE
                }, 150)
            fabOpened = false
        }
    }

}