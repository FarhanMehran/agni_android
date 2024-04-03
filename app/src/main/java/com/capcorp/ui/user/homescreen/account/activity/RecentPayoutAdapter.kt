package com.capcorp.ui.user.homescreen.account.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capcorp.R
import com.capcorp.ui.user.homescreen.account.AccountInterface
class RecentPayoutAdapter(
    private val mContext: Context
) :
    RecyclerView.Adapter<RecentPayoutAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentPayoutAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_recentpayouts, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecentPayoutAdapter.ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() = with(itemView) {

        }
    }

}