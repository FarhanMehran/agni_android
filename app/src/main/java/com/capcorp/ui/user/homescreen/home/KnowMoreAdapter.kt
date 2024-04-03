package com.capcorp.ui.user.homescreen.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capcorp.R
import com.capcorp.webservice.models.KnowMoreResponse
import kotlinx.android.synthetic.main.item_know_more.view.*

class KnowMoreAdapter(private val mContext: Context, private val list: List<KnowMoreResponse.Eng>) :
    RecyclerView.Adapter<KnowMoreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KnowMoreAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_know_more, parent, false)
        )
    }

    override fun onBindViewHolder(holder: KnowMoreAdapter.ViewHolder, position: Int) {
        holder.bind(list, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(orderList: List<KnowMoreResponse.Eng>, position: Int) = with(itemView) {
            itemView.tvTextTitle.text = orderList.get(position).link_title
            itemView.tvTextDesc.text = orderList.get(position).link_subtitle
        }
    }


}