package com.example.androidproject.loginActivities

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.androidproject.databinding.ActivityUserDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


class UserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var auth: FirebaseAuth

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        val imageURL = binding.imageUrlText
        imageURL.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val uri = Uri.parse(imageURL.text.toString())
                Glide
                    .with(applicationContext)
                    .load(uri)
                    .centerCrop()
                    .into(binding.pfpPreviewIm);
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        binding.confirmBtn.setOnClickListener {
            if(binding.usernameText.text.toString().isNotEmpty() && imageURL.text.toString().isNotEmpty()) {
                val user = auth.currentUser
                val uri = Uri.parse(imageURL.text.toString())
                val profileUpdates = userProfileChangeRequest {
                    displayName = binding.usernameText.text.toString()
                    photoUri = uri
                }
                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            finish()
                        }
                    }
            } else {
                Toast.makeText(applicationContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        binding.nestedSv.setOnTouchListener { _, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if(imm.isAcceptingText) imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            false
        }
    }
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}