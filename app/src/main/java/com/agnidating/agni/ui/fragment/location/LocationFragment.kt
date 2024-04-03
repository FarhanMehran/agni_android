package com.agnidating.agni.ui.fragment.location

import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentLocationBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.fragment.settings.SettingsFragmentViewModel
import com.agnidating.agni.utils.checkNetwork
import com.agnidating.agni.utils.successDialog
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * Create by AJAY ASIJA
 */
@AndroidEntryPoint
class LocationFragment : ScopedFragment() {

    private lateinit var binding: FragmentLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location:Location?=null
    private val viewModel: SettingsFragmentViewModel by viewModels()
    private val args:LocationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentLocationBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etLocation.setText(args.interestedLocation)
       // fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        clickListeners()
        //locationPermission()
        bindObserver()
    }

    /**
     * Bind live data observers [bindObserver]
     */
    private fun bindObserver() {
        viewModel.baseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    successDialog(it.data.message.toString()){
                        binding.ivBack.performClick()
                    }
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }

   /* @SuppressLint("MissingPermission")
    private fun locationPermission() {
        requestPermission(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ){status,msg->
            if (status){
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        this.location=location
                        val address=getAddressFromLocation(location)
                        binding.etLocation.setText(address)
                    }
            }
            else{
                showToast(msg)
            }
        }
    }*/

    /**
     * Handle all listener
     *
     */

    private fun clickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btNext.setOnClickListener {
            val valid =checkNetwork()
            if (valid){
                val map=HashMap<String,String>()
                map["latitude"]= args.interestedLat
                map["longitude"]= args.interestedLong
                map["address"]= binding.etLocation.text.toString()
                map["status"]= "1"
                viewModel.updateInterestedLocation(map)
            }
            else if (valid&&location==null){
                showToast("Enter your location")
            }
        }

    }

    /**
     * get address from location coordinates [getAddressFromLocation]
     */
    private fun getAddressFromLocation(location: Location?): String {
        if (location!=null){
            val geoCoder= Geocoder(requireContext(), Locale.getDefault())
            val addresses= geoCoder.getFromLocation(location.latitude,location.longitude,1)[0]
            return addresses.locality
        }
        return ""
    }
}