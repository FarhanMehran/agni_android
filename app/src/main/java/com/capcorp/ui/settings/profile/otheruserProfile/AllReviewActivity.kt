package com.capcorp.ui.settings.profile.otheruserProfile

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.webservice.models.ReviewDetails
import kotlinx.android.synthetic.main.activity_all_review.*


class AllReviewActivity : BaseActivity() {

    private lateinit var reviewAdapter: AllReviewAdapter
    private var reviewList = ArrayList<ReviewDetails>()
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_review)
        reviewList = intent.getSerializableExtra("list") as ArrayList<ReviewDetails>
        setRecyclerAdapter()
    }

    private fun setRecyclerAdapter() {
        layoutManager = LinearLayoutManager(this)
        rv_reviews.layoutManager = layoutManager
        reviewAdapter = AllReviewAdapter(rv_reviews.context, reviewList)
        rv_reviews.adapter = reviewAdapter
    }

}
