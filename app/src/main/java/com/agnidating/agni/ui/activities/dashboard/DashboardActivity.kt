package com.agnidating.agni.ui.activities.dashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.ActivityDashboardBinding
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.agnidating.agni.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : ScopedActivity(), NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    var isStart = true
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        manageBottomNavigation()
        handleBroadcast()
    }


    private fun handleBroadcast() {
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                   showHideBadge(true)
                }

            }, IntentFilter(CommonKeys.CHAT_NOTIFICATION))
    }

    fun showHideBadge(show: Boolean) {
        var badge = binding.bottomNavigationView.getOrCreateBadge(R.id.messageFragment)
        badge.backgroundColor=ContextCompat.getColor(this,R.color.orange_pink)
        badge.isVisible = show
        badge.number=9
        badge.verticalOffset=8
        badge.horizontalOffset=8
        badge.badgeTextColor=ContextCompat.getColor(this,R.color.orange_pink)
    }


    private fun manageBottomNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostView) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(this)
        showHideBadge(sharedPrefs.getBoolean(CommonKeys.HAVE_NEW_MESSAGES))
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment -> {
                binding.bottomNavigationView.visible()
            }
            R.id.connections -> {
                binding.bottomNavigationView.visible()
            }
            R.id.messageFragment -> {
                binding.bottomNavigationView.visible()
            }
            R.id.favouriteFragment -> {
                binding.bottomNavigationView.visible()
            }
            R.id.settingFragment -> {
                binding.bottomNavigationView.visible()
            }

            else -> {
                binding.bottomNavigationView.gone()
            }
        }

    }

    fun goToSetting() {
        binding.bottomNavigationView.selectedItemId = R.id.settingFragment
    }

    fun goToMatch() {
        binding.bottomNavigationView.selectedItemId = R.id.connections
    }

}