package com.example.androidproject.plantHandling

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidproject.R
import com.example.androidproject.model.PlantItem
import java.util.*
import kotlin.collections.ArrayList

class RecyclerPlantListAdapter(private val context : Context, private val plantList : ArrayList<PlantItem>,
                               private val plantChosenListener: (PlantItem) -> Unit)
    : RecyclerView.Adapter<RecyclerPlantListAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(private val plantItemView : View) : RecyclerView.ViewHolder(plantItemView) {
        private val plantImage : ImageView = plantItemView.findViewById(R.id.plant_list_image)
        private val plantName : TextView = plantItemView.findViewById(R.id.plant_list_name)
        private val plantBtn : ImageButton = plantItemView.findViewById(R.id.plant_list_btn)

        fun theAllBinding(plant : PlantItem) {
            val isdaname = "${plant.name!!.substring(0, 1).uppercase(Locale.ROOT)}${plant.name!!.substring(1).lowercase(Locale.ROOT)}"
            plantName.text = isdaname;
            Glide
                .with(context)
                .load(plant.url)
                .centerCrop()
                .into(plantImage)
            plantBtn.setOnClickListener {
                plantChosenListener(PlantItem())
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.plant_card_layout, parent, false)
        return ItemViewHolder(v)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val daPlant = plantList[position]
        holder.theAllBinding(daPlant)
    }

    override fun getItemCount() = plantList.size
}