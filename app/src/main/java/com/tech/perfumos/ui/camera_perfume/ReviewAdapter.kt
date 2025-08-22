package com.tech.perfumos.ui.camera_perfume

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.databinding.ReviewItemViewBinding
import com.tech.perfumos.ui.camera_perfume.model.ReviewModel
import com.tech.perfumos.utils.Utils.generateInitialsBitmap
import com.tech.perfumos.utils.Utils.makeExpandable

class ReviewAdapter(
    val context: Context,
    var list: ArrayList<ReviewModel?>?
) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ReviewItemViewBinding.inflate(
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
        Log.d("model", "onBindViewHolder:${model?.authorImage.toString()} ")
        try {

            holder.binding.apply {
                nameUser.text = model?.authorName.toString()
                reviewDis.text = model?.review.toString()

                /*reviewDis.makeExpandable(
                    model?.review.toString(),
                    model?.isExpand ?: false
                ) { expanded ->
                    model?.isExpand = expanded
                    notifyItemChanged(position)
                }*/
                //reviewDis.text = model?.review.toString()
                ratingBar.rating = (model?.rating ?: 0.0).toFloat()

                if (model?.authorImage.isNullOrEmpty()) {
                    val initialsBitmap =
                        generateInitialsBitmap(model?.authorName.toString())
                    ivAuthor.setImageBitmap(initialsBitmap)
                } else {
                    val image = if (model?.authorImage.toString().contains("http")) {
                        model?.authorImage.toString()
                    } else {
                        "${Constants.BASE_URL_IMAGE}${model?.authorImage.toString()}"
                    }
                    Glide.with(context)
                        .load(image)
                        //.placeholder(R.drawable.article_dummy_image) // Placeholder while loading
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                glideModel: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.d("loadImage", "onLoadFailed: loading failed")
                                val initialsBitmap =
                                    generateInitialsBitmap(model?.authorName.toString())
                                ivAuthor.setImageBitmap(initialsBitmap)
                                return true
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                // Handle success if needed
                                return false
                            }
                        })
                        .into(ivAuthor)
                }
                if(model?.title.isNullOrEmpty()){
                    tvTitle.visibility = View.GONE
                }else{
                    tvTitle.visibility = View.VISIBLE
                    tvTitle.text = model?.title.toString()
                }
                /* val initialsBitmap =
                     generateInitialsBitmap(model?.authorName.toString())
                 ivAuthor.setImageBitmap(initialsBitmap)*/
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun addNewItem(newItem: ReviewModel) {
        if (!list.isNullOrEmpty()) {
            list?.add(0, newItem)
            notifyItemInserted(0)
            // this.scrollToPosition(0) // optional
        }else{
            list?.add(newItem)
            notifyDataSetChanged()
        }

    }

    class ViewHolder(itemView: ReviewItemViewBinding) : RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

}