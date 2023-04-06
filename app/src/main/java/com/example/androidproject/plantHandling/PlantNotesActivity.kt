package com.example.androidproject.plantHandling

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.androidproject.databinding.ActivityPlantNotesBinding
import com.example.androidproject.model.PlantItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PlantNotesActivity : AppCompatActivity() {

    private lateinit var daPlant : PlantItem
    private lateinit var binding : ActivityPlantNotesBinding
    private var notesChanged = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityPlantNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        daPlant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("plant_object_notes", PlantItem::class.java)
        } else {
            intent.getParcelableExtra<PlantItem>("plant_object_notes")
        }!!
        binding.userPlantNotes.setText(daPlant.note)
        binding.plantNameNotesText.text = "Notes: ${daPlant.name}"

        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val db = Firebase.firestore

        binding.userPlantNotes.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                notesChanged = true
            }
            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        binding.notesReturnBtn.setOnClickListener {
            if(notesChanged) {
                AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Unsaved changes have been made")
                    .setMessage("Do you want to save before returning?")
                    .setPositiveButton("Yes") { _, _ ->
                        var newDaPlant = PlantItem(daPlant.name, daPlant.url)
                        if (binding.userPlantNotes.text.isNotEmpty()) {
                            newDaPlant =
                                PlantItem(
                                    daPlant.name,
                                    daPlant.url,
                                    binding.userPlantNotes.text.toString()
                                )
                        }
                        val userPlantsRef = db.collection("users").document(currentUser!!.uid)
                        userPlantsRef.update("plantList", FieldValue.arrayRemove(daPlant))
                        userPlantsRef.update("plantList", FieldValue.arrayUnion(newDaPlant))
                        finish()
                    }
                    .setNegativeButton("No") {_, _ ->
                        finish()
                    }
                    .show()
            } else {
                finish()
            }
        }

        binding.buttoningSaveNotes.setOnClickListener {
            var newDaPlant = PlantItem(daPlant.name, daPlant.url)
            if(binding.userPlantNotes.text.isNotEmpty()) {
                newDaPlant =
                    PlantItem(daPlant.name, daPlant.url, binding.userPlantNotes.text.toString())
            }
            val userPlantsRef = db.collection("users").document(currentUser!!.uid)
            userPlantsRef.update("plantList", FieldValue.arrayRemove(daPlant))
            userPlantsRef.update("plantList", FieldValue.arrayUnion(newDaPlant))
            notesChanged = false
        }

    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(imm.isAcceptingText) imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        return true
    }
}