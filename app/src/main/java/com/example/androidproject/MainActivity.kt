package com.example.androidproject

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.androidproject.databinding.ActivityMainBinding
import com.example.androidproject.loginActivities.LoginActivity
import com.example.androidproject.plantCameraActivities.AddPlantsActivity
import com.example.androidproject.plantCameraActivities.ScanPlantsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var fabOpened = false
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        sharedPreferences = getSharedPreferences("Main_Onboard_Pref", MODE_PRIVATE)
        setContentView(view)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100);
        }

        auth = Firebase.auth

        if (sharedPreferences.getBoolean("first_time", true)) {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }

        if(isNetworkConnected()) {
            findViewById<CoordinatorLayout>(R.id.content_main_view).visibility = View.VISIBLE
            binding.fab.isClickable = true
            binding.bottomNavigationView.visibility = View.VISIBLE
            binding.noInternetLayout.visibility = View.GONE

            binding.addPlantFab.setOnClickListener {
                val intent = Intent(this, AddPlantsActivity::class.java)
                startActivity(intent)
                animateFabMenu(false)
            }
            binding.scanPlantFab.setOnClickListener {
                val intent = Intent(this, ScanPlantsActivity::class.java)
                startActivity(intent)
                animateFabMenu(false)
            }


            //get Firebase auth
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            setSupportActionBar(binding.bottomAppToolBar)
            binding.bottomNavigationView.setupWithNavController(navController)

            binding.fab.setOnClickListener {
                animateFabMenu(true)
            }
            view.setOnClickListener {
                animateFabMenu(false)
            }
        } else {
            findViewById<CoordinatorLayout>(R.id.content_main_view).visibility = View.GONE
            binding.fab.isClickable = false
            binding.bottomNavigationView.visibility = View.GONE
            binding.noInternetLayout.visibility = View.VISIBLE

            binding.reloadWifiBtn.setOnClickListener() {
                recreate()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(isNetworkConnected()) {
            val currentUser = auth.currentUser
            if (currentUser == null && !(sharedPreferences.getBoolean("first_time", true))) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }


    fun animateFabMenu(b: Boolean) {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val height = metrics.heightPixels
        val rotationAnimator = ObjectAnimator.ofFloat(binding.fab, "rotation", 0f, 45f)
        rotationAnimator.duration = 250L
        val addPlantAnimator = ObjectAnimator.ofFloat(
            binding.addPlant,
            "translationY",
            0f,
            (-((height.toFloat() / binding.fab.height.toFloat()) * 25))
        )
        addPlantAnimator.duration = 350L
        val scanPlantAnimator = ObjectAnimator.ofFloat(
            binding.scanPlant,
            "translationY",
            0f,
            (-((height.toFloat() / binding.fab.height.toFloat()) * 37))
        )
        scanPlantAnimator.duration = 350L
        val animatorSet = AnimatorSet()
        animatorSet.play(addPlantAnimator).with(scanPlantAnimator).after(rotationAnimator)



        if (!fabOpened && b) {
            animatorSet.start()
            fabOpened = true

            binding.shadowView.apply {
                alpha = 0f
                visibility = View.VISIBLE

                animate().alpha(0.4f).duration = 250
            }

            Handler(Looper.getMainLooper()).postDelayed({
                binding.addPlant.visibility = View.VISIBLE
                binding.scanPlant.visibility = View.VISIBLE
            }, 200)
        } else if (fabOpened) {
            animatorSet.reverse()
            fabOpened = false

            binding.shadowView.apply {
                visibility = View.VISIBLE
                alpha = 0.4f

                animate().alpha(0f).setDuration(250).withEndAction {
                    visibility = View.GONE
                }
            }

            Handler(Looper.getMainLooper()).postDelayed({
                binding.addPlant.visibility = View.GONE
                binding.scanPlant.visibility = View.GONE
            }, 150)
        }
    }
    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Really Exit?")
            .setMessage("Are you sure you want to exit application?")
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which -> finish() })
            .setNegativeButton("No", null)
            .show()
    }

    //Checks if network is connected
    private fun isNetworkConnected(): Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }
}