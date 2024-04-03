package com.capcorp.ui.user.homescreen.orders


import android.animation.AnimatorInflater
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capcorp.R
import com.capcorp.ui.user.homescreen.orders.accepted.AcceptedFragment
import com.capcorp.ui.user.homescreen.orders.completed.CompletedFragment
import com.capcorp.ui.user.homescreen.orders.expired.ExpiredFragment
import com.capcorp.ui.user.homescreen.orders.requested.RequestedFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_orders.*

/**
 * A simple [Fragment] subclass.
 *
 */
class OrdersFragment : Fragment(), TabLayout.OnTabSelectedListener {
    private var fragmentList = mutableListOf<Fragment>()
    private lateinit var tabViewPaggerAdapter: TabViewPaggerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addFragments()
        setListeners()

        //tvTitleDrop.visibility = View.GONE*/
    }

    private fun addFragments() {
        fragmentList.clear()
        fragmentList.add(RequestedFragment())
        fragmentList.add(AcceptedFragment())
        fragmentList.add(CompletedFragment())
        fragmentList.add(ExpiredFragment())
        setTabLayout()
    }

    private fun setListeners() {
        tablayout.addOnTabSelectedListener(this)
    }

    private fun setTabLayout() {
        val fragmentTitle = mutableListOf<String>()
        fragmentTitle.add(getString(R.string.requested))
        fragmentTitle.add(getString(R.string.accepted))
        fragmentTitle.add(getString(R.string.completed))
        fragmentTitle.add(getString(R.string.expired))
        tabViewPaggerAdapter =
            TabViewPaggerAdapter(childFragmentManager, fragmentList, fragmentTitle)
        viewPager.adapter = tabViewPaggerAdapter
        tablayout.setupWithViewPager(viewPager)
        tablayout.addOnTabSelectedListener(this)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) {
            viewPager.currentItem = tab.position
        }
    }

}
