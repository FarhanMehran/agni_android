package com.agnidating.agni.ui.adapters

import com.agnidating.agni.R
import com.agnidating.agni.base.BaseAdapter
import com.agnidating.agni.databinding.ItemAddReportImageBinding
import com.agnidating.agni.utils.*
import java.io.File

class AddReportImages(val images: ArrayList<File>, val imageEvents: ()->Unit) :BaseAdapter<ItemAddReportImageBinding>(R.layout.item_add_report_image) {


    override fun onBindViewHolder(holder: MyViewHolder<ItemAddReportImageBinding>, position: Int) {
        if (position<images.size){
            holder.binding.ivRound.visible()
            holder.binding.ivDelete.visible()
            holder.binding.ivAddImage.gone()
            holder.binding.ivRound.load(images[position],null)
        }
        else {
            holder.binding.ivRound.gone()
            holder.binding.ivDelete.gone()
            holder.binding.ivAddImage.visible()
        }
        holder.binding.ivAddImage.setOnClickListener {
            imageEvents()
        }
        holder.binding.ivDelete.setOnClickListener {
            images.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
       return if (images.size<3) images.size+1 else images.size
    }



}