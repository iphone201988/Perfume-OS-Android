package com.tech.perfumos.ui.camera_perfume

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tech.perfumos.databinding.NotesItemViewBinding
import com.tech.perfumos.ui.camera_perfume.model.NotesList

class NotesAdapter(
    val context: Context,
    var list: ArrayList<NotesList?>?
) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NotesItemViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list?.get(position)
        holder.binding.apply {
        }


    }

    class ViewHolder(itemView: NotesItemViewBinding) : RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

}