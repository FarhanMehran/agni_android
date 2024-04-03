package com.agnidating.agni.ui.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentProfileBinding
import com.agnidating.agni.model.profile.Data
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.checkNetwork
import com.agnidating.agni.utils.load
import com.agnidating.agni.utils.toAge
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by AJAY ASIJA
 */
@AndroidEntryPoint
class ProfileFragment : ScopedFragment() {
    private var profileData: Data?=null
    private lateinit var binding: FragmentProfileBinding

    private val viewModel:ProfileFragmentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentProfileBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
        bindObservers()
    }

    override fun onResume() {
        super.onResume()
        hitApi()
    }
    /**
     * bind all live data observers [bindObservers]
     */
    private fun bindObservers() {
        viewModel.profileLiveData.observe(viewLifecycleOwner){
            when(it){
                is ResultWrapper.Loading->{
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success->{
                    (requireActivity() as ScopedActivity).hideProgress()
                    handleResponse(it.data.data)
                }
                is ResultWrapper.Error->{
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }


    /**
     * Hit all api here [hitApi]
     */
    private fun hitApi() {
        if (checkNetwork()){
            viewModel.getProfile()
        }
    }

    /**
     * Handle listeners [listeners]
     */

    private fun listeners() {
        binding.tvSetting.setOnClickListener {
          //  findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
        }
        binding.tvEditProfile.setOnClickListener {
           /* val action=ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(profileData?.profileImg?.toTypedArray(),profileData?.bio.toString())
            findNavController().navigate(action)*/
        }
    }

    /**
     * Handle response [handleResponse]
     */
    private fun handleResponse(data: Data) {
        profileData=data
        binding.tvName.text=data.name+".${data.birthDate.toAge()}"
        binding.civProfile.load(data.profileImg[0].profile,CommonKeys.IMAGE_BASE_URL)
    }
}