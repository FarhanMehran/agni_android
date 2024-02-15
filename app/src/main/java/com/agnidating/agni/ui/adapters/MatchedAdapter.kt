package com.agnidating.agni.ui.adapters
import androidx.lifecycle.MutableLiveData
import com.agnidating.agni.R
import com.agnidating.agni.base.BasePagingAdapter
import com.agnidating.agni.databinding.ItemMatchedBinding
import com.agnidating.agni.model.home.User
class MatchedAdapter(val onSelect:(Int,User)->Unit) : BasePagingAdapter<User, ItemMatchedBinding>(R.layout.item_matched, HomeCardsAdapter.UserComparator) {
    val removeEmpty= MutableLiveData<Boolean>()
    override fun onBindViewHolder(holder: MyViewHolder<ItemMatchedBinding>, position: Int) {
        holder.binding.item=getItem(position)
        removeEmpty.postValue(true)
        holder.binding.civProfile.setOnClickListener {
            onSelect.invoke(PROFILE,getItem(position)!!)
        }
        holder.binding.root.setOnClickListener {
            onSelect.invoke(CHAT,getItem(position)!!)
        }
    }
    companion object{
        const val PROFILE=0
        const val CHAT=1
    }
}


