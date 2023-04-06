package com.example.androidproject.mainFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidproject.MainActivity
import com.example.androidproject.databinding.FragmentPlantBinding
import com.example.androidproject.model.PlantItem
import com.example.androidproject.model.PlantItemList
import com.example.androidproject.plantHandling.PlantDetails
import com.example.androidproject.plantHandling.RecyclerPlantListAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.Serializable


class PlantFragment : Fragment() {
    private var _binding : FragmentPlantBinding? = null
    private val binding get() = _binding!!

    companion object {
        private lateinit var listOPlants: ArrayList<PlantItem>

        fun getPlantNumber() : Int {
            return listOPlants.size
        }
    }
    private lateinit var recyclerView : RecyclerView



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout binding for this fragment
        _binding = FragmentPlantBinding.inflate(inflater, container, false)
        val view = binding.root

        listOPlants = arrayListOf()

        recyclerView = binding.RecyclerPlantListView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecyclerPlantListAdapter(requireContext(), listOPlants) {
            println("I have been clicked uwu!")
            val intent = Intent(requireContext(), PlantDetails::class.java)
            intent.putExtra("plant_object", it)
            startActivity(intent)

        }
        recyclerView.setOnTouchListener { _, _ ->
            (activity as MainActivity).animateFabMenu(false)
            false
        }
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        //println("helloooo uwu")
        binding.progressBar.visibility = View.VISIBLE
        CoroutineScope(IO).launch {
            updateRecyclerView()
        }
    }
    suspend fun updateRecyclerView() {
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val db = Firebase.firestore
        if(currentUser != null) {
            db.collection("users").document(currentUser!!.uid).get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val document = task.result
                        if(document.exists()) {
                            listOPlants.clear()
                            listOPlants.addAll(document.toObject(PlantItemList::class.java)!!.plantList)
                            listOPlants.reverse()
                            recyclerView.adapter!!.notifyDataSetChanged()
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "get failed with ", exception)
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}