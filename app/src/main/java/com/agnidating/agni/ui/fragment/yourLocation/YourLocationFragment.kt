package com.agnidating.agni.ui.fragment.yourLocation

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.ActivityYourLocationBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.completeProfile.CompleteProfileViewModel
import com.agnidating.agni.utils.checkNetwork
import com.agnidating.agni.utils.requestPermission
import dagger.hilt.android.AndroidEntryPoint

/**
* Created by AJAY ASIJA
*/

@AndroidEntryPoint
class YourLocationFragment : ScopedFragment() {
    private lateinit var binding: ActivityYourLocationBinding

    private var location:Location?=null

    private val viewModel: CompleteProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ActivityYourLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBack(binding.ivBack)
        viewModel.initLocationClient(requireContext())
        viewModel.createLocationCallback()
        locationPermission()
        listeners()
        bindObserver()
    }



    @SuppressLint("MissingPermission")
    private fun locationPermission() {
        requestPermission(
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ){status,msg->
            if (status){
                viewModel.fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        if (location==null){
                            viewModel.checkLocationSetting(requireActivity())
                        }else{
                            this.location=location
                            val address=viewModel.getAddressFromLocation(location,requireContext())
                            if (address.isEmpty()){
                                viewModel.checkLocationSetting(requireActivity())
                            }else{
                                binding.etLocation.setText(address)
                            }
                        }
                    }

            }
            else{
                showToast(msg)
            }
        }
    }


    private fun listeners() {

        binding.btNext.setOnClickListener {
            val valid =checkNetwork()
            if (valid&&location!=null){
                val map=HashMap<String,String>()
                map["latitude"]= location!!.latitude.toString()
                map["longitude"]= location!!.longitude.toString()
                map["address"]= binding.etLocation.text.toString()
                viewModel.setLocation(map)
            }
            else if (valid && binding.etLocation.text?.trim()?.toString().isNullOrEmpty()){
                showToast("Enter your location")
            }
        }
    }

    private fun bindObserver() {
        viewModel.updateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    viewModel.updateLiveData.postValue(null)
                    findNavController().navigate(R.id.action_navLocation_to_navBirthday)
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
        viewModel.locationLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    location=it.data
                    val address=viewModel.getAddressFromLocation(location,requireContext())
                    binding.etLocation.setText(address)
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }
}