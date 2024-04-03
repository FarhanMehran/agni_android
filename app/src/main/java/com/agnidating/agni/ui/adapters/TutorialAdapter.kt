package com.agnidating.agni.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.agnidating.agni.ui.fragment.tutorial.TutorialFragment

/**
 * Create by AJAY ASIJA on 04/21/2022
 */
class TutorialAdapter(fa:FragmentActivity):FragmentStateAdapter(fa) {
    override fun getItemCount()=1

    override fun createFragment(position: Int): Fragment {
        return TutorialFragment()
    }
}