package com.example.androidproject.plantCameraActivities

import android.app.AlarmManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidproject.databinding.ActivityPlantConfirmationBinding
import com.example.androidproject.model.tools.Souper
import java.io.FileInputStream


class PlantConfirmationActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPlantConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantConfirmationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //val byteArray = intent.getByteArrayExtra("byteArray")
        //val bitmapIm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        val rotationDegree = intent.getFloatExtra("imageRotation", 0f)
        val recyclerView = binding.confirmRecyclerview

        //binding.plantImage.setImageBitmap(bitmapIm)
        //binding.plantImage.rotation = rotationDegree
        var bitmapIm: Bitmap? = null
        val filename = intent.getStringExtra("imageTaken")
        try {
            val `is`: FileInputStream = openFileInput(filename)
            bitmapIm = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.plantImage.setImageBitmap(bitmapIm)
        binding.plantImage.rotation = rotationDegree

        var plantDetectedList : ArrayList<Pair<String, Pair<Int, Uri>>> = ArrayList()

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        for(i in 0 until 3) {
            val name = intent.getStringExtra("name$i")!!
            var url = ""
                url = Souper.getGoogImageUri(name)!!
            plantDetectedList.add(Pair(name, Pair(intent.getIntExtra("percent$i",0), Uri.parse(url))))
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerConfirmationAdapter(applicationContext, plantDetectedList) {
            println("$it was chosen")
        }
    }
}