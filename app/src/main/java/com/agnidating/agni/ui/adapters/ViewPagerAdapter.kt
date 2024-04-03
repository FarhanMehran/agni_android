package com.agnidating.agni.ui.adapters

import com.agnidating.agni.R
import com.agnidating.agni.base.BaseAdapter
import com.agnidating.agni.databinding.ItemViewpagerBinding
import com.agnidating.agni.model.profile.Images
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.load

/**
 * Create by AJAY ASIJA on 04/21/2022
 */
class ViewPagerAdapter(val list: List<Images>) :BaseAdapter<ItemViewpagerBinding>(R.layout.item_viewpager) {


    override fun onBindViewHolder(holder: MyViewHolder<ItemViewpagerBinding>, position: Int) {
        holder.binding.ivVp.load(list[position].profile,CommonKeys.IMAGE_BASE_URL)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}