package com.capcorp.ui.settings.profile.otheruserProfile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.webservice.models.ReviewDetails
import kotlinx.android.synthetic.main.item_reviews.view.*

class AllReviewAdapter(
    private val mContext: Context,
    private val reviewList: ArrayList<ReviewDetails>
) : RecyclerView.Adapter<AllReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllReviewAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_reviews, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AllReviewAdapter.ViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(orderList: ReviewDetails) = with(itemView) {
            tvUserName.text = reviewList.get(adapterPosition).fullName
            tvNotificationText.text = reviewList.get(adapterPosition).description
            ratingBars.rating = reviewList.get(adapterPosition).totalRating
            Glide.with(mContext).load(reviewList.get(adapterPosition).profilePicURL?.original)
                .apply(RequestOptions().circleCrop())
                .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder))
                .into(ivProfilePic)
        }
    }

}