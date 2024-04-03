package com.capcorp.ui.driver.homescreen.detail_offer_accepeted.home_delivery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.webservice.models.driver.driver_request_pic.ReceiptImages

class ReceiptImageAdapter(
    private val context: Context,
    private val adapterListener: ProfileImagesAdapterListener
) : PagerAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val mImages: ArrayList<ReceiptImages> = ArrayList()

    override fun getCount(): Int {
        return mImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.item_request_image, container, false)
        val imageView = itemView.findViewById<View>(R.id.ivUserImage) as ImageView

        Glide.with(context).load(mImages[position].original)
            .apply(RequestOptions().placeholder(R.drawable.ic_package)).into(imageView)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun addProfileImages(profileImages: List<ReceiptImages>) {
        mImages.addAll(profileImages)
        notifyDataSetChanged()
    }

    interface ProfileImagesAdapterListener {
        fun onImageClicked(imagePath: String?)
    }
}