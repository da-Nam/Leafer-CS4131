package com.example.androidproject

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.androidproject.databinding.ActivityMainBinding
import com.example.androidproject.loginActivities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var fabOpened = false
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        sharedPreferences = getSharedPreferences("Main_Onboard_Pref", MODE_PRIVATE)
        setContentView(view)

        if(sharedPreferences.getBoolean("first_time", true)) {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.addPlantFab.setOnClickListener {

        }
        binding.scanPlantFab.setOnClickListener {
        }

        //get Firebase auth
        auth = Firebase.auth

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        setSupportActionBar(binding.bottomAppToolBar)
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.fab.setOnClickListener {
            animateFabMenu(true)
        }
        view.setOnClickListener {
            animateFabMenu(false)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser == null && !(sharedPreferences.getBoolean("first_time", true))){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun animateFabMenu(b : Boolean) {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val height = metrics.heightPixels
        val rotationAnimator = ObjectAnimator.ofFloat(binding.fab, "rotation", 0f, 45f)
        rotationAnimator.duration = 250L
        val addPlantAnimator = ObjectAnimator.ofFloat(binding.addPlant, "translationY", 0f, (-((height.toFloat()/binding.fab.height.toFloat())* 25)))
        addPlantAnimator.duration = 350L
        val scanPlantAnimator = ObjectAnimator.ofFloat(binding.scanPlant, "translationY", 0f, (-((height.toFloat()/binding.fab.height.toFloat())* 37)))
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