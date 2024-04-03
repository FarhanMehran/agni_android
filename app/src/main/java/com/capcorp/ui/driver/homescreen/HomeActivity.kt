package com.capcorp.ui.driver.homescreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.driver.homescreen.account.AccountFragmentDriver
import com.capcorp.ui.driver.homescreen.home.requests.ShipmentRequestsFragment
import com.capcorp.ui.driver.homescreen.select_trips.SelectTripsFragment
import com.capcorp.ui.user.homescreen.HomeAdapter
import com.capcorp.ui.user.homescreen.chat.ChatsFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : BaseActivity() {

    private lateinit var adapter: HomeAdapter
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home2)
/*
        AppSocket.get().init(this)
*/
        removeShiftMode(navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        adapter = HomeAdapter(supportFragmentManager)
        adapter.addFragments(ShipmentRequestsFragment())
        adapter.addFragments(SelectTripsFragment())
        adapter.addFragments(ChatsFragment())
        //adapter.addFragments(NotificationsFragment())
        adapter.addFragments(AccountFragmentDriver())
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    viewPager.setCurrentItem(0, false)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_trips -> {
                    viewPager.setCurrentItem(1, false)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_chats -> {
                    viewPager.setCurrentItem(2, false)
                    return@OnNavigationItemSelectedListener true
                }
                /*R.id.nav_notifications -> {
                    viewPager.setCurrentItem(3, false)
                    return@OnNavigationItemSelectedListener true
                }*/
                R.id.nav_account -> {
                    viewPager.setCurrentItem(3, false)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    @SuppressLint("RestrictedApi")
    private fun removeShiftMode(view: BottomNavigationView) {
        val menuView = view.getChildAt(0) as BottomNavigationMenuView
        try {
            val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
            shiftingMode.isAccessible = true
            shiftingMode.setBoolean(menuView, false)
            shiftingMode.isAccessible = false
            for (i in 0 until menuView.childCount) {
                val item = menuView.getChildAt(i) as BottomNavigationItemView
                item.setShifting(false)
                // set once again checked value, so view will be updated
                item.itemData?.isChecked?.let { item.setChecked(it) }
            }
        } catch (e: NoSuchFieldException) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field")
        } catch (e: IllegalAccessException) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode")
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //  super.onBackPressed()
            finishAffinity()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.click_back_again_to_exit), Toast.LENGTH_SHORT)
            .show()
        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

}
