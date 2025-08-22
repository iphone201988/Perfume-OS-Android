package com.tech.perfumos.ui.dashboad.fragment_folder.compare

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.tech.perfumos.R
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.databinding.CompareEmptyItemViewBinding
import com.tech.perfumos.databinding.CompareItemViewBinding
import com.tech.perfumos.ui.camera_perfume.model.NotesList
import com.tech.perfumos.ui.camera_perfume.model.PerfumeInfoModel
import com.tech.perfumos.ui.dashboad.fragment_folder.compare.CompareFragment.Companion.compareIndex
import com.tech.perfumos.utils.CustomChipView
import com.tech.perfumos.utils.Utils.preventMultipleClick

class ComparePerfumeAdapter(
    val context: Context,
    val list: ArrayList<PerfumeInfoModel.PerfumeInfoData?>?,
    val clickListener: ItemClickListener
) : Adapter<RecyclerView.ViewHolder>() {


    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_EMPTY = 1
    }

    override fun getItemViewType(position: Int): Int {
        // If data at this position is null or some flag, return TYPE_EMPTY
        // For example: if list is empty or this item is a placeholder
        // Or you can choose empty item based on data or position
        return if (list?.get(position) == null) TYPE_EMPTY else TYPE_ITEM

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //return ViewHolder(CompareItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
        Log.d("viewType", "onCreateViewHolder: $viewType")
        return if (viewType == TYPE_ITEM) {
            val binding =
                CompareItemViewBinding.inflate(LayoutInflater.from(context), parent, false)
            ViewHolder(binding)
        } else {
            val binding =
                CompareEmptyItemViewBinding.inflate(LayoutInflater.from(context), parent, false)
            EmptyViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list?.get(position)
        val drawable = GradientDrawable()

        // Shape type
        drawable.shape = GradientDrawable.RECTANGLE

        // Corner radius
        val cornerRadius = context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp)
        drawable.cornerRadius = cornerRadius

        // Solid color
        drawable.setColor(Color.parseColor("#B2FFFFFF")) // ARGB color

        // Stroke
        val strokeWidthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 0.5f, context.resources.displayMetrics
        ).toInt()

        drawable.setStroke(strokeWidthPx, ContextCompat.getColor(context, R.color.white))

        /*val seasonsList = ArrayList<String>()
        val notesList = ArrayList<String>()*/


        if (holder is ViewHolder) {
            holder.binding.apply {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.bindWithBlur()
                } else {
                    holder.binding.blurView.setBlurEnabled(false)
                }*/
                if (position == compareIndex) {
                    root.background =
                        ContextCompat.getDrawable(context, R.drawable.bg_selctor_border_gradient)
                }
                Log.d("position", "onBindViewHolder: ${position}")
                if (model != null) {
                    textTitle.text = model.name
                    textBrand.text = model.brand

                    Glide.with(context).load(model.image).into(imagePerfume)
                }

                if(model?.isFavorite == true){
                    btnFavorite.setImageResource(R.drawable.icon_favorite)
                }else{
                    btnFavorite.setImageResource(R.drawable.unfavorite_icon)
                }

                btnFavorite.setOnClickListener {
                    clickListener.onItemFavorite(model?.id.toString(), position)
                }

                if (model?.seasons.isNullOrEmpty()) {
                    seasonsGroup.visibility=View.GONE
                    noDataSeasons.visibility = View.VISIBLE

                } else {
                    noDataSeasons.visibility = View.GONE
                    seasonsGroup.visibility=View.VISIBLE
                    model?.seasons?.forEach {
                        val chip = Chip(seasonsGroup.context)
                        chip.text = it?.name
                        chip.backgroundDrawable =
                            ContextCompat.getDrawable(context, R.drawable.bg_rounded_tv_white)
                        chip.chipBackgroundColor =
                            ContextCompat.getColorStateList(context, R.color.bgRec3Fill)
                        chip.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_username_home
                            )
                        )
                        chip.textSize = 15f
                        chip.chipCornerRadius = 22f
                        chip.isCheckable = false // If you want selectable chips
                        seasonsGroup.addView(chip)
                    }
                }

                if (model?.occasions.isNullOrEmpty()) {
                    occasionGroup.visibility=View.GONE
                    noDataOccasion.visibility = View.VISIBLE
                } else {
                    noDataOccasion.visibility = View.GONE
                    occasionGroup.visibility=View.VISIBLE
                    model?.occasions?.forEach {
                        val chip = Chip(occasionGroup.context)
                        chip.text = it?.name
                        chip.backgroundDrawable =
                            ContextCompat.getDrawable(context, R.drawable.bg_rounded_tv_white)
                        chip.chipBackgroundColor =
                            ContextCompat.getColorStateList(context, R.color.bgRec3Fill)
                        chip.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_username_home
                            )
                        )
                        chip.textSize = 15f
                        chip.chipCornerRadius = 20f
                        chip.isCheckable = false // If you want selectable chips
                        occasionGroup.addView(chip)
                    }
                }



                if (model?.notes?.notes.isNullOrEmpty()) {
                    llNotesTop.visibility = View.VISIBLE

                    if (model?.notes?.top.isNullOrEmpty()) {
                        llNotesTop.visibility = View.GONE

                        view5.visibility = View.GONE
                    } else {
                        llNotesTop.visibility = View.VISIBLE
                        view5.visibility = View.VISIBLE

                        loadNote(model?.notes?.top, notesTopGroup)

                    }

                    if (model?.notes?.middle.isNullOrEmpty()) {

                        llNotesMid.visibility = View.GONE
                        view6.visibility = View.GONE
                    } else {
                        llNotesMid.visibility = View.VISIBLE
                        view6.visibility = View.VISIBLE

                        loadNote(model?.notes?.middle, notesMidGroup)
                    }

                    if (model?.notes?.base.isNullOrEmpty()) {
                        llNotesBase.visibility = View.GONE
                        //view7.visibility = View.GONE

                    } else {
                        llNotesBase.visibility = View.VISIBLE
                        //view7.visibility = View.VISIBLE

                        loadNote(model?.notes?.base, notesBaseGroup)
                    }

                    if (model?.notes?.top.isNullOrEmpty() && model?.notes?.middle.isNullOrEmpty() && model?.notes?.base.isNullOrEmpty() && model?.notes?.notes.isNullOrEmpty()) {
                        noDataNotes.visibility = View.VISIBLE
                        view2.visibility = View.VISIBLE
                    } else {
                        noDataNotes.visibility = View.GONE
                        view2.visibility = View.GONE

                    }


                } else {
                    llNotesTop.visibility = View.VISIBLE
                    view5.visibility = View.VISIBLE

                    ivNoteTop.setImageResource(R.drawable.iv_notes)
                    tvNoteTop.text = "Notes"
//                    view.visibility = View.GONE

                    llNotesMid.visibility = View.GONE
                    llNotesBase.visibility = View.GONE
                    view6.visibility = View.GONE
                    //view7.visibility = View.GONE

                    //notesTopGroup.removeAllViews()
                    loadNote(model?.notes?.notes, notesTopGroup)

                    /*model?.notes?.notes?.forEach {

                        val chipView = CustomChipView(context)
                        val url = it?.image
                        if (url != null) {
                            val imageUrl = if (url.contains("http")) {
                                url
                            } else {
                                "${Constants.BASE_URL_IMAGE}$url"
                            }
                            chipView.setImage(imageUrl)

                        } else {

                        }
                        chipView.setName(it?.name.toString())

                        // Optional: set margin programmatically between chips
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            // rightMargin = resources.getDimensionPixelSize(R.dimen._8sdp)
                        }
                        //chipView.layoutParams = params

                        notesTopGroup.addView(chipView)
                    }*/

                }


                val topNotesList = model?.notes?.top
                val middleNotesList = model?.notes?.middle
                val baseNotesList = model?.notes?.base
                val allNotesList = model?.notes?.notes


                /*val favoriteIcon = if (item.isFavorite) {
                    R.drawable.icon_favorite
                } else {
                    R.drawable.unfavorite_icon
                }
                btnFavorite.setImageResource(favoriteIcon)
                btnFavorite.setOnClickListener {
                    item.isFavorite = !item.isFavorite

                    // Notify item changed so RecyclerView refreshes that item
                    notifyItemChanged(position)
                }*/

                btnSwap.setOnClickListener {
                    clickListener.onEmptyItemClickListener(position)
                }
                textLearnMore.setOnClickListener {
                    clickListener.onItemClickListener(model?.id.toString(), position)
                }

            }
        } else if (holder is EmptyViewHolder) {
            holder.emptyBinding.apply {
                /*if (position == 1) {
                    root.background =
                        ContextCompat.getDrawable(context, R.drawable.bg_selctor_border_gradient)
                }*/

                imagePerfume.setOnClickListener {
                    imagePerfume.preventMultipleClick()
                    clickListener.onEmptyItemClickListener(position)
                }
                ivAdd.setOnClickListener {

                    ivAdd.preventMultipleClick()
                    clickListener.onEmptyItemClickListener(position)
                }
            }
        }
    }

    fun loadNote(data: ArrayList<NotesList?>?, group: LinearLayout) {
        group.removeAllViews()
        val dataList = data?.take(3)
        dataList?.forEach {

            val chipView = CustomChipView(context)
            val url = it?.image
            if (url != null) {
                val imageUrl = if (url.contains("http")) {
                    url
                } else {
                    "${Constants.BASE_URL_IMAGE}$url"
                }
                chipView.setImage(imageUrl)

            } else {

            }
            chipView.setName(it?.name.toString())

            // Optional: set margin programmatically between chips
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                // rightMargin = resources.getDimensionPixelSize(R.dimen._8sdp)
            }
            //chipView.layoutParams = params

            group.addView(chipView)
        }
    }

    class ViewHolder(itemView: CompareItemViewBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding = itemView

    }

    class EmptyViewHolder(itemView: CompareEmptyItemViewBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var emptyBinding = itemView

    }

    interface ItemClickListener {
        fun onItemClickListener(perfumeId: String, position: Int)
        fun onItemFavorite(perfumeId: String, position: Int)
        fun onEmptyItemClickListener(position: Int)

    }


}