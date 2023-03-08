package com.example.androidproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.androidproject.databinding.ActivityMainBinding

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

    }
}