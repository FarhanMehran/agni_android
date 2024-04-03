package com.capcorp.ui.user.homescreen.home

import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.signup.phone_verification.HomePresenter
import com.capcorp.ui.splash.VideoViewActivity
import com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.activity.ShipmentActivity
import com.capcorp.utils.DialogIndeterminate
import com.capcorp.utils.PREF_LANG
import com.capcorp.utils.SharedPrefs
import com.capcorp.webservice.models.KnowMoreResponse
import com.capcorp.webservice.models.home.Data
import com.capcorp.webservice.models.product_information.ProductInformation
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), HomeContract.View {
    private lateinit var titles: Array<String>

    private lateinit var subtitles: Array<String>

    private lateinit var iconsResIds: TypedArray
    private lateinit var layoutManager: LinearLayoutManager
    private var homePresenter = HomePresenter()
    private lateinit var dialogIndeterminate: DialogIndeterminate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogIndeterminate = DialogIndeterminate(activity)
        titles = resources.getStringArray(R.array.home_titles)
        subtitles = resources.getStringArray(R.array.home_subtitles)
        iconsResIds = resources.obtainTypedArray(R.array.home_icons_res_ids)
        homePresenter.attachView(this)
        setListeners()
        homePresenter.getHomeDetails()
    }

    private fun setData(data: Data) {

        Glide.with(requireActivity()).load(data.video?.videoThumbnail)
            .apply(RequestOptions().placeholder(R.drawable.ic_package)).into(image_slider)

        layoutManager = LinearLayoutManager(activity)
        rvPager.layoutManager = layoutManager
        rvPager.adapter = activity?.let { data.storeCards?.let { it1 -> HomePagarAdapter(it, it1) } }

        image_slider.setOnClickListener {
            startActivity(
                Intent(requireActivity(), VideoViewActivity::class.java).putExtra(
                    "video",
                    data.video?.eng?.videoUrl
                )
            )
        }
    }
    private fun setDataKnowMore(data: KnowMoreResponse.Data) {

//        Glide.with(requireActivity()).load(data.video?.videoThumbnail)
//            .apply(RequestOptions().placeholder(R.drawable.ic_package)).into(image_slider)

        layoutManager = LinearLayoutManager(activity)
        rvKnowMore.layoutManager = layoutManager
        val language = SharedPrefs.with(activity).getString(PREF_LANG, "en") ?: "en"
        if (language.equals("en")) {
            rvKnowMore.adapter =
                activity?.let { data.featured_links.eng?.let { it1 -> KnowMoreAdapter(it, it1) } }
        }else{
            rvKnowMore.adapter =
                activity?.let { data.featured_links.esp?.let { it1 -> KnowMoreSpainAdapter(it, it1) } }
        }
    }


    override fun onResume() {
        super.onResume()
    }


    class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) :
        RecyclerView.ItemDecoration() {

        private val horizontalMarginInPx: Int =
            context.resources.getDimension(horizontalMarginInDp).toInt()

        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.right = horizontalMarginInPx
            outRect.left = horizontalMarginInPx
        }

    }



    private fun setListeners() {
        cvStartMyOrder.setOnClickListener(getStartedClickListener)
    }

    private var getStartedClickListener = View.OnClickListener {
        val intent = Intent(activity, ShipmentActivity::class.java)
        startActivity(intent)
    }



    override fun getHomeDetailsSuccess(data: Data) {
        dialogIndeterminate.dismiss()
        setData(data)
        homePresenter.getKnowMore()
    }

    override fun getKnowMoreSuccess(data: KnowMoreResponse.Data) {
        dialogIndeterminate.dismiss()
        setDataKnowMore(data)
    }

    override fun getProductInformationSuccess(data: ProductInformation) {

    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        dialogIndeterminate.dismiss()

    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        dialogIndeterminate.dismiss()
    }

    override fun validationsFailure(type: String?) {

    }
}
