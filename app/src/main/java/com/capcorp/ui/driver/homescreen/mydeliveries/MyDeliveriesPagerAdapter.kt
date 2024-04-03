package com.capcorp.ui.driver.homescreen.mydeliveries

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.capcorp.R
import com.capcorp.ui.driver.homescreen.mydeliveries.deliveries.DeliveriesFragment
import com.capcorp.utils.OrderStatusDriver

class MyDeliveriesPagerAdapter(fm: FragmentManager, private val context: Context?) :
    FragmentStatePagerAdapter(fm) {

    private var oderStatus = arrayOf(
        OrderStatusDriver.OFFER_MADE,
        OrderStatusDriver.ACCEPTED,
        OrderStatusDriver.COMPLETED
    )

    override fun getItem(position: Int): Fragment {
        return DeliveriesFragment.newInstance(oderStatus[position])
    }

    override fun getCount(): Int {
        return oderStatus.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context?.resources?.getStringArray(R.array.order_titles)?.get(position)
    }

}