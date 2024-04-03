package com.agnidating.agni.ui.activities.addImages

import com.agnidating.agni.R
import com.agnidating.agni.base.BaseAdapter
import com.agnidating.agni.databinding.ItemAddImageBinding
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.load
import com.agnidating.agni.utils.visible
import java.io.File

class AddImagesAdapter(val images: ArrayList<File>,val addImages: (Int)->Unit,val removeImages: (Int)->Unit) :BaseAdapter<ItemAddImageBinding>(R.layout.item_add_image) {
    override fun onBindViewHolder(holder: MyViewHolder<ItemAddImageBinding>, position: Int) {
        if (position<images.size){
            holder.binding.ivRound.visible()
            holder.binding.ivAddImage.gone()
            holder.binding.ivDelete.visible()
            holder.binding.ivRound.load(images[position],null)
        }
        else if (position==images.size){
            holder.binding.ivRound.gone()
            holder.binding.ivAddImage.visible()
            holder.binding.ivDelete.gone()
        }
        else {
            holder.binding.ivRound.gone()
            holder.binding.ivAddImage.gone()
        }
        holder.binding.ivAddImage.setOnClickListener {
            addImages(position)
        }

        holder.binding.ivDelete.setOnClickListener {
            removeImages(position)
        }
    }

    override fun getItemCount(): Int {
       return 7
    }


}