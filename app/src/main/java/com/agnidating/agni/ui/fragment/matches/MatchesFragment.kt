package com.agnidating.agni.ui.fragment.matches

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.BuyPremiumAlertBinding
import com.agnidating.agni.databinding.FragmentMatchesBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.plans.PlanSelectionActivity
import com.agnidating.agni.ui.adapters.MatchedAdapter
import com.agnidating.agni.ui.adapters.MatchesAdapter
import com.agnidating.agni.ui.fragment.chat.ChatActivity
import com.agnidating.agni.ui.fragment.userDetails.UserDetailsDialogFragment
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.agnidating.agni.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MatchesFragment:ScopedFragment()
{
    private lateinit var matchesAdapter: MatchesAdapter

    private lateinit var matchedAdapter: MatchedAdapter
    private lateinit var binding: FragmentMatchesBinding
    private val viewModel:MatchesViewModel by viewModels()
    private var tabPosition:Int?=0
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode==Activity.RESULT_OK){
            viewModel.matchesDataStore?.invalidate()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMatchesBinding.inflate(layoutInflater)
        bindObserver()
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabPosition=arguments?.getInt("position")
        if (tabPosition==0){
            matchesAdapter= MatchesAdapter(sharedPrefs){accept,user->
               if (accept==2){
                   val userInfoDialogFragment = UserDetailsDialogFragment()
                   userInfoDialogFragment.id = user.sourceId.toString()
                   userInfoDialogFragment.hideMessage=true
                   userInfoDialogFragment.matchStatus=false
                   userInfoDialogFragment.callBack={
                       if (it){
                           viewModel.matchesDataStore?.invalidate()
                       }
                   }
                   userInfoDialogFragment.show(parentFragmentManager, "")
                  /* findNavController().navigate(R.id.action_matches_to_userDetailsDialogFragment,Bundle().apply {
                       putString(CommonKeys.USER_ID,user.sourceId)
                   })*/
               }
               else if (accept==3){
                    openBuyPremiumDialog()
               }
               else{
                   val map=HashMap<String,String>()
                   map["sourceId"]=user.sourceId!!
                   map["status"]=accept.toString()
                   viewModel.onAcceptReject(map,user)
               }
            }
            binding.tvNoMatch.text="You Have No Pending Request!"
            binding.rvMatches.adapter=matchesAdapter
            setLoadStateListener()
        }
        else{
            matchedAdapter= MatchedAdapter(){type,user->
                val userInfoDialogFragment = UserDetailsDialogFragment()
                userInfoDialogFragment.id = user.id.toString()
                userInfoDialogFragment.hideMessage=false
                userInfoDialogFragment.matchStatus=true
                userInfoDialogFragment.callBack={
                    if (it){
                        viewModel.matchesDataStore?.invalidate()
                    }
                }
                userInfoDialogFragment.show(parentFragmentManager, "")
            }
            binding.rvMatches.adapter=matchedAdapter
            setMatchedLoadStateListener()
        }
        setListerners()
    }

    /**
     * Open buy premium dialog when free swipes exceeded
     */
    private fun openBuyPremiumDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val binding = BuyPremiumAlertBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        val alertDialog = builder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        binding.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        binding.btBuyFlower.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(requireActivity(), PlanSelectionActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setListerners() {
        binding.tvUnlock.setOnClickListener {
            val intent= Intent(requireActivity(), PlanSelectionActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * set load state listener for matches screen
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun setLoadStateListener() {
        matchesAdapter.addLoadStateListener {
            when(it.refresh){
                is LoadState.Loading-> (requireActivity() as ScopedActivity).showProgress()
                else->{
                    (requireActivity() as ScopedActivity).hideProgress()
                    matchesAdapter.notifyDataSetChanged()
                    if (matchesAdapter.itemCount==0){
                        binding.tvNoMatch.visible()
                        binding.ivNoMatch.visible()
                    }else{
                        binding.tvNoMatch.gone()
                        binding.ivNoMatch.gone()
                    }
                }
            }
        }
    }
    /**
     * set load state listener for matches screen
     */
    private fun setMatchedLoadStateListener() {
        matchedAdapter.addLoadStateListener {
            when(it.refresh){
                is LoadState.Loading-> (requireActivity() as ScopedActivity).showProgress()
                else->{
                    (requireActivity() as ScopedActivity).hideProgress()
                    if (matchedAdapter.itemCount==0){
                        binding.tvNoMatch.visible()
                        binding.ivNoMatch.visible()
                    }
                    else{
                        binding.tvNoMatch.gone()
                        binding.ivNoMatch.gone()
                    }
                }
            }
        }
    }

    private fun bindObserver() {
        viewModel.homeList?.observe(viewLifecycleOwner){
            viewLifecycleOwner.lifecycleScope.launch{
                matchesAdapter.submitData(it)
            }
        }
        viewModel.matchedList?.observe(viewLifecycleOwner) {
            viewLifecycleOwner.lifecycleScope.launch {
                matchedAdapter.submitData(it)
            }
        }
        if (this::matchedAdapter.isInitialized){
            matchedAdapter.removeEmpty.observe(viewLifecycleOwner){
                if (it){
                    binding.ivNoMatch.gone()
                    binding.tvNoMatch.gone()
                }
            }
        }
        viewModel.baseLiveData.observe(viewLifecycleOwner){
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.data.message.toString())

                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }

    /**
     * show list of users like this user [showRequestList]
     */
    private fun showRequestList() {
        viewModel.updateList()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.matches?.collectLatest {
                matchesAdapter.submitData(it)
            }
        }
    }


    /**
     * show list of matched users[showMatchedUsers]
     */
    private fun showMatchedUsers() {
        viewModel.updateMatchedList()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.matchedUser?.collectLatest {
                matchedAdapter.submitData(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (tabPosition==0){
            showRequestList()
        }else{
            showMatchedUsers()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.baseLiveData= MutableLiveData(null)
    }
}