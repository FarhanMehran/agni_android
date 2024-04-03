package com.capcorp.ui.driver.homescreen.select_trips

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.capcorp.R
import com.capcorp.ui.driver.homescreen.mydeliveries.MyDeliveriesActivity
import com.capcorp.webservice.models.select_trips.Data
import com.capcorp.webservice.models.select_trips.Rewards
import kotlinx.android.synthetic.main.item_all_trips.view.*
import java.util.*
import kotlin.collections.ArrayList


class SelectTripsAdapter(
    private val mContext: Context,
    private val tripsListing: ArrayList<Data>,
    private val upcomingRewards: java.util.ArrayList<Rewards>,
    private val selectTripsFragment: SelectTripsFragment
) : RecyclerView.Adapter<SelectTripsAdapter.ViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_all_trips, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tripsListing[position])

        for(item in upcomingRewards){
            item.city?.let {
                if(it == tripsListing[position].dropDownCountry){
                    holder.itemView.tvUpcomingRewardsValue.text = "$ "+ String.format("%.2f",item.total.toDouble())
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return tripsListing.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        override fun onClick(v: View?) {

        }

        init {
            itemView.setOnClickListener {
                selectedPosition = adapterPosition
                val intent = Intent(mContext, MyDeliveriesActivity::class.java)
                intent.putExtra("pickCountry", tripsListing.get(selectedPosition).pickUpCountry)
                intent.putExtra(
                    "dropDownCountry",
                    tripsListing.get(selectedPosition).dropDownCountry
                )
                mContext.startActivity(intent)
            }
        }

        fun bind(tripsList: Data) = with(itemView) {
            tvSToD.text = tripsList.dropDownCountry?.uppercase(Locale.getDefault())
            tvToDeliverValue.text = tripsList.acceptedCount
            tvDeliveredValue.text = tripsList.completedCount
            tvOfferMadeValue.text = tripsList.requestCount

            callAPITosetImage(tripsList.dropDownCountry, itemView.ivBG)


        }
    }

    private fun callAPITosetImage(dropDownCountry: String?, ivBG: ImageView) {
        dropDownCountry?.let { selectTripsFragment.presenter.getImages(it, ivBG) }
    }
}
