package com.tech.perfumos.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tech.perfumos.R

class CustomSpinnerAdapter(
    context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item_view, parent, false)
        val textView = view.findViewById<TextView>(R.id.spinnerText)
        textView.text = items[position]
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item_view, parent, false)
        val textView = view.findViewById<TextView>(R.id.spinnerText)
        textView.text = items[position]
        return view
    }

}