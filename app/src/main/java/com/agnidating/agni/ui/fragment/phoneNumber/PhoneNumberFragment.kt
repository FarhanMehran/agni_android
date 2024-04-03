package com.agnidating.agni.ui.fragment.phoneNumber

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agnidating.agni.databinding.FragmentPhoneNumberBinding

class PhoneNumberFragment : Fragment() {
    private lateinit var binding:FragmentPhoneNumberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentPhoneNumberBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}