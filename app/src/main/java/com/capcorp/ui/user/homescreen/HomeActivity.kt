package com.capcorp.ui.user.homescreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.amazonaws.mobile.client.AWSMobileClient
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.user.homescreen.account.AccountFragment
import com.capcorp.ui.user.homescreen.chat.ChatsFragment
import com.capcorp.ui.user.homescreen.home.HomeFragment
import com.capcorp.ui.user.homescreen.orders.OrdersFragment
import com.capcorp.webservice.models.request_model.ShipDataRequest
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {

    private lateinit var adapter: HomeAdapter
    private var doubleBackToExitPressedOnce = false

    val shipData = ShipDataRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AppSocket.get().init(applicationContext)
        setContentView(R.layout.activity_home)
        removeShiftMode(navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        adapter = HomeAdapter(supportFragmentManager)
        adapter.addFragments(HomeFragment())
        adapter.addFragments(OrdersFragment())
        adapter.addFragments(ChatsFragment())
        //adapter.addFragments(NotificationsFragment())
        adapter.addFragments(AccountFragment())
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4
        AWSMobileClient.getInstance().initialize(this) {
            Log.e(
                "YourMainActivity",
                "AWSMobileClient is instantiated and you are connected to AWS!"
            )
        }.execute()
    }

    fun setViewPagerAdapter() {
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
                R.id.nav_orders -> {
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
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.click_back_again_to_exit), Toast.LENGTH_SHORT)
            .show()
        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
}
