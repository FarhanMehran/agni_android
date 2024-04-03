package com.capcorp.ui.user.homescreen.account.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capcorp.R

import com.capcorp.ui.user.homescreen.account.AccountAdapter
import kotlinx.android.synthetic.main.activity_card_list.tvBack
import kotlinx.android.synthetic.main.fragment_account_driver.rvItem

class RecentsPayoutActivity : AppCompatActivity() {

    //private lateinit var binding: ActivityRecentsPayoutBinding
    private lateinit var recentPayoutAdapter: RecentPayoutAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var tvBack : TextView
    private lateinit var layoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityRecentsPayoutBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_recents_payout)
        recyclerView = findViewById(R.id.recyclerView)
        tvBack = findViewById(R.id.tvBack)

        tvBack.setOnClickListener {
            onBackPressed()
        }

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recentPayoutAdapter = RecentPayoutAdapter(this)
        recyclerView.adapter = recentPayoutAdapter
    }
}