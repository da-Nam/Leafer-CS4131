package com.example.androidproject.loginActivities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.androidproject.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.log


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var loginScreen = true


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        auth = Firebase.auth
        val logText = binding.logText
        val reText = binding.reText
        val emailText = binding.emailText
        val passText = binding.passwordText
        val confirmPass = binding.confirmPassText
        val regisLayout = binding.confirmPassLayout
        val bannerText = binding.bannerText
        val confirmBtn = binding.confirmBtn

        //region Spannable text string for changing between sign in and sign up
        var ss = SpannableString("Don't have an account? Sign up now!")
        var ss1 = SpannableString("Have an account? Sign in now!")
        val spanClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                if(loginScreen) {
                    regisLayout.visibility = View.VISIBLE
                    logText.visibility = View.GONE
                    reText.visibility = View.VISIBLE
                    bannerText.text = "Sign up to Leafer!"
                    confirmBtn.text = "Sign up"
                    loginScreen = false
                } else {
                    regisLayout.visibility = View.GONE
                    logText.visibility = View.VISIBLE
                    reText.visibility = View.GONE
                    bannerText.text = "Sign in to Leafer!"
                    confirmBtn.text = "Sign in"
                    loginScreen = true
                }
            }
        }
        ss.setSpan(spanClick, 23, ss.length-1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        logText.text = ss
        logText.movementMethod = LinkMovementMethod.getInstance()
        ss1.setSpan(spanClick, 17, ss1.length-1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        reText.text = ss1
        reText.movementMethod = LinkMovementMethod.getInstance()
        //endregion


        confirmBtn.setOnClickListener {
            println(binding.emailText.text)
        }
        //hide soft keyboard on touch
        binding.nestedSv.setOnTouchListener { _, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if(imm.isAcceptingText) imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            false
        }
    }

}