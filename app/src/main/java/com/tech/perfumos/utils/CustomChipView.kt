package com.tech.perfumos.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.tech.perfumos.databinding.CompareNotesItemViewBinding
import com.tech.perfumos.databinding.NotesItemViewBinding

class CustomChipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = CompareNotesItemViewBinding.inflate(LayoutInflater.from(context), this, true)


    fun setImage(imageUrl: String) {
        // Use Coil/Glide/Picasso to load image asynchronously for example:
        // Coil example:
        // binding.ivNote.load(imageUrl)
        Glide.with(context).load(imageUrl).into(binding.ivNote)


    }

    fun setName(name: String) {
        binding.tvNote.text = name
    }
}