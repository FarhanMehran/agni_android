package com.capcorp.ui.driver.homescreen.mydeliveries

import android.animation.AnimatorInflater
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capcorp.R
import kotlinx.android.synthetic.main.fragment_orders.*

class MyDeliveriesFragment : Fragment() {

    companion object {
        const val TAG = "com.capcorp.ui.driver.homescreen.mydeliveries.MyDeliveriesFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitle.text = "Your " + MyDeliveriesActivity.dropDownCountry + " Trip"
        //tvTitleDrop.text = MyDeliveriesActivity.dropDownCountry

        val adapter = context?.let { MyDeliveriesPagerAdapter(childFragmentManager, it) }
        viewPager.adapter = adapter
        tablayout.setupWithViewPager(viewPager)

    }

}
