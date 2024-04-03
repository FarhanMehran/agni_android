package com.agnidating.agni.ui.fragment.editProfile

import com.agnidating.agni.R
import com.agnidating.agni.base.BaseAdapter
import com.agnidating.agni.databinding.ItemAddImageBinding
import com.agnidating.agni.model.EditImageModel
import com.agnidating.agni.utils.*

class EditImagesAdapter(val images: ArrayList<EditImageModel>, val imageEvents: (Int, Int)->Unit) :BaseAdapter<ItemAddImageBinding>(R.layout.item_add_image) {


    override fun onBindViewHolder(holder: MyViewHolder<ItemAddImageBinding>, position: Int) {
        // if recyclerview index is smaller than array size show image
        if (position<images.size){
            holder.binding.ivRound.visible()
            holder.binding.ivDelete.visible()
            holder.binding.ivAddImage.gone()
            //if image is selectedImage
            if (images[position].image==null){
                holder.binding.ivRound.load(images[position].imageFile!!,null)
            }else{
                // if image is url image
                val item=images[position]
                holder.binding.ivRound.load(item.image?.profile.toString(),CommonKeys.IMAGE_BASE_URL)
            }
        }
        else if (position==images.size){
            //next index after all images
            holder.binding.ivRound.gone()
            holder.binding.ivDelete.gone()
            holder.binding.ivAddImage.visible()
        }
        else {
            // all other index
            holder.binding.ivRound.gone()
            holder.binding.ivDelete.gone()
            holder.binding.ivAddImage.gone()
        }
        holder.binding.ivAddImage.setOnClickListener {
            imageEvents(position, EVENT_ADD_IMAGE)
        }
        holder.binding.ivDelete.setOnClickListener {
            if (images[position].image==null){
                images.removeAt(position)
                notifyItemRemoved(position)
            }else{
                if (images.size==1){
                    "You must add least one image".toast(requireContext())
                }else{
                    imageEvents(position, EVENT_DELETE_IMAGE)
                }
            }
        }
    }

    override fun getItemCount(): Int {
       return 7
    }

    companion object {
        const val EVENT_ADD_IMAGE=0
        const val EVENT_DELETE_IMAGE=1
    }


}