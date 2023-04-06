package com.example.androidproject.plantHandling

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidproject.databinding.ActivityPlantDetailsBinding
import com.example.androidproject.model.PlantItem
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class PlantDetails : AppCompatActivity() {
    private lateinit var binding : ActivityPlantDetailsBinding
    private lateinit var daPlant : PlantItem
    private lateinit var alarmManager : AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var recyclerView : RecyclerView
    private lateinit var reminderList: ArrayList<String>
    private lateinit var preferencesShared : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        daPlant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("plant_object", PlantItem::class.java)
        } else {
            intent.getParcelableExtra<PlantItem>("plant_object")
        }!!

        binding.leaveBtn.setOnClickListener {
            finish()
        }

        binding.openPlantNotesBtn.setOnClickListener {

        }
        val isdaname = "${daPlant.name!!.substring(0, 1).uppercase(Locale.ROOT)}${
            daPlant.name!!.substring(1).lowercase(Locale.ROOT)
        }"
        binding.plantName.text = isdaname
        Glide
            .with(applicationContext)
            .load(daPlant.url)
            .centerCrop()
            .into(binding.imagePlant)

        binding.wikiBtn.setOnClickListener {
            val webURL = "https://en.wikipedia.org/wiki/$isdaname"
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(webURL))
            startActivity(intent)
        }
        reminderList = arrayListOf("Water Plant", "Change plant soil")

        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val db = Firebase.firestore

        binding.plantNotes.movementMethod = ScrollingMovementMethod()

        binding.deleteButton.setOnClickListener {
            val userPlantsRef = db.collection("users").document(currentUser!!.uid)
            userPlantsRef.update("plantList", FieldValue.arrayRemove(daPlant))
            finish()
        }

        binding.openPlantNotesBtn.setOnClickListener {
            val notesintent = Intent(this, PlantNotesActivity::class.java)
            notesintent.putExtra("plant_object_notes", daPlant)
            startActivity(notesintent)
            finish()
        }

        preferencesShared = getSharedPreferences(daPlant.url!!.split("/")[3], 0);


        recyclerView = binding.remindersList
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ReminderListAdapter(this, reminderList, preferencesShared) { name ->
            dateAndTimeSelection(name)
            if(!preferencesShared.getBoolean(name, false)) {
                if(::pendingIntent.isInitialized && ::alarmManager.isInitialized) {
                    alarmManager.cancel(pendingIntent)
                }
            }
        }
        createNotificationChannel(daPlant.url!!, isdaname, "Channel for $isdaname reminders")
    }
    override fun onStart() {
        super.onStart()
        binding.plantNotes.text = daPlant.note
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun dateAndTimeSelection(reminderName : String) {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .build()
            val timePicker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(10)
                    .setInputMode(INPUT_MODE_CLOCK)
                    .setTitleText("Select time")
                    .build()
            datePicker.show(supportFragmentManager, "plantAlarm");
            datePicker.addOnPositiveButtonClickListener {
                // Respond to positive button click.
                println("positive button")
                val datee = formatter.format(it)
                //println(datee)
                timePicker.show(supportFragmentManager, "plantAlarm")
                timePicker.isCancelable = false
                timePicker.addOnPositiveButtonClickListener {
                    val pickedDateTime = Calendar.getInstance()
                    pickedDateTime.timeInMillis = System.currentTimeMillis()
                    pickedDateTime.clear()
                    val a = datee.split("/")
                    pickedDateTime.set(
                        a[2].toInt(),
                        a[1].toInt() - 1,
                        a[0].toInt(),
                        timePicker.hour,
                        timePicker.minute
                    )
                    pickedDateTime.set(Calendar.SECOND, 0)
                    val intent = Intent(this, PlantAlarmReceiver::class.java)
                    intent.putExtra("reminder_name", reminderName)
                    intent.putExtra("url", daPlant.url!!)
                    intent.putExtra("da_plant_namee", daPlant.name!!)
                    pendingIntent = PendingIntent.getBroadcast(this, Random().nextInt(), intent, FLAG_IMMUTABLE)
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, pickedDateTime.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                    Toast.makeText(applicationContext, "Plant Alarm On", Toast.LENGTH_SHORT).show()
                }
            }
            datePicker.addOnCancelListener() {
                Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_SHORT).show()
                preferencesShared.edit().putBoolean(reminderName, false).apply()
                recyclerView.adapter!!.notifyDataSetChanged()
            }
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_SHORT).show()
                preferencesShared.edit().putBoolean(reminderName, false).apply()
                recyclerView.adapter!!.notifyDataSetChanged()
            }
    }
    private fun createNotificationChannel(id: String, name: String, description: String){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)
        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager.createNotificationChannel(channel)
    }
    /*
    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(this,  { _, year, month, day ->
            TimePickerDialog(this,  { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                println(month)
                pickedDateTime.set(year, month, day, hour, minute)
                //doSomethingWith(pickedDateTime)
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

     */

}