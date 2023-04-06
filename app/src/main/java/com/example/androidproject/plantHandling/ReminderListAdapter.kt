package com.example.androidproject.plantHandling

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidproject.R
import com.google.android.material.materialswitch.MaterialSwitch


class ReminderListAdapter(private val context : Context, private val reminderList : ArrayList<String>, private val sharedPreferences: SharedPreferences,
                          private val reminderActivatedListener: (String) -> Unit)
    : RecyclerView.Adapter<ReminderListAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val reminderItem : View) : RecyclerView.ViewHolder(reminderItem) {
        private val reminderName : TextView = reminderItem.findViewById(R.id.reminder_list_item_name)
        private val reminderSwitch : MaterialSwitch = reminderItem.findViewById(R.id.reminder_list_item_toggle)

        fun theAllBinding(reminder : String) {
            reminderName.text = reminder
            reminderSwitch.isChecked = sharedPreferences.getBoolean(reminder, false)
            reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    reminderActivatedListener(reminder)
                    sharedPreferences.edit().putBoolean(reminder, true).apply()
                } else {
                    sharedPreferences.edit().putBoolean(reminder, false).apply()
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.reminder_card_layout, parent, false)
        return ItemViewHolder(v)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val daReminder = reminderList[position]
        holder.theAllBinding(daReminder)
    }

    override fun getItemCount() = reminderList.size
}