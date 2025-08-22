package com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.databinding.FavoriteItemViewBinding

class FavoriteAdapter(val context: Context, var list: ArrayList<FavoriteList?>?, val clickListener: ItemClickListener): RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FavoriteItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model  = list?.get(position)
        holder.binding.apply {

            when(model?.type){
                "perfume"->{
                    val url  = model.perfumeId?.image
                    try {
                        if(url != null) {
                            val imageUrl = if (url.contains("http")) {
                                url
                            } else {
                                "${Constants.BASE_URL_IMAGE}$url"
                            }
                            Glide.with(view.context).load(imageUrl).into(searchImg)
                        }
                        else{
                            Glide.with(view.context).load(R.drawable.earn_badge_img) .into(searchImg)
                        }
                    }catch (ex:Exception){
                        ex.printStackTrace()
                    }
                    tvName.text = model.perfumeId?.name
                }
                "note"->{
                    val url  = model.noteId?.bgUrl
                    try {
                        if(url != null) {
                            val imageUrl = if (url.contains("http")) {
                                url
                            } else {
                                "${Constants.BASE_URL_IMAGE}$url"
                            }
                            Glide.with(view.context).load(imageUrl).into(searchImg)
                        }
                        else{
                            Glide.with(view.context).load(R.drawable.earn_badge_img) .into(searchImg)
                        }
                    }catch (ex:Exception){
                        ex.printStackTrace()
                    }
                    tvName.text = model.noteId?.name
                }
                "perfumer"->{
                    val url  = model.perfumerId?.smallImage
                    try {
                        if(url != null) {
                            val imageUrl = if (url.contains("http")) {
                                url
                            } else {
                                "${Constants.BASE_URL_IMAGE}$url"
                            }
                            Glide.with(view.context).load(imageUrl).into(searchImg)
                        }
                        else{
                            Glide.with(view.context).load(R.drawable.earn_badge_img) .into(searchImg)
                        }
                    }catch (ex:Exception){
                        ex.printStackTrace()
                    }
                    tvName.text = model.perfumerId?.name
                }
                "article"->{
                    val url  = model.articleId?.image
                    try {
                        if(url != null) {
                            val imageUrl = if (url.contains("http")) {
                                url
                            } else {
                                "${Constants.BASE_URL_IMAGE}$url"
                            }
                            Glide.with(view.context).load(imageUrl).into(searchImg)
                        }
                        else{
                            Glide.with(view.context).load(R.drawable.earn_badge_img) .into(searchImg)
                        }
                    }catch (ex:Exception){
                        ex.printStackTrace()
                    }
                    tvName.text = model.articleId?.title
                }
            }
            clMain.setOnClickListener {
                clickListener.onItemClickListener(model!!)
            }
        }
    }

    class ViewHolder(itemView: FavoriteItemViewBinding) : RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }


    interface ItemClickListener {
        fun onItemClickListener(model: FavoriteList)

    }
}