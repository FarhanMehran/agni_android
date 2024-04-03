package com.capcorp.ui.signup.enter_phoneno

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capcorp.R
import com.capcorp.ui.signup.phone_verification.EnterPhoneNoContract
import com.capcorp.ui.signup.phone_verification.EnterPhoneNoPresenter
import com.capcorp.ui.signup.phone_verification.PhoneVerificationFragment
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_enter_phone_number.*


class EnterPhoneNumberFragment : Fragment(), EnterPhoneNoContract.View {
    override fun sendOtpSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun apiEmailSuccess(isPhoneNoExists: Boolean, data: SignupModel) {

    }

    private val TAG = javaClass.simpleName

    private val presenter = EnterPhoneNoPresenter()

    private lateinit var dialogIndeterminate: DialogIndeterminate

    private var isPhoneNoValid: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_phone_number, container, false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(activity)
        countryPicker.registerCarrierNumberEditText(etPhone)
        setListeners()
    }

    fun getBundle() {
        var bundle = arguments
        if (bundle != null) {
            var data = bundle.getSerializable("mapData")
            if (data != null) {
                textView5.text = getString(R.string.number_to_be_register)
            }
        }
    }


    private fun setListeners() {
        countryPicker.setPhoneNumberValidityChangeListener { isPhoneNoValid = it }
    }

    override fun onResume() {
        super.onResume()
        activity.fab.setOnClickListener(fabClickListener)
        getBundle()
    }

    private val fabClickListener = View.OnClickListener {
        when {
            etPhone.text.toString().trim()
                .isEmpty() -> activity.signupRoot.showSnack(R.string.please_enter_phonenumber)
            isPhoneNoValid ->
                presenter.phoneVerificationApiCall(
                    countryPicker.fullNumberWithPlus + "" + etPhone.text.toString().replace(
                        Regex("[^0-9]"),
                        ""
                    ), "USER", "Signup"
                )
            else -> activity.signupRoot.showSnack(R.string.enter_a_valid_phone)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

    override fun apiSuccess(isPhoneNoExists: Boolean, data: SignupModel) {
        if (!isPhoneNoExists) {
            val fragment = PhoneVerificationFragment()
            var bundle: Bundle? = null
            if (arguments != null) {
                bundle = arguments
            } else {
                bundle = Bundle()
            }

            bundle!!.putString(COUNTRY_CODE, countryPicker.selectedCountryCodeWithPlus)
            bundle.putString(
                PHONE_NUMBER, etPhone.text.toString().replace(
                    Regex("[^0-9]"),
                    ""
                )
            )
            fragment.arguments = bundle
            replaceFragment(fragmentManager, fragment, TAG)
        } else {
            activity.signupRoot.showSnack(R.string.phone_number_already_exists)
        }
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        activity.signupRoot.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        activity.signupRoot.showSWWerror()
    }

    override fun validationsFailure(type: String?) {
        when (type) {
            Validations.FIELD_EMPTY -> {
                activity.signupRoot?.showSnack(R.string.please_enter_phonenumber)
            }
            Validations.FIELD_INVALID -> {
                activity.signupRoot?.showSnack(R.string.enter_a_valid_phone)
            }
        }
    }


}
