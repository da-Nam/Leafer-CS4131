package com.example.androidproject.loginActivities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.androidproject.databinding.ActivityUserDetailsBinding
import com.example.androidproject.model.PlantItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class UserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    private var pickedImage : Uri? = null
    private var pickedBitmap : Bitmap? = null

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            pickedImage = data?.data

            if (pickedImage != null){
                if (Build.VERSION.SDK_INT >= 28)
                {
                    val source = ImageDecoder.createSource(this.contentResolver,pickedImage!!)
                    pickedBitmap = ImageDecoder.decodeBitmap(source)
                    val imageSelect = binding.pfpPreviewIm
                    imageSelect.setImageBitmap(pickedBitmap)
                }
                else
                {
                    pickedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,pickedImage)
                    val imageSelect = binding.pfpPreviewIm
                    imageSelect.setImageBitmap(pickedBitmap)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 200);
        }

        auth = Firebase.auth
        storage = Firebase.storage

        /*
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
         */

        binding.pfpAdd.setOnClickListener {
                val daIntent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                resultLauncher.launch(daIntent)
        }

        val currentUser = auth.currentUser
        val db = Firebase.firestore

        binding.confirmBtn.setOnClickListener {
            val storageRef = storage.reference

            val pfpRef = storageRef.child("pfp_images/${currentUser!!.uid}")
            val uploadTask = pfpRef.putFile(pickedImage!!)

            val urlTask = uploadTask.continueWithTask {task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                pfpRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    updateProfile(downloadUri)
                } else {
                    Toast.makeText(applicationContext, "Failed to upload pfp image", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.nestedSv.setOnTouchListener { _, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if(imm.isAcceptingText) imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            false
        }
    }
    fun updateProfile(uri : Uri) {
        val currentUser = auth.currentUser
        val db = Firebase.firestore
        if(binding.usernameText.text.toString().isNotEmpty()) {
            val user = auth.currentUser
            val profileUpdates = userProfileChangeRequest {
                displayName = binding.usernameText.text.toString()
                photoUri = uri
            }
            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userPlantData = hashMapOf(
                            "plantList" to arrayListOf<PlantItem>()
                        )
                        db.collection("users").document(currentUser!!.uid).set(userPlantData)
                        finish()
                    }
                }
        } else {
            Toast.makeText(applicationContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}