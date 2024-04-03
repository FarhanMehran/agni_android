package com.agnidating.agni.ui.adapters

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.agnidating.agni.R
import com.agnidating.agni.base.BaseAdapter
import com.agnidating.agni.databinding.ItemReligionCommunityBinding
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.visible
import javax.inject.Inject


class ReligionCommunityAdapter @Inject constructor(val list: ArrayList<String>,var selectedIndex:Int=-1) :
    BaseAdapter<ItemReligionCommunityBinding>(R.layout.item_religion_community) {


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder<ItemReligionCommunityBinding>, position: Int) {
        val item=list[position]
        holder.binding.tvTitle.text=item
        if (position==selectedIndex){
            holder.binding.tvTitle.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange_pink))
            holder.binding.ivSelection.visible()
        }else{
            holder.binding.tvTitle.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            holder.binding.ivSelection.gone()
        }
        if(position==list.lastIndex){
            holder.binding.dividerView.gone()
        }
        else{
            holder.binding.dividerView.visible()
        }
        holder.binding.root.setOnClickListener {
            selectedIndex=position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}


