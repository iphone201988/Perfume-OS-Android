package com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.databinding.ProfileCollectionEmptyItemViewBinding
import com.tech.perfumos.databinding.ProfileCollectionItemViewBinding
import com.tech.perfumos.ui.camera_perfume.PerfumeInfoActivity
import com.tech.perfumos.ui.dashboad.fragment_folder.compare.ComparePerfumeAdapter

class ProfileCollectionAdapter(val context: Context, var list: ArrayList<UserPerfumeList?>?, val clickListener: ItemClickListener) :
    Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_EMPTY = 1
    }
    override fun getItemViewType(position: Int): Int {
        // If data at this position is null or some flag, return TYPE_EMPTY
        // For example: if list is empty or this item is a placeholder
        // Or you can choose empty item based on data or position
        //return if (position == 0) TYPE_EMPTY else TYPE_ITEM
        return TYPE_ITEM
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //return ViewHolder(CompareItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
        Log.d("viewType", "onCreateViewHolder: $viewType")
        return if (viewType == TYPE_ITEM) {
            val binding =
                ProfileCollectionItemViewBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            ViewHolder(binding)
        } else {
            val binding =
                ProfileCollectionEmptyItemViewBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            EmptyViewHolder(binding)
        }
    }
    override fun getItemCount(): Int {
        return list!!.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ViewHolder) {
                val model = list?.get(position)
                holder.binding.apply {
                    if (model != null) {
                        val url = model.perfumeId?.image.toString()
                        if (model.perfumeId?.image != null) {


                            val imageUrl = if (url.contains("http")) {
                                url
                            } else {
                                "${Constants.BASE_URL_IMAGE}$url"
                            }
                            Glide.with(context).load(imageUrl).into(imagePerfume)
                        }
                    }
                    llMain.setOnClickListener {

                        /*val intent = Intent(context, PerfumeInfoActivity::class.java)
                        intent.putExtra("perfumeId", model?.perfumeId?.id.toString())
                        context.startActivity(intent)*/

                        clickListener.onItemClickListener(model?.perfumeId?.id.toString())

                    }
                }
            } else if (holder is EmptyViewHolder) {
                holder.emptyBinding.apply {

                    imagePerfume.setOnClickListener {
                        clickListener.onEmptyItemClickListener(position)
                    }
                    ivAdd.setOnClickListener {
                        clickListener.onEmptyItemClickListener(position)
                    }
                }
        }
    }

    class ViewHolder(itemView: ProfileCollectionItemViewBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding = itemView

    }

    class EmptyViewHolder(itemView: ProfileCollectionEmptyItemViewBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var emptyBinding = itemView

    }

    interface ItemClickListener {
        fun onItemClickListener(perfumeId: String)
        fun onEmptyItemClickListener(position: Int)

    }
}
