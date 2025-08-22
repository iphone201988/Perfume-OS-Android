package com.tech.perfumos.ui.camera_perfume

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tech.perfumos.R
import com.tech.perfumos.databinding.SinglePerfumeItemViewBinding
import com.tech.perfumos.ui.camera_perfume.model.ReviewModel
import com.tech.perfumos.ui.camera_perfume.model.SimilarPerfume

class SimilarPerfumeAdapter(
    val context: Context,
    var list: ArrayList<SimilarPerfume?>?,
    val clickListener: ClickListener
) : RecyclerView.Adapter<SimilarPerfumeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SinglePerfumeItemViewBinding.inflate(
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

            Glide.with(context)
                .load(model?.image)
                .placeholder(R.drawable.perfume_botton_dummy) // Placeholder while loading
                .into(ivPerfume)

            clMain.setOnClickListener {
                clickListener.onClick(position, model!!)
            }
        }
    }

    class ViewHolder(itemView: SinglePerfumeItemViewBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    interface ClickListener {
        fun onClick(position: Int, model: SimilarPerfume)
    }


    fun addNewItem(newItem: ArrayList<SimilarPerfume?>?) {
        if (newItem != null) {
            list?.addAll(newItem)
            notifyDataSetChanged()
        }

    }

}