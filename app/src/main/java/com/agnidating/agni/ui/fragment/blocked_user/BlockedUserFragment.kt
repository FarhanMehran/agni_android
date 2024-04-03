package com.agnidating.agni.ui.fragment.blocked_user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentBlockedUserBinding
import com.agnidating.agni.model.blockUser.BlockedResponse
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.adapters.BlockUserAdapter
import com.agnidating.agni.utils.custom_view.DeleteDialog
import com.agnidating.agni.utils.custom_view.UnblockDialog
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.visible
import dagger.hilt.android.AndroidEntryPoint

/**
 * Create by AJAY ASIJA on 07/11/2022
 */
@AndroidEntryPoint
class BlockedUserFragment : ScopedFragment() {

    private var adapter: BlockUserAdapter?=null
    private lateinit var binding: FragmentBlockedUserBinding
    private val viewModel:BlockUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentBlockedUserBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getBlockedUsers()
        bindObserver()
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun bindObserver() {
        viewModel.blockedResponse.observe(viewLifecycleOwner){
            when(it){
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                }
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    handleResponse(it)
                }
            }
        }
        viewModel.unblockResponse.observe(viewLifecycleOwner){
            when(it){
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    adapter?.removeAt(it.data.pos)
                    checkNoData()
                }
            }
        }
    }

    private fun handleResponse(it: ResultWrapper.Success<BlockedResponse>) {
        adapter=BlockUserAdapter(it.data.data){position,user->
            val unblockDialog= UnblockDialog{
                if (it) viewModel.unblockUser(position,user.id)
            }
            unblockDialog.user=user
            unblockDialog.show(parentFragmentManager, "")
        }
        binding.rvBlockedUsers.adapter=adapter
        checkNoData()
    }

    private fun checkNoData() {
        binding.rlNoData.isVisible=adapter!!.isEmpty()
        binding.rvBlockedUsers.isVisible=adapter!!.isEmpty().not()
    }
}