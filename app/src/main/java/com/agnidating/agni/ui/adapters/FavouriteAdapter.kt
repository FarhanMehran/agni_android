package com.agnidating.agni.ui.adapters



import com.agnidating.agni.R
import com.agnidating.agni.base.BaseAdapter
import com.agnidating.agni.databinding.ItemFavouriteBinding
import com.agnidating.agni.model.home.User
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.visible


class FavouriteAdapter(val data: List<User>, private val onClick:(User,Int)->Unit) :
    BaseAdapter<ItemFavouriteBinding>(R.layout.item_favourite) {

    override fun onBindViewHolder(holder: MyViewHolder<ItemFavouriteBinding>, position: Int) {
        val item=data[position]
        holder.binding.item=data[position]
        if (item.matchStatus==0){
            holder.binding.etSendFlower.text=requireContext().getString(R.string.send_flower)
            holder.binding.ivFlower.visible()
        }else{
            holder.binding.etSendFlower.text=requireContext().getString(R.string.send_message)
            holder.binding.ivFlower.gone()
        }
        holder.binding.root.setOnClickListener {
            onClick.invoke(data[position], USER_PROFILE)
        }
        holder.binding.rlSendFlower.setOnClickListener {
            onClick.invoke(data[position], SEND_FLOWER)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    companion object{
        const val SEND_FLOWER=0
        const val USER_PROFILE=1
    }

}


