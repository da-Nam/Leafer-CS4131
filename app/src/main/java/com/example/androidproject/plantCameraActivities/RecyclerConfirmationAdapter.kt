package com.example.androidproject.plantCameraActivities

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidproject.R
import com.example.androidproject.model.tools.Souper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class RecyclerConfirmationAdapter(private val context : Context, private val recogList: ArrayList<Pair<String, Pair<Int, Uri>>>,
                                  private val plantChosenListener: (String) -> Unit)
    : RecyclerView.Adapter<RecyclerConfirmationAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(private val confirmItemView : View) : RecyclerView.ViewHolder(confirmItemView) {
        val plantImage : ImageView = confirmItemView.findViewById(R.id.confirm_plant_im)
        val plantName : TextView = confirmItemView.findViewById(R.id.confirm_plant_name)
        val plantPercent : TextView = confirmItemView.findViewById(R.id.confirm_plant_percent)

        fun theAllBinding(plant : Pair<String, Pair<Int, Uri>>) {
            //Get a random plant name image onto confirmation list

            Glide
                .with(context)
                .load(plant.second.second)
                .centerCrop()
                .into(plantImage)

            plantName.text = plant.first
            plantPercent.text = "Percentage: ${plant.second.first}%"

            itemView.setOnClickListener {
                plantChosenListener(plant.first)
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.confirmation_plant_layout, parent, false)
        return ItemViewHolder(v)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val possiblePlant = recogList[position]
        holder.theAllBinding(possiblePlant)

    }

    override fun getItemCount() = recogList.size
}