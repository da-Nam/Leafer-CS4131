package com.example.androidproject.loginActivities

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidproject.databinding.ActivityLoginBinding
import com.example.androidproject.model.PlantItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var loginScreen = true

    private lateinit var logText : TextView
    private lateinit var reText : TextView
    private lateinit var emailText : TextInputEditText
    private lateinit var passText : TextInputEditText
    private lateinit var confirmPass : TextInputEditText
    private lateinit var regisLayout : TextInputLayout
    private lateinit var bannerText : TextView
    private lateinit var confirmBtn : Button


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        auth = Firebase.auth

        logText = binding.logText
        reText = binding.reText

        emailText = binding.emailText
        passText = binding.passwordText
        confirmPass = binding.confirmPassText
        regisLayout = binding.confirmPassLayout
        bannerText = binding.bannerText
        confirmBtn = binding.confirmBtn

        //region Spannable text string for changing between sign in and sign up
        var ss = SpannableString("Don't have an account? Sign-up now!")
        var ss1 = SpannableString("Have an account? Sign-in now!")
        val spanClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                if(loginScreen) {
                    regisLayout.visibility = View.VISIBLE
                    logText.visibility = View.GONE
                    reText.visibility = View.VISIBLE
                    bannerText.text = "Sign-up for Leafer!"
                    confirmBtn.text = "Sign-up"
                    loginScreen = false
                } else {
                    regisLayout.visibility = View.GONE
                    logText.visibility = View.VISIBLE
                    reText.visibility = View.GONE
                    bannerText.text = "Sign-in to Leafer!"
                    confirmBtn.text = "Sign-in"
                    clearFields()
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



        clearFields()
        confirmBtn.setOnClickListener {
            if(emailText.text.toString().isEmpty() || passText.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            } else {
                if(loginScreen) {
                    auth.signInWithEmailAndPassword(emailText.text.toString(), passText.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("FIREBASE_AUTH", "signInWithEmail:success")
                                Toast.makeText(applicationContext, "Signing in...",Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FIREBASE_AUTH", "signInWithEmail:failure", task.exception)
                                Toast.makeText(applicationContext, "Sign-in failed, re-enter the fields correctly!",Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    if(confirmPass.text.toString() == passText.text.toString()) {
                        auth.createUserWithEmailAndPassword(
                            emailText.text.toString(),
                            passText.text.toString()
                        )
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("FIREBASE_AUTH", "createUserWithEmail:success")
                                    loginScreen = true
                                    Toast.makeText(applicationContext, "Success! Signing in...",Toast.LENGTH_SHORT).show()
                                    val intentUDetails = Intent(this, UserDetailsActivity::class.java)
                                    startActivity(intentUDetails)
                                    finish()
                                } else {
                                    // If sign in fails, display a message to the user.
                                    try {
                                        throw task.exception!!
                                    } catch (e: FirebaseAuthWeakPasswordException) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Please enter a stronger password!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Please enter a proper email!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (e: FirebaseAuthUserCollisionException) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Email already exists, user a different one!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (e: Exception) {
                                        Log.w(
                                            "FIREBASE_AUTH",
                                            "createUserWithEmail:failure",
                                            task.exception
                                        )
                                    }
                                }
                            }
                    } else {
                        Toast.makeText(applicationContext, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        view.setOnClickListener(null)
        //hide soft keyboard on touch
        binding.nestedSv.setOnTouchListener { _, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if(imm.isAcceptingText) imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            false
        }
    }
    fun clearFields() {
        emailText.setText("")
        passText.setText("")
        confirmPass.setText("")
    }
}