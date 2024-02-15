package com.agnidating.agni.ui.fragment.favourite


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentFavoriteBinding
import com.agnidating.agni.model.home.User
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.dashboard.DashboardActivity
import com.agnidating.agni.ui.adapters.FavouriteAdapter
import com.agnidating.agni.ui.fragment.chat.ChatActivity
import com.agnidating.agni.ui.fragment.userDetails.UserDetailsDialogFragment
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.custom_view.FlowerDialog
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteFragment:ScopedFragment() {
    private var addFlower: Int=0
    private lateinit var favouriteAdapter: FavouriteAdapter
    private lateinit var binding: FragmentFavoriteBinding
    private val viewModel:FavoriteViewModel by viewModels()
    private var myFlowers=0
    private var data=ArrayList<User>()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private var isConnected=false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= FragmentFavoriteBinding.inflate(layoutInflater)
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initBillingClient(requireContext())
        binding.tvTotalFlowers.setOnClickListener {
            buyFlower()
        }
        setAdapter()
        bindObserver()
    }

    override fun onResume() {
        super.onResume()
        getTopProfiles()
    }

    /**
     * bind livedata observers [bindObserver]
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun bindObserver() {
        viewModel.topLiveData.observe(viewLifecycleOwner){
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    setFlowers(it.data.flowerData.totalFlowers)
                    data.clear()
                    data.addAll(it.data.data)
                    handleNoDataUi()
                    favouriteAdapter.notifyDataSetChanged()
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }

        viewModel.baseViewModel.observe(viewLifecycleOwner){
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    myFlowers=it.data.data.totalFlowers.toInt()
                    setFlowers(myFlowers.toString())
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
        viewModel.connectStatus.observe(viewLifecycleOwner){
            isConnected=it
        }
        viewModel.buyStatus.observe(viewLifecycleOwner){
            if (it && addFlower > 0) {
                addFlowers(addFlower)
                viewModel.buyStatus.postValue(false)
            }
        }

        viewModel.addFlowerLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    myFlowers = it.data.data.totalFlowers.toInt()
                    setFlowers(myFlowers.toString())
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }

    private fun handleNoDataUi() {
        binding.rlNoData.isVisible=data.isEmpty()
        binding.rvFavourite.isVisible=data.isNotEmpty()
    }

    /**
     * set total no of flowers users already have
     */
    @SuppressLint("SetTextI18n")
    private fun setFlowers(totalFlowers: String) {
        myFlowers=totalFlowers.toInt()
        binding.tvTotalFlowers.text="Flowers ($totalFlowers)"
    }

    /**
     * hit api for getting top profiles [getTopProfiles]
     */
    private fun getTopProfiles() {
        viewModel.getTopProfiles()
    }

    private fun sendFlower(user: User, flowers: Int, msg: String) {

        myFlowers-=flowers
        setFlowers(myFlowers.toString())
        val map=HashMap<String,String>()
        map["targetId"]=user.id+""
        map["msg"]=msg
        map["flower"]=flowers.toString()
        map["status"]="1"
        viewModel.sendRequest(map)

        //viewModel.connectSocket(msg.ifEmpty { "Flower" },user.id.toInt(),sharedPrefs.getString(CommonKeys.USER_ID)!!.toInt(),flowers)
    }
    private fun addFlowers(flowers: Int) {
        val map = HashMap<String, String>()
        map["flower"] = flowers.toString()
        map["transactionId"] = "tx122"
        map["type"] = "1"
        viewModel.addFlowers(map)
    }

    /**
     * set adapter for recyclerview [setAdapter]
     */
    private fun setAdapter() {
        favouriteAdapter= FavouriteAdapter(data){user,type->
           if (type==FavouriteAdapter.USER_PROFILE){

               val userInfoDialogFragment = UserDetailsDialogFragment()
               userInfoDialogFragment.id = user.id
               userInfoDialogFragment.hideMessage=false
               userInfoDialogFragment.matchStatus=user.matchStatus==1
               userInfoDialogFragment.callBack={
                  /* if (it){
                       viewModel.matchesDataStore?.invalidate()
                   }*/
               }
               userInfoDialogFragment.show(parentFragmentManager, "")

              /* findNavController().navigate(R.id.action_favouriteFragment_to_userDetailsDialogFragment,Bundle().apply {
                   putString(CommonKeys.USER_ID,user.id)
               })*/
           }else{
              if (user.matchStatus==0){
                  if (myFlowers>0){
                      handleSendFlower(user)
                  }else{
                      buyFlower()
                  }
              }
              else{
                  val userId= user.id.toInt()
                  val intent= Intent(requireActivity(), ChatActivity::class.java)
                  intent.putExtra(CommonKeys.RECEIVER_ID,userId)
                  startActivity(intent)
              }
           }
        }
        binding.rvFavourite.adapter=favouriteAdapter
        val snapHelper=PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvFavourite)
    }

    /**
     * handle logic for buy flower
     */
    private fun buyFlower() {
        val buyFlowerDialog=FlowerDialog()
        buyFlowerDialog.title=getString(R.string.flower_heading)
        buyFlowerDialog.desc=getString(R.string.flower_sub_heading)
        buyFlowerDialog.type=0
        buyFlowerDialog.onDismiss={it,_->
            addFlower=it
            val quantity =if (it==1) "roses" else "rose_group"
            if (isConnected){
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.launchBillingFlow(requireActivity() as DashboardActivity,quantity)
                }
            }
        }
        buyFlowerDialog.show(parentFragmentManager,"")
    }

    /**
     * implement Send flower logic
     */
    private fun handleSendFlower(user: User) {
        val sendFlowerDialog=FlowerDialog()
        sendFlowerDialog.title=getString(R.string.flower_heading)
        sendFlowerDialog.desc=getString(R.string.flower_sub_heading)
        sendFlowerDialog.type=1
        sendFlowerDialog.onDismiss={flowers,msg->
           if (user.matchStatus==0){
               if (flowers <= myFlowers) {
                   sendFlower(user, flowers, msg)
               }
               else{
                   sendFlowerDialog.dismiss()
                   buyFlower()
               }
           }else{
               //open chat screen
           }
        }
        sendFlowerDialog.show(parentFragmentManager,"")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.baseViewModel.postValue(null)
        viewModel.topLiveData.postValue(null)
        data.clear()
        favouriteAdapter.notifyDataSetChanged()
    }
}