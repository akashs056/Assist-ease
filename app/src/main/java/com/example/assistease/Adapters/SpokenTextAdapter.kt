package com.example.assistease.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assistease.R

class SpokenTextAdapter(val onDeleteClickListener: (Any) -> Unit) : RecyclerView.Adapter<SpokenTextAdapter.SpokenTextViewHolder>() {

    private val spokenTextList = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpokenTextViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sample_speech_to_text, parent, false)
        return SpokenTextViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpokenTextViewHolder, position: Int) {
        holder.bind(spokenTextList[position])
        holder.delete.setOnClickListener {
            onDeleteClickListener.invoke(position)
        }
    }

    override fun getItemCount(): Int = spokenTextList.size

    fun addSpokenText(spokenText: String) {
        spokenTextList.add(spokenText)
        notifyItemInserted(spokenTextList.size - 1)
    }
    fun removeItem(position: Any) {
        spokenTextList.removeAt(position as Int)
        notifyItemRemoved(position)
    }


    class SpokenTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewSpokenText: TextView = itemView.findViewById(R.id.text)
        val delete: ImageView = itemView.findViewById(R.id.delete)

        fun bind(spokenText: String) {
            textViewSpokenText.text = spokenText
        }
    }
}