package com.agnidating.agni.ui.fragment.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.BuyPremiumAlertBinding
import com.agnidating.agni.databinding.HomeFragmentBinding
import com.agnidating.agni.model.home.User
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.dashboard.DashboardActivity
import com.agnidating.agni.ui.activities.notifications.NotificationsActivity
import com.agnidating.agni.ui.activities.plans.PlanSelectionActivity
import com.agnidating.agni.ui.adapters.HomeCardsAdapter
import com.agnidating.agni.ui.fragment.optionBottomSheet.OptionBottomSheet
import com.agnidating.agni.ui.fragment.userDetails.UserDetailsDialogFragment
import com.agnidating.agni.utils.*
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.Direction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.agnidating.agni.utils.CommonKeys.OPTION
import com.agnidating.agni.utils.CommonKeys.SEND_REQUEST
import com.agnidating.agni.utils.CommonKeys.FLOWER
import com.agnidating.agni.utils.CommonKeys.USER_DETAILS
import com.agnidating.agni.utils.custom_view.FlowerDialog
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import javax.inject.Inject


/**
 * Created by AJAY ASIJA
 *
 */

@AndroidEntryPoint
class HomeFragment : ScopedFragment() {

    private lateinit var layoutManager: CardStackLayoutManager
    private var adapter: HomeCardsAdapter? = null
    private lateinit var binding: HomeFragmentBinding
    private val viewModel: HomeViewModel by viewModels()
    private var myFlowers = 0
    private var addFlower: Int = 0
    private var isConnected = false
    private val notificationLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode== Activity.RESULT_OK){
            (requireActivity() as DashboardActivity).goToMatch()
        }
    }

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
        viewModel.initBillingClient(requireContext())
        (requireActivity() as ScopedActivity).showProgress()
        setupAdapter()
        listeners()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity().isOnline()) {
            loadProfiles()
        } else {
            errorDialog("Network Unavailable Please connect to internet") {}
        }
    }

    private fun loadProfiles() {
        viewModel.getProfiles()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profiles?.collectLatest {
                (requireActivity() as ScopedActivity).hideProgress()
                adapter?.submitData(it)
            }
        }
    }

    private fun bindObserver() {
        viewModel.homeList?.observe(viewLifecycleOwner) {
            viewLifecycleOwner.lifecycleScope.launch {
                adapter?.submitData(it)
            }
        }
        viewModel.baseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> {
                   // (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    //showToast(it.data.message.toString())
                    if (layoutManager.childCount==0){
                        binding.rlNoData.visible()
                        binding.cardStackView.gone()
                    }else{
                        binding.rlNoData.gone()
                    }
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    if (it.error?.message.toString() == "Your 10 swipe limit per day has finished.") {
                        openBuyPremiumDialog()
                        binding.cardStackView.rewind()
                    }
                }
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
                    setFlowers()
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }

        viewModel.flowersLiveData.observe(viewLifecycleOwner) {
            myFlowers = it
            setFlowers()
        }
        viewModel.unreadNotifications.observe(viewLifecycleOwner) {
           binding.toolbar.notificationDot.isVisible=it>0
        }

        viewModel.connectStatus.observe(viewLifecycleOwner) {
            isConnected = it
        }
        viewModel.buyStatus.observe(viewLifecycleOwner) {
            if (it && addFlower > 0) {
                addFlowers(addFlower)
                viewModel.buyStatus.postValue(false)
            }
        }
        viewModel.sentLiveData.observe(viewLifecycleOwner) {
           if(it!=null){
               showToast(it)
           }
        }
    }

    /**
     * update value in my flowers test
     */
    @SuppressLint("SetTextI18n")
    private fun setFlowers() {
        binding.toolbar.tvTotalFlowers.text = "Roses ( $myFlowers )"
    }

    private fun addFlowers(flowers: Int) {
        val map = HashMap<String, String>()
        map["flower"] = flowers.toString()
        map["transactionId"] = "tx122"
        map["type"] = "1"
        viewModel.addFlowers(map)
    }

    /**
     * handle logic for buy flower
     */
    private fun buyFlower() {
        val buyFlowerDialog = FlowerDialog()
        buyFlowerDialog.title = getString(R.string.flower_heading)
        buyFlowerDialog.desc = getString(R.string.flower_sub_heading)
        buyFlowerDialog.type = 0
        buyFlowerDialog.onDismiss = { it, _ ->
            addFlower = it
            val quantity = if (it == 1) "roses" else "rose_group"
            if (isConnected) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.launchBillingFlow(requireActivity() as DashboardActivity, quantity)
                }
            }
        }
        buyFlowerDialog.show(parentFragmentManager, "")
    }

    /**
     * implement Send flower logic
     */
    private fun handleSendFlower(user: User) {
        val sendFlowerDialog = FlowerDialog()
        sendFlowerDialog.title = getString(R.string.flower_heading)
        sendFlowerDialog.desc = getString(R.string.flower_sub_heading)
        sendFlowerDialog.type = 1
        sendFlowerDialog.onDismiss = { flowers, msg ->
            if (flowers <= myFlowers) {
                sendFlower(user, flowers, msg)
            }
            else{
                sendFlowerDialog.dismiss()
                buyFlower()
            }
        }
        sendFlowerDialog.show(parentFragmentManager, "")
    }

    /**
     *
     */
    private fun sendFlower(user: User, flowers: Int, msg: String) {
        myFlowers -= flowers
        setFlowers()
        val map=HashMap<String,String>()
        map["targetId"]=user.id+""
        map["msg"]=msg
        map["flower"]=flowers.toString()
        map["status"]="1"
        viewModel.sendRequest(map)
        swipeCard()

       /* viewModel.connectSocket(
            msg.ifEmpty { "Flower" }, user.id.toInt(), sharedPrefs.getString(
                CommonKeys.USER_ID
            )!!.toInt(), flowers
        )*/
    }

    /**
     * setup card stack view with [swipeListener]
     */
    private fun setupAdapter() {
        layoutManager = CardStackLayoutManager(requireContext(), swipeListener)
        layoutManager.setDirections(Direction.HORIZONTAL)
        layoutManager.setCanScrollVertical(false)
        adapter = HomeCardsAdapter { item, type ->
            /**
             * if user click heart icon(type=1) then send request to user otherwise redirect to user details screen
             */
            when (type) {
                USER_DETAILS -> {
                  /*  findNavController().navigate(R.id.action_homeFragment_to_userDetailsDialogFragment,Bundle().apply {
                        putString(CommonKeys.USER_ID,item.id)
                    })*/

                    val userInfoDialogFragment = UserDetailsDialogFragment()
                    userInfoDialogFragment.id = item.id
                    userInfoDialogFragment.hideMessage=false
                    userInfoDialogFragment.matchStatus=false
                    userInfoDialogFragment.callBack={
                       /* if (it){
                            viewModel.matchesDataStore?.invalidate()
                        }*/
                    }
                    userInfoDialogFragment.show(parentFragmentManager, "")
                }
                SEND_REQUEST -> {
                    val map=HashMap<String,String>()
                    map["targetId"]=item.id+""
                    map["msg"]=adapter?.msgView?.text.toString().ifEmpty { "" }
                    map["flower"]="0"
                    map["status"]="1"
                    viewModel.sendRequest(map)
                    swipeCard()
                    adapter?.msgView?.hideKeyboard()
                   /* val msg = adapter?.msgView?.text.toString()
                    if (msg.isNotEmpty()){
                        viewModel.connectSocket(
                            msg, item.id.toInt(), sharedPrefs.getString(
                                CommonKeys.USER_ID
                            )!!.toInt()
                        )
                        adapter?.msgView?.setText("")
                    }*/
                }
                FLOWER -> {
                    if (myFlowers > 0) {
                        handleSendFlower(item)
                    } else {
                        buyFlower()
                    }
                }
                OPTION -> {
                    val bottomSheet=OptionBottomSheet(item)
                    bottomSheet.callback={
                        viewModel.reload()
                    }
                    bottomSheet.show(parentFragmentManager, "")
                }
            }
        }
        binding.cardStackView.adapter = adapter
        binding.cardStackView.layoutManager = layoutManager
    }

    /**
     * programmatically swipe card stack
     */
    private fun swipeCard() {
        val setting = SwipeAnimationSetting.Builder()
            .setDirection(Direction.Top)
            .setDuration(Duration.Normal.duration)
            .setInterpolator(AccelerateInterpolator())
            .build()
        layoutManager.setSwipeAnimationSetting(setting)
        binding.cardStackView.swipe()
    }

    private fun listeners() {
        binding.toolbar.ivNotifications.setOnClickListener {
            notificationLauncher.launch(Intent(requireContext(), NotificationsActivity::class.java))
        }
        adapter?.addLoadStateListener {
            when(it.refresh){
                is LoadState.Loading-> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is LoadState.NotLoading->{
                    (requireActivity() as ScopedActivity).hideProgress()
                    if (adapter?.itemCount==0){
                        binding.cardStackView.gone()
                        binding.rlNoData.visible()
                        binding.tvOr.gone()
                        binding.tvRejected.gone()
                    }
                    else{
                        binding.rlNoData.gone()
                        binding.cardStackView.visible()
                    }
                }
                is LoadState.Error->{
                    (requireActivity() as ScopedActivity).hideProgress()
                    "Some error occurred".toast(requireContext())
                }
            }
        }
        binding.tvChangePreferences.setOnClickListener {
            (requireActivity() as DashboardActivity).goToSetting()
        }
        binding.tvRejected.setOnClickListener {
           viewLifecycleOwner.lifecycleScope.launch {
               adapter?.submitData(PagingData.empty())
           }
           viewModel.reload()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.baseLiveData.postValue(null)
        viewModel.addFlowerLiveData.postValue(null)
        viewModel.sentLiveData.postValue(null)
    }


    /**
     * Handle card swipe event
     */

    private val swipeListener = object : MyCardStackListener() {
        override fun onCardSwiped(direction: Direction?) {
            super.onCardSwiped(direction)
            val item = adapter?.getUser(layoutManager.topPosition - 1)
            // If swipe right like otherwise dislike
            if (direction == Direction.Right) {
                val map = HashMap<String, String>()
                map["targetId"] = item?.id + ""
                map["msg"] = ""
                map["flower"]="0"
                map["status"] = "1"
                viewModel.sendRequest(map)
            }
            else if (direction == Direction.Left) {
                val map = HashMap<String, String>()
                map["targetId"] = item?.id + ""
                map["msg"] = ""
                map["flower"]="0"
                map["status"] = "0"
                viewModel.sendRequest(map)
            }
        }
    }
}