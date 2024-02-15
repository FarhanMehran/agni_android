package com.agnidating.agni.ui.adapters


import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.agnidating.agni.R
import com.agnidating.agni.base.BasePagingAdapter
import com.agnidating.agni.databinding.ItemMatchesBinding
import com.agnidating.agni.model.home.User
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.logs
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.agnidating.agni.utils.visible


class MatchesAdapter constructor(
    val sharedPrefs: SharedPrefs,
    private val onAcceptReject: (Int, User) -> Unit
) :
    BasePagingAdapter<User, ItemMatchesBinding>(
        R.layout.item_matches,
        HomeCardsAdapter.UserComparator
    ) {
    override fun onBindViewHolder(holder: MyViewHolder<ItemMatchesBinding>, position: Int) {
        val item = getItem(position)!!
        holder.binding.item = getItem(position)
        if (sharedPrefs.isSubscribed().not()) {
            if (item.show == 1) {
                holder.binding.blurry.gone()
                holder.binding.ivCheck.visible()
                holder.binding.ivCross.visible()
                holder.binding.llFlower.isVisible=item.type=="f"
                if (item.type=="f"){
                  if (item.flowers?.toInt()!!>=5){
                      holder.binding.ivFlower.setImageResource(R.drawable.flower)
                      holder.binding.tvTotalFlowers.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange_pink))
                  }else{
                      holder.binding.ivFlower.setImageResource(R.drawable.ic_rose)
                      holder.binding.tvTotalFlowers.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                  }
                    holder.binding.tvTotalFlowers.text=item.flowers
                }
                setListeners(holder)
            } else {
                holder.binding.blurry.visible()
                holder.binding.ivCheck.gone()
                holder.binding.ivCross.gone()
                holder.binding.llFlower.gone()
                holder.binding.root.setOnClickListener {
                    onAcceptReject.invoke(3, holder.binding.item!!)
                }
            }
        } else {
            holder.binding.blurry.gone()
            holder.binding.ivCheck.visible()
            holder.binding.ivCross.visible()
            holder.binding.llFlower.gone()
            setListeners(holder)
        }
    }

    private fun setListeners(holder: MyViewHolder<ItemMatchesBinding>) {
        holder.binding.root.setOnClickListener {
            onAcceptReject.invoke(2, holder.binding.item!!)
        }
        holder.binding.tvAccept.setOnClickListener {
            onAcceptReject.invoke(1, holder.binding.item!!)
        }
        holder.binding.tvReject.setOnClickListener {
            onAcceptReject.invoke(0, holder.binding.item!!)
        }
    }
}


