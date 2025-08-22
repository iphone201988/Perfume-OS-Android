package com.tech.perfumos.ui.onboarding_folder

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.RecyclerListener
import com.tech.perfumos.R
import com.tech.perfumos.databinding.GenderItemViewBinding
import java.util.Locale


class ChooseGenderAdapter(val context: Context, val list : ArrayList<GenderList>, val listener : GenderSelection) : Adapter<ChooseGenderAdapter.ViewHolder>() {
    var selectedPosition = RecyclerView.NO_POSITION
    class ViewHolder(itemView: GenderItemViewBinding) : RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(GenderItemViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model  = list[position]
        holder.binding.apply {
            if(position < 2){
                ivIcon.setImageResource(model.drawable)
                ivIcon.visibility = View.VISIBLE
            }else{
                ivIcon.visibility = View.GONE
            }

            textHeading.text = model.text
            when(position){
                0 -> {
                    textHeading.setTextColor(Color.parseColor("#006AFA"))
                }
                1 -> {
                    textHeading.setTextColor(Color.parseColor("#E024FF"))
                }
                else-> {
                    textHeading.setTextColor(Color.parseColor("#2C2D3D"))
                }
            }

            if (selectedPosition == position) {
                val backgroundRes = when (position) {
                    0 -> ContextCompat.getDrawable(context, R.drawable.bg_selected_45)
                    1 -> ContextCompat.getDrawable(context, R.drawable.bg_selected_45)
                    else -> ContextCompat.getDrawable(context, R.drawable.bg_selected_45)
                }
                val textColor = when (position) {
                    0 -> R.color.male_selection
                    1 -> R.color.female_selection
                    else -> R.color.white
                }
                val bgColor = when (position) {
                    0 -> R.color.male_selection
                    1 -> R.color.female_selection
                    else -> R.color.trophy_color1
                }

                clMain.background = backgroundRes
                clMain.backgroundTintList = ContextCompat.getColorStateList(context,bgColor)
                textHeading.setTextColor( ContextCompat.getColor(context, R.color.white))
                ivIcon.imageTintList = ContextCompat.getColorStateList(context, R.color.white)
            } else {
                clMain.background = ContextCompat.getDrawable(context, R.drawable.artical_bg_45)
                clMain.backgroundTintList = null
                val backgroundRes = when (position) {
                    0 -> ContextCompat.getDrawable(context, R.drawable.bg_selected_45)
                    1 -> ContextCompat.getDrawable(context, R.drawable.bg_selected_45)
                    else -> ContextCompat.getDrawable(context, R.drawable.bg_selected_45)
                }
                val textColor = when (position) {
                    0 -> R.color.male_selection
                    1 -> R.color.female_selection
                    else -> R.color.white
                }

                textHeading.setTextColor(textColor)
                ivIcon.imageTintList = ContextCompat.getColorStateList(context, textColor)

            }


            clMain.setOnClickListener {

                val previousPosition = selectedPosition
                selectedPosition = position

                listener.onSelect(model.text.lowercase(Locale.ROOT))
                // Refresh only the previous and current selection
                notifyItemChanged(previousPosition)
                notifyItemChanged(position)

            }
        }
    }

    interface GenderSelection {
        fun onSelect(text: String)
    }
}