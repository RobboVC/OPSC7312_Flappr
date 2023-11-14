package com.example.opsc7312_flappr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotableBirdAdapter(private var birdList: List<NotableObservations>) :
    RecyclerView.Adapter<NotableBirdAdapter.BirdViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notable_bird, parent, false)
        return BirdViewHolder(view)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        val bird = birdList[position]
        holder.bind(bird)
    }

    override fun getItemCount(): Int = birdList.size

    class BirdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val commonNameTextView: TextView = itemView.findViewById(R.id.tvCommonName)
        private val scientificNameTextView: TextView = itemView.findViewById(R.id.tvScientificName)

        fun bind(bird: NotableObservations) {
            commonNameTextView.text = bird.comName
            scientificNameTextView.text = bird.sciName
        }
    }

    fun updateData(newList: List<NotableObservations>) {
        birdList = newList
        notifyDataSetChanged()
    }
}