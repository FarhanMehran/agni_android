package com.capcorp.ui.user.homescreen.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.capcorp.R
import com.capcorp.webservice.models.home.StoreCard
import kotlinx.android.synthetic.main.item_pager_home.view.*

class HomePagarAdapter(private val mContext: Context, private val list: ArrayList<StoreCard>) :
    RecyclerView.Adapter<HomePagarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePagarAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_pager_home, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomePagarAdapter.ViewHolder, position: Int) {
        holder.bind(list, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(orderList: ArrayList<StoreCard>, position: Int) = with(itemView) {

// Add a PageTransformer that translates the next and previous items horizontally
// towards the center of the screen, which makes them visible
            val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
            val currentItemHorizontalMarginPx =
                resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
            val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
            val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
                page.translationX = -pageTranslationX * position
                // Next line scales the item's height. You can remove it if you don't want this effect
                page.scaleY = 1 - (0.15f * Math.abs(position))
                // If you want a fading effect uncomment the next line:
                // page.alpha = 0.25f + (1 - abs(position))
            }
            itemView.vpTech.setPageTransformer(pageTransformer)

// The ItemDecoration gives the current (centered) item horizontal margin so that
// it doesn't occupy the whole screen width. Without it the items overlap
            val itemDecoration = context?.let {
                HomeFragment.HorizontalMarginItemDecoration(
                    it,
                    R.dimen.viewpager_current_item_horizontal_margin
                )
            }
            itemDecoration?.let { vpTech.addItemDecoration(it) }

            orderList[position].store.let { list ->
                val adapter = context?.let { list?.let { it1 -> PagarAdapter(it, it1) } }
                vpTech.adapter = adapter
                vpTech.offscreenPageLimit = 4
            }


            itemView.tvTitleTech.text = orderList[position].nameEn ?: " "
        }
    }


}