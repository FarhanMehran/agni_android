package com.agnidating.agni.ui.fragment.contactUs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentContactUsBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.fragment.settings.SettingsFragmentViewModel
import com.agnidating.agni.utils.checkNetwork
import com.agnidating.agni.utils.isValidEmail
import com.agnidating.agni.utils.showToast
import com.agnidating.agni.utils.successDialog
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by AJAY ASIJA
 *
 */
@AndroidEntryPoint
class ContactUsFragment:ScopedFragment() {
    private lateinit var binding: FragmentContactUsBinding
    private val viewModel: SettingsFragmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentContactUsBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

      
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
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
                    binding.ivBack.performClick()
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
            findNavController().popBackStack()
        }

        binding.btNext.setOnClickListener {
            if(isValid()){
                val map=HashMap<String,String>()
                map["email"]=binding.etEmail.text.toString()
                map["subject"]=binding.etSubject.text.toString()
                map["message"]=binding.etMessage.text.toString()
                viewModel.contactUs(map)
            }
        }
    }

    private fun isValid(): Boolean {
        var valid=checkNetwork()
        if (binding.etEmail.text!!.toString().trim().isValidEmail().not()){
            valid=false
            requireActivity().showToast("Enter valid email")
        }
        if (binding.etMessage.text!!.trim().isEmpty()){
            valid=false
            requireActivity().showToast("Enter message")
         }
        if (binding.etSubject.text!!.trim().isEmpty()){
            valid=false
            requireActivity().showToast("Enter subject")
         }

        return valid
    }
}