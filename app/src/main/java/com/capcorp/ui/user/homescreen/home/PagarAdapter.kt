package com.capcorp.ui.user.homescreen.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.user.homescreen.home.create_order_from_web.CreateOrderFromWeb
import kotlinx.android.synthetic.main.item_pager.view.*

class PagarAdapter(
    private val mContext: Context,
    private val list: ArrayList<com.capcorp.webservice.models.home.Store>
) : RecyclerView.Adapter<PagarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagarAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_pager, parent, false))
    }

    override fun onBindViewHolder(holder: PagarAdapter.ViewHolder, position: Int) {
        holder.bind(list, position)
    }

    override fun getItemCount(): Int {
        list.let {
            return it.size
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(orderList: ArrayList<com.capcorp.webservice.models.home.Store>, position: Int) =
            with(itemView) {
                tvTitle.text = orderList.get(position).storeTitle ?: ""
                Glide.with(mContext).load(orderList.get(position).storeImage)
                    .apply(RequestOptions().placeholder(R.drawable.ic_package)).into(ivImage)

                itemView.setOnClickListener {
                    context.startActivity(
                        Intent(
                            context,
                            CreateOrderFromWeb::class.java
                        ).putExtra("url", orderList.get(position).storeLink)
                    )
                }
            }
    }


}