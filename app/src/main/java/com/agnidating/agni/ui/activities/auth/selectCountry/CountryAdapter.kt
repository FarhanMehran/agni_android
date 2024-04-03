package com.agnidating.agni.ui.activities.auth.selectCountry

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agnidating.agni.databinding.ItemCountryBinding
import com.agnidating.agni.model.countries.Country

class CountryAdapter(
    private val mContext: Context,
    private val countryList: List<Country>,
    private var filteredList: ArrayList<Country>,
    var lastItem: Country,
    private val dismiss:()->Unit,
) :
    RecyclerView.Adapter<CountryAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCountryBinding.inflate(LayoutInflater.from(mContext), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.item = filteredList[position]
        holder.binding.consFour.isChecked = filteredList[position].selected
        holder.binding.consFour.setOnClickListener {
            lastItem.selected = false
            notifyItemChanged(filteredList.indexOf(lastItem))
            filteredList[position].selected = true
            holder.binding.consFour.isChecked = true
            lastItem = filteredList[position]
            dismiss.invoke()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(keyword: String) {
        filteredList = if (keyword.isEmpty()) {
            ArrayList(countryList)
        } else {
            ArrayList(
                countryList.filter {
                    it.name.lowercase().contains(keyword.lowercase()) || it.phoneCode.contains(keyword)
                }
            )
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    class MyViewHolder(val binding: ItemCountryBinding) : RecyclerView.ViewHolder(binding.root)

}