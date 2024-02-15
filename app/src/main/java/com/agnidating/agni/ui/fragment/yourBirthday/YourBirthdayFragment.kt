package com.agnidating.agni.ui.fragment.yourBirthday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.ActivityYourBirthdayBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.completeProfile.CompleteProfileViewModel
import com.agnidating.agni.utils.checkNetwork
import com.agnidating.agni.utils.isDateValid
import com.agnidating.agni.utils.openDatePicker
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by AJAY ASIJA
 */

@AndroidEntryPoint

class YourBirthdayFragment : ScopedFragment(), View.OnClickListener {
    private lateinit var date: String
    private lateinit var binding: ActivityYourBirthdayBinding
    private val viewModel: CompleteProfileViewModel by viewModels()

    private var selectedTime:Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ActivityYourBirthdayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedTime=System.currentTimeMillis()-568080000000
        handleBack(binding.ivBack)
        binding.etDD.requestFocus()
        binding.ftPicker.minValue= 3
        binding.ftPicker.maxValue= 10
        binding.inchPicker.minValue= 0
        binding.inchPicker.maxValue= 11
        listeners()
        bindObserver()
    }

    private fun listeners() {
        binding.etDD.setOnClickListener(this)
        binding.etMM.setOnClickListener(this)
        binding.etYYYY.setOnClickListener(this)
        binding.btNext.setOnClickListener {
            if (isValid()) {
                val map=HashMap<String,String>()
                map["dob"]=date
                map["height"]= getSelectedHeight()
                viewModel.setBirthdate(map)
            }
        }
    }

    private fun getSelectedHeight(): String {
        if (binding.inchPicker.value==0){
            return "${binding.ftPicker.value} ft."
        }
        return "${binding.ftPicker.value} ft. ${binding.inchPicker.value} in."
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
                    findNavController().navigate(R.id.action_navBirthday_to_gender)
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())

                }
            }
        }
    }//

    private fun isValid(): Boolean {
        var valid = checkNetwork()
        date = binding.etMM.text.toString() + "-" + binding.etDD.text.toString() + "-" + binding.etYYYY.text.toString()
        if (date.isDateValid("MM-dd-yyyy").not()) {
            valid = false
            showToast(getString(R.string.birth_date_error))
        }
        return valid
    }

    override fun onClick(v: View?) {
        when(v){
            binding.etDD,binding.etMM,binding.etYYYY->{
                requireContext().openDatePicker(selectedTime){
                    binding.etDD.setText(it.dd)
                    binding.etMM.setText(it.month)
                    binding.etYYYY.setText(it.year)
                    selectedTime=it.timeInMillis
                }
            }
        }
    }
}