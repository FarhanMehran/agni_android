package com.agnidating.agni.ui.activities.completeProfile

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.ActivityCompleteProfileBinding
import com.agnidating.agni.ui.activities.getStarted.GetStarted
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.errorDialog
import com.agnidating.agni.utils.successDialog
import dagger.hilt.android.AndroidEntryPoint

/**
 * Create by AJAY ASIJA
 */
@AndroidEntryPoint
class CompleteProfileActivity:ScopedActivity() {
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivityCompleteProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavController()
        handleIntentData()
    }



    /**
     * Initialize nav controller
     */
    private fun initNavController() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostView) as NavHostFragment
        navController = navHostFragment.navController

    }

    /**
     * navigate to custom start destination to resolve back navigation
     */
    private fun handleIntentData() {
        val destination=intent.extras?.getInt(CommonKeys.DESTINATION)
        if (destination!=0){
            if (destination != null) {
                val graph=navController.navInflater.inflate(R.navigation.complete_profile)
                graph.setStartDestination(destination)
                navController.graph=graph
            }
        }
    }

    override fun onBackPressed() {
        errorDialog("You are being redirected to the Login screen.Do you want to continue?",true){
            startActivity(Intent(this,GetStarted::class.java))
            finishAffinity()
        }
    }
}