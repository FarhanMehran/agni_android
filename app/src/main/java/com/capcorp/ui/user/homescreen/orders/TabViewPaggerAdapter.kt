package com.capcorp.ui.user.homescreen.orders

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TabViewPaggerAdapter(
    fm: FragmentManager, fragmentList: List<Fragment>,
    titles: List<String>
) : FragmentStatePagerAdapter(fm) {

    private var fragmentList = ArrayList<Fragment>()
    private var titles = ArrayList<String>()

    init {
        this.fragmentList = fragmentList as ArrayList<Fragment>
        this.titles = titles as ArrayList<String>
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}
