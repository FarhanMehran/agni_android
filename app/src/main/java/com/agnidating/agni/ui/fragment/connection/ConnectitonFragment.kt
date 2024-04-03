package com.agnidating.agni.ui.fragment.connection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentConnectionBinding
import com.agnidating.agni.ui.adapters.MatchTabAdapter
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Create by AJAY ASIJA on 05/09/2022
 */
class ConnectitonFragment :ScopedFragment() {

    private lateinit var binding:FragmentConnectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentConnectionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter=MatchTabAdapter(requireActivity())
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if (position==0){
                tab.text="Requests"
            }else{
                tab.text="Accepted"
            }
        }.attach()
    }
}