package com.agnidating.agni.ui.fragment.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.TutorialFragmentBinding

class TutorialFragment :ScopedFragment() {
    private lateinit var binding:TutorialFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= TutorialFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}