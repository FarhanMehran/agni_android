package com.agnidating.agni.ui.fragment.religionCommunity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentReligionCommunityBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.addImages.AddImagesActivity
import com.agnidating.agni.ui.activities.completeProfile.CompleteProfileViewModel
import com.agnidating.agni.ui.adapters.ReligionCommunityAdapter
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.communities
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.startNewActivityWithFinish
import dagger.hilt.android.AndroidEntryPoint

/**
 * Create by AJAY ASIJA on 05/27/2022
 */
@AndroidEntryPoint
class CommunityFragment:ScopedFragment() {
    private lateinit var binding: FragmentReligionCommunityBinding
    private lateinit var adapter:ReligionCommunityAdapter
    private val viewModel: CompleteProfileViewModel by viewModels()
    var update=false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentReligionCommunityBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update= arguments?.getBoolean(CommonKeys.FOR_UPDATE,false) == true
        if (update)  binding.btNext.text=getString(R.string.save)
        binding.tvTitle.text="Community"
        handleClicks()
        bindObserver()
        val community=if (update){
            communities().indexOf(arguments?.getString(CommonKeys.COMMUNITY).toString())
        }else{
            -1
        }
        adapter=ReligionCommunityAdapter(communities(),community)
        binding.rvReligionCommunity.adapter=adapter
    }

    /**
     * bind live data observers
     */
    private fun bindObserver() {
        viewModel.updateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    viewModel.updateLiveData.postValue(null)
                    if (update){
                        findNavController().popBackStack()
                    }
                    else{
                        requireActivity().startNewActivityWithFinish(AddImagesActivity::class.java,null)
                    }
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }
    /**
     * handle click listeners
     */
    private fun handleClicks() {
        if(update.not()){
            handleBack(binding.ivBack)
        }else{
            binding.ivBack.setOnClickListener {
                binding.btNext.performClick()
            }
        }
        binding.btNext.setOnClickListener {
            if (adapter.selectedIndex==-1){
                showToast("Please select your communit")
            }else{
                val map=HashMap<String,String>()
                map["community"]= communities()[adapter.selectedIndex]
                val status=if (update) "0" else "1"
                map["status"]=status
                viewModel.setCommunity(map)
            }
        }

    }
}