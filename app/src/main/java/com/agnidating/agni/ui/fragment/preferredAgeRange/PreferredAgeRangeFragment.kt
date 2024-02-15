package com.agnidating.agni.ui.fragment.preferredAgeRange

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
import com.agnidating.agni.databinding.FragmentPreferredAgeRangeBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.fragment.settings.SettingsFragmentViewModel
import com.agnidating.agni.utils.checkNetwork
import com.agnidating.agni.utils.successDialog
import com.google.android.material.slider.LabelFormatter
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by AJAY ASIJA
 *
 */
@AndroidEntryPoint
class PreferredAgeRangeFragment : ScopedFragment() {
    private lateinit var binding: FragmentPreferredAgeRangeBinding
    private val viewModel:SettingsFragmentViewModel by viewModels()
    private val args: PreferredAgeRangeFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentPreferredAgeRangeBinding.inflate(LayoutInflater.from(context))
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
            val list=ArrayList<Float>()
            list.add(args.minAge.toFloat())
            list.add(args.maxAge.toFloat())
            binding.rangeSlider.values=list
            val to=if (list[1].toInt().toString()=="75") "75+" else list[1].toInt().toString()
            binding.range.text="${list[0].toInt()}-$to"
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
     * Handle all click events [listeners]
     *
     */
    @SuppressLint("SetTextI18n")
    private fun listeners() {
        binding.ivBack.setOnClickListener {
            binding.btGoNext.performClick()
        }
        binding.btGoNext.setOnClickListener {
            if(checkNetwork()){
                val map=HashMap<String,String>()
                map["min"]=binding.rangeSlider.values[0].toInt().toString()
                map["max"]=binding.rangeSlider.values[1].toInt().toString()
                viewModel.updateInterestedAgeRange(map)
            }
        }
        binding.rangeSlider.addOnChangeListener { slider, _, _ ->
            val to=if (slider.values[1].toInt().toString()=="75") "75+" else slider.values[1].toInt().toString()
            binding.range.text=slider.values[0].toInt().toString() +"-"+to
        }
    }
}