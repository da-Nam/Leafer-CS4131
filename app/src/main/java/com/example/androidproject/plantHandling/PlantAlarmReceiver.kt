package com.example.androidproject.plantHandling

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.example.androidproject.MainActivity
import java.util.*


class PlantAlarmReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {


        val ii = Intent(context, MainActivity::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent =  PendingIntent.getActivity(context, 0, ii, FLAG_IMMUTABLE)









        val vibrator: Vibrator = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))



        val reminderName = intent!!.extras!!.getString("reminder_name")
        val url = intent!!.extras!!.getString("url")
        val plantName = intent!!.extras!!.getString("da_plant_namee")
        val isdaname = "${plantName!!.substring(0, 1).uppercase(Locale.ROOT)}${
            plantName!!.substring(1).lowercase(Locale.ROOT)
        }"






        Toast.makeText(context, "$reminderName UWU!", Toast.LENGTH_LONG).show()


        var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        val ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone.play()



        val notification = Notification.Builder(context!!, url)
            .setContentTitle("$reminderName: $isdaname")
            .setContentText("Remember about your $isdaname")
            .setAutoCancel(true)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setChannelId(url).build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, notification)
    }

}