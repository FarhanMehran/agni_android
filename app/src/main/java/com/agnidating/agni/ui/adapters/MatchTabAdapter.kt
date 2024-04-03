package com.agnidating.agni.ui.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.agnidating.agni.ui.fragment.matches.MatchesFragment

/**
 * Create by AJAY ASIJA on 05/09/2022
 */
class MatchTabAdapter(fa:FragmentActivity):FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        val bundle=Bundle()
        bundle.putInt("position",position)
        val fragment=MatchesFragment()
        fragment.arguments=bundle
        return fragment
    }


}