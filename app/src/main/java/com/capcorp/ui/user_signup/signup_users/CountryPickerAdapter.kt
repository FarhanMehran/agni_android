package com.capcorp.ui.user_signup.signup_users

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capcorp.R
import kotlinx.android.synthetic.main.item_country_names.view.*

class CountryPickerAdapter(
    val items: List<String>,
    val context: Context,
    private val selectedCountryName: SelectedCountryName
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCountryNames?.text = items.get(position)
        holder.itemView.setOnClickListener {
            selectedCountryName.selectedCountryName(items.get(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_country_names, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvCountryNames = view.tv_country_names
}