package com.example.androidproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.androidproject.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
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
        binding.bottomNavigationView.bringToFront()
        binding.fab.setOnClickListener {

        }
    }
}