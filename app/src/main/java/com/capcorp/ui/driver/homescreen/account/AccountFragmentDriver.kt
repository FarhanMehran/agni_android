package com.capcorp.ui.driver.homescreen.account


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.driver.homescreen.home.ImageViewerActivity
import com.capcorp.ui.settings.profile.profile.ProfileActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.account.AccountAdapter
import com.capcorp.ui.user.homescreen.account.AccountContract
import com.capcorp.ui.user.homescreen.account.AccountInterface
import com.capcorp.ui.user.homescreen.account.AccountPresenter
import com.capcorp.utils.*
import com.capcorp.webservice.models.Account
import com.capcorp.webservice.models.Faq
import com.capcorp.webservice.models.Reason
import com.capcorp.webservice.models.SignupModel
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account_driver.*
import kotlinx.android.synthetic.main.fragment_account_driver.btnSwitch
import kotlinx.android.synthetic.main.fragment_account_driver.ivUserProfile
import kotlinx.android.synthetic.main.fragment_account_driver.ivVerified
import kotlinx.android.synthetic.main.fragment_account_driver.ratingBarUser
import kotlinx.android.synthetic.main.fragment_account_driver.rvItem
import kotlinx.android.synthetic.main.fragment_account_driver.tvEditProfile
import kotlinx.android.synthetic.main.fragment_account_driver.tvUsername

const val RQ_CODE_PROFILE = 105

class AccountFragmentDriver : Fragment(), View.OnClickListener, AccountContract.View,
    AccountInterface {

    override fun blockPush(signupModel: SignupModel) {
        signupModel.isMute?.let {
            SharedPrefs.with(activity).save(IS_MUTE, signupModel.isMute)

        }
    }

    override fun reasonSuccess(data: List<Reason>) {
    }

    override fun supportSuccess() {
    }

    override fun onFaqSuccess(data: List<Faq>) {
    }

    private var userDetail: SignupModel? = null
    private val presenter = AccountPresenter()
    private lateinit var dialogIndeterminate: DialogIndeterminate
    private var userType = ""
    private var isMute = ""
    private var mIsVerified = false
    private var videoUrl = ""
    private var imageUrl = ""

    private lateinit var accountAdapter: AccountAdapter
    private var list: ArrayList<Account> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_driver, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(activity)
        userDetail = SharedPrefs.with(activity).getObject(USER_DATA, SignupModel::class.java)
        userType = SharedPrefs.with(activity).getString(USER_TYPE, "")
        if (userType.equals(UserType.USER)) {
            btnSwitch.text = getString(R.string.switch_to_driver)
        } else if (userType.equals(UserType.DRIVER)) {
            btnSwitch.text = getString(R.string.switch_to_shopper)
        } else {
            btnSwitch.text = getString(R.string.switch_to_shopper)
        }
        setUserDetails()
        setListeners()
        setAdapter()
    }

    override fun setUserVisibleHint(visible: Boolean) {
        super.setUserVisibleHint(visible)
        if (visible && isResumed) {
            onResume()
        }
    }

    private lateinit var layoutManager: LinearLayoutManager

    private fun setAdapter() {
        list.add(
            Account(
                R.drawable.ic_account_payment, getString(R.string.payment_method),
                isLanguage = false,
                isNotification = false
            )
        )
        list.add(
            Account(
                R.drawable.ic_account_payment, getString(R.string.payouts),
                isLanguage = false,
                isNotification = false
            )
        )
        list.add(
            Account(
                R.drawable.ic_account_notification, getString(R.string.notifications),
                isLanguage = false,
                isNotification = true
            )
        )
        list.add(
            Account(
                R.drawable.ic_account_invite, getString(R.string.invite_friends),
                isLanguage = false,
                isNotification = false
            )
        )
        list.add(
            Account(
                R.drawable.ic_account_faq,
                getString(R.string.faq),
                isLanguage = false,
                isNotification = false
            )
        )
        list.add(
            Account(
                R.drawable.ic_account_about, getString(R.string.about_h2d),
                isLanguage = false,
                isNotification = false
            )
        )
        list.add(
            Account(
                R.drawable.ic_account_call, getString(R.string.contact_support),
                isLanguage = false,
                isNotification = false
            )
        )
        list.add(
            Account(
                R.drawable.ic_language, getString(R.string.change_language),
                isLanguage = true,
                isNotification = false
            )
        )
        list.add(
            Account(
                R.drawable.ic_account_about, getString(R.string.tutories),
                isLanguage = false,
                isNotification = false
            )
        )
        list.add(
            Account(
                R.drawable.ic_account_logout, getString(R.string.logout),
                isLanguage = false,
                isNotification = false
            )
        )


        layoutManager = LinearLayoutManager(activity)
        rvItem.layoutManager = layoutManager
        accountAdapter = AccountAdapter(rvItem.context, list, this)
        rvItem.adapter = accountAdapter
    }

    override fun onResume() {
        super.onResume()
        getProfileDetails()
        //isMute = SharedPrefs.with(activity).getString(IS_MUTE, "")
        //switchPush.isChecked = !isMute.equals("true")
    }

    private fun getProfileDetails() {
        if (CheckNetworkConnection.isOnline(context)) {
            val map = HashMap<String, String>()
            /*   map[FIRST_NAME] = userDetail?.firstName!!
               map[LAST_NAME] = userDetail?.lastName!!
               map[EMAIL] = userDetail?.emailId ?: ""*/
            presenter.editProfileApiCall(getAuthAccessToken(activity), map)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_CODE_PROFILE && resultCode == Activity.RESULT_OK) {
            setUserDetails()
            val pager = activity?.findViewById<ViewPager>(R.id.viewPager)
            pager?.offscreenPageLimit = 1
        }
    }

    private fun setUserDetails() {
        userDetail = SharedPrefs.with(activity).getObject(USER_DATA, SignupModel::class.java)
/*
        Glide.with(this).load(userDetail?.profilePicURL?.original).apply(RequestOptions().circleCrop())
                .apply(RequestOptions().placeholder(R.drawable.com_facebook_profile_picture_blank_square)).into(ivProfilePic)
*/
        if (userType.equals(UserType.USER)) {
            imageUrl = userDetail?.profilePicURL?.original.toString()
            Glide.with(this).load(imageUrl).apply(RequestOptions().circleCrop())
                .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder))
                .into(ivUserProfile)
        } else if (userType.equals(UserType.DRIVER)) {
            imageUrl = userDetail?.profilePicURL?.original.toString()

            Glide.with(this).load(imageUrl).apply(RequestOptions().circleCrop())
                .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder))
                .into(ivUserProfile)
        } else {
            imageUrl = userDetail?.profilePicURL?.original.toString()

            Glide.with(this).load(imageUrl).apply(RequestOptions().circleCrop())
                .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder))
                .into(ivUserProfile)
        }
        tvUsername.text = userDetail?.fullName
