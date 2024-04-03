package com.agnidating.agni.ui.fragment.distanceRange

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentDistanceRangeBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.fragment.settings.SettingsFragmentViewModel
import com.agnidating.agni.utils.checkNetwork
import com.agnidating.agni.utils.successDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList


/**
 * Created by AJAY ASIJA
 *
 */
@AndroidEntryPoint
class DistanceRangeFragment : ScopedFragment() {
    private lateinit var binding: FragmentDistanceRangeBinding
    private val viewModel: SettingsFragmentViewModel by viewModels()
    private val args: DistanceRangeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentDistanceRangeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        listeners()
        bindObserver()
    }


    /**
     * initialize preferred age slider if user has already set preferred age range
     */
    @SuppressLint("SetTextI18n")
    private fun initView() {
        if (args.minAge.isNotEmpty()&&args.maxAge.isNotEmpty()){
            binding.rangeSlider.value=args.maxAge.toFloat()
            val to=if (args.maxAge.toFloat().toInt().toString()=="100"||args.maxAge.isEmpty()) "100+" else args.maxAge.toFloat().toInt().toString()
            binding.range.text= "$to Miles"
        }
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
                    findNavController().popBackStack()
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }
    /**
     * handle click events [listeners]
     */
    @SuppressLint("SetTextI18n")
    private fun listeners() {
        binding.ivBack.setOnClickListener {
            binding.btGoNext.performClick()
        }

        binding.btGoNext.setOnClickListener {
            if(checkNetwork()){
                val map=HashMap<String,String>()
                map["min"]="0"
                map["max"]=binding.rangeSlider.value.toString()
                viewModel.updateDistanceRange(map)
            }
        }
        binding.rangeSlider.addOnChangeListener { slider, _, _ ->
            val to=if (slider.value>99) "100+" else slider.value.toInt().toString()
            binding.range.text= "$to Miles"
          /*  val to=if (slider.values[1].toInt().toString()=="100") "100+" else slider.values[1].toInt().toString()
            binding.range.text=slider.values[0].toInt().toString() +"-"+to+" Miles"*/
        }
    }


}