//        if (SharedPrefs.with(activity).getString(PREF_LANG, "en").equals("en")) {
//            tvChange.text = getString(R.string.spanish)
//        } else {
//            tvChange.text = getString(R.string.english)
//        }


        /* val activity=activity as HomeActivity
         activity.setViewPagerAdapter()*/
    }

    private fun setListeners() {
        tvEditProfile.setOnClickListener(this)
//        paymentMethod.setOnClickListener(this)
//        tvInviteFriends.setOnClickListener(this)
//        tvFaqs.setOnClickListener(this)
//        tvAbout.setOnClickListener(this)
//        tv_contact.setOnClickListener(this)
//        tvLogOut.setOnClickListener(this)
//        tvTutorialTraveler.setOnClickListener(this)
//        tvChangeLanguage.setOnClickListener(this)
        btnSwitch.setOnClickListener(this)
//        tvAccount.setOnClickListener(this)
//        switchPush.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                if (CheckNetworkConnection.isOnline(activity)) {
//                    presenter.blockPush(getAuthAccessToken(context), "UNBLOCKED")
//                }
//            } else {
//                presenter.blockPush(getAuthAccessToken(context), "BLOCKED")
//            }
//        }

        ivUserProfile.setOnClickListener {
            val intent = Intent(context, ImageViewerActivity::class.java)
            intent.putExtra(PROFILE_PIC_URL, imageUrl)
            startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvEditProfile -> {
                val options = activity?.let {
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        it,
                        ivUserProfile,
                        ViewCompat.getTransitionName(ivUserProfile) ?: ""
                    )
                }
                startActivityForResult(
                    Intent(activity, ProfileActivity::class.java),
                    com.capcorp.ui.user.homescreen.account.RQ_CODE_PROFILE,
                    options?.toBundle()
                )
            }
            R.id.btnSwitch -> {
                if (CheckNetworkConnection.isOnline(context)) {
                    if (userType.equals(UserType.USER)) {
                        presenter.switchUser(getAuthAccessToken(context), UserType.DRIVER)
                    } else {
                        presenter.switchUser(getAuthAccessToken(context), UserType.USER)
                    }
                }

            }
        }

    }

    private fun setLayoutChanges() {
        activity?.recreate()
    }


    override fun languageSuccess() {

    }

    override fun switchUserSuccess(signupModel: SignupModel) {
        //dialogIndeterminate.dismiss()
        SharedPrefs.with(activity).save(USER_TYPE, signupModel.type)
        SharedPrefs.with(activity).save(USER_DATA, signupModel)
        userType = SharedPrefs.with(activity).getString(USER_TYPE, "")

        if (userType.equals(UserType.USER)) {
            btnSwitch.text = getString(R.string.switch_to_driver)
            val intent = Intent(activity, com.capcorp.ui.user.homescreen.HomeActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        } else if (userType.equals(UserType.DRIVER)) {
            btnSwitch.text = getString(R.string.switch_to_shopper)
            val intent = Intent(activity, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        } else {
            btnSwitch.text = getString(R.string.switch_to_shopper)
            val intent = Intent(activity, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        }
    }

    override fun logoutSuccess() {
        //dialogIndeterminate.dismiss()
        SharedPrefs.with(activity).remove(ACCESS_TOKEN)
        activity?.finishAffinity()
        startActivity(Intent(activity, SplashActivity::class.java))
    }

    override fun onEditProfileApiSuccess(data: SignupModel?) {
        mIsVerified = data?.isApproved == 1

        SharedPrefs.with(activity).save(USER_DATA, data)
        if (mIsVerified) {
            ivVerified.setImageResource(R.drawable.verified)
        } else {
            ivVerified.setImageResource(R.drawable.un_verified)
        }
        ivVerified.setOnClickListener {
            if(mIsVerified) {
                ViewTooltip.on(activity, ivVerified)
                    .autoHide(true, 3000)
                    .corner(30)
                    .text(getString(R.string.identity_verification_completed))
                    .position(ViewTooltip.Position.BOTTOM)
                    .show()
            }else{
                ViewTooltip.on(activity, ivVerified)
                    .autoHide(true, 3000)
                    .corner(30)
                    .text(getString(R.string.identity_verification_pending))
                    .position(ViewTooltip.Position.BOTTOM)
                    .show()
            }
        }

        if (ratingBarUser != null) {
            ratingBarUser.rating = data?.totalRating!!
        }

        if (userType.equals(UserType.USER)) {
            if (ratingBarUser != null) {
                ratingBarUser.rating = data?.totalRating!!
            }
        } else if (userType.equals(UserType.DRIVER)) {
            if (ratingBarUser != null) {
                ratingBarUser.rating = data?.driverTotalRating!!
            }
        }
    }

    private fun showLogoutDialog() {
        AlertDialogUtil.getInstance().createOkCancelDialog(activity, R.string.log_out,
            R.string.logout_dialog_message, R.string.yes,
            R.string.no, true, object : AlertDialogUtil.OnOkCancelDialogListener {
                override fun onOkButtonClicked() {
                    presenter.logout(getAuthAccessToken(activity))
                }

                override fun onCancelButtonClicked() {

                }

            }).show()
    }

    override fun showLoader(isLoading: Boolean) {
        //dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        //dialogIndeterminate.dismiss()
        activity?.tvEditProfile?.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        //dialogIndeterminate.dismiss()
        if (code == 401) {
            val dialog = Dialog(requireActivity(),R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = activity?.getString(R.string.error)
            tvDescription.text = activity?.getString(R.string.sorry_account_have_been_logged)
            dialogButton.text = activity?.getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                SharedPrefs.with(activity).remove(ACCESS_TOKEN)
                activity?.finishAffinity()
                startActivity(Intent(activity, SplashActivity::class.java))
            }
            dialog.show()
        } else {
            activity?.tvEditProfile?.showSnack(errorBody!!)
        }
    }

    override fun validationsFailure(type: String?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun switchChange(isNotificationOn: Boolean) {
        if (isNotificationOn) {
            if (CheckNetworkConnection.isOnline(activity)) {
                presenter.blockPush(getAuthAccessToken(context), "UNBLOCKED")
            }
        } else {
            presenter.blockPush(getAuthAccessToken(context), "BLOCKED")
        }
    }

    override fun changeLanguage(language: String) {
        setLayoutChanges()
        if (CheckNetworkConnection.isOnline(context)) {
            if (language.equals("es")) {
                presenter.languageChangeApi(getAuthAccessToken(context), 1)
            } else {
                presenter.languageChangeApi(getAuthAccessToken(context), 0)

            }
        }
    }

    override fun doLogout() {
        presenter.logout(getAuthAccessToken(activity))
    }

}
