package com.capcorp.ui.payment.add_card

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.braintreepayments.cardform.OnCardFormSubmitListener
import com.braintreepayments.cardform.utils.CardType
import com.braintreepayments.cardform.view.CardEditText
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.payment.model.CardData
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.utils.*
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.CardParams
import com.stripe.android.model.Token
import com.stripe.android.view.CardInputListener
import kotlinx.android.synthetic.main.activity_card.*
import java.util.regex.Pattern


class CardActivity : BaseActivity(), OnCardFormSubmitListener,
    CardEditText.OnCardTypeChangedListener, AddCardContract.View, CardInputListener {

    private val presenter = AddCardPresenter()
    private lateinit var dialogIndeterminate: DialogIndeterminate
    private var isCardNumberComplete = false
    private var isPostalCodeComplete = false
    private var isCVCComplete = false
    private var isExpiryDateComplete = false
    private var isCardHolderNameComplete = false
    private val ZIP_CODE_PATTERN = Pattern.compile("^[0-9]{5}$")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        cardInputWidget.postalCodeEnabled = true
        cardInputWidget.postalCodeRequired = true

        tvBack.setOnClickListener {
            onBackPressed()
        }

        cardInputWidget.setCardInputListener(this)

        edtCardHolderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isCardHolderNameComplete = !(p0.toString().isEmpty() || p0.toString().length < 3)
                setButtonColor()

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        cardInputWidget.setCardNumberTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length < 12) {
                    btn_done.isEnabled = false
                    btn_done.setBackgroundResource(R.drawable.bg_button_disable)
                }
                setButtonColor()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        cardInputWidget.setExpiryDateTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length < 5) {
                    btn_done.isEnabled = false
                    btn_done.setBackgroundResource(R.drawable.bg_button_disable)
                }
                setButtonColor()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        cardInputWidget.setCvcNumberTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length < 3) {
                    btn_done.isEnabled = false
                    btn_done.setBackgroundResource(R.drawable.bg_button_disable)
                }
                setButtonColor()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        cardInputWidget.setPostalCodeTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length < 4) {
                    btn_done.isEnabled = false
                    btn_done.setBackgroundResource(R.drawable.bg_button_disable)
                    isPostalCodeComplete = false
                }
                setButtonColor()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
//        tbrCarddetail.setNavigationOnClickListener {
//            onBackPressed()
//        }

        // Warning: this is for development purposes only and should never be done outside of this example app.
        // Failure to set FLAG_SECURE exposes your app to screenshots allowing other apps to steal card information.
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        btn_done.setOnClickListener {
            onCardFormSubmit()
        }
    }

    fun setButtonColor() {
        if (isCardNumberComplete && isExpiryDateComplete && isCVCComplete && isPostalCodeComplete && isCardHolderNameComplete) {
            btn_done.isEnabled = true
            btn_done.setBackgroundResource(R.drawable.bg_button)
        } else {
            btn_done.isEnabled = false
            btn_done.setBackgroundResource(R.drawable.bg_button_disable)
        }
    }

    fun isValidInput(): Boolean {
        return isCardNumberComplete && isExpiryDateComplete && isCVCComplete && isPostalCodeComplete && isCardHolderNameComplete
    }

    override fun onCardFormSubmit() {
        if (isValidInput()) {
            showLoader(true)
            //progressDialog?.show()
            Log.e(
                "Card Number",
                cardInputWidget.paymentMethodCreateParams?.card?.toParamMap().toString()
            )
            Log.e(
                "Card Number",
                cardInputWidget.paymentMethodCreateParams.toString()
            )
            val param: Map<String, Any>? =
                cardInputWidget.paymentMethodCreateParams?.card?.toParamMap()
            param?.let {

                val cardNumber = it["number"]
                val cardHolderName = edtCardHolderName.text.toString().trim()
                val cardCvv = it["cvc"]
                val cardExpMonth = it["exp_month"]
                val cardExpYear = it["exp_year"]


                val cardParams = CardParams(
                    cardNumber.toString(),
                    Integer.parseInt(cardExpMonth.toString()),
                    Integer.parseInt(cardExpYear.toString()),
                    cardCvv.toString(),
                    cardHolderName.toString(),
                    cardInputWidget.paymentMethodCreateParams?.billingDetails?.address,
                    "USD"
                )

                Stripe(this, STRIPE_KEY).createCardToken(
                    cardParams,
                    null,
                    null,
                    object : ApiResultCallback<Token> {
                        override fun onSuccess(result: Token) {

                            if (result.card != null) {
                                Log.v("Token!", "Token Created!!" + result.id)
                                addCard(
                                    cardHolderName,
                                    cardExpMonth.toString(),
                                    cardExpYear.toString(),
                                    result.id
                                )
                            } else {
                                Log.v("Token!", "Token Failed!!$result")
                            }
                        }

                        override fun onError(error: Exception) {
                            //progressDialog?.dismiss()
                            Toast.makeText(this@CardActivity, error.message, Toast.LENGTH_SHORT)
                                .show()
                            error.printStackTrace()
                        }
                    })
            }
        } else {
            //progressDialog?.dismiss()
            /* card_form.validate()*/
            Toast.makeText(this, R.string.valid, Toast.LENGTH_SHORT).show()
        }
    }

    fun addCard(cardHolderName: String, cardExpMonth: String, cardExpYear: String, token: String) {
        val hashMap = HashMap<String, String>()
        hashMap["tokenId"] = token
        hashMap["cardHolderName"] = cardHolderName
        hashMap["expMonth"] = cardExpMonth
        hashMap["expYear"] = cardExpYear
        presenter.addCard(getAuthAccessToken(this), hashMap)
    }

    override fun onCardTypeChanged(cardType: CardType?) {

    }

    override fun onCardListSuccess(list: List<CardData>?) {
    }

    override fun onAddCarcSuccess() {
        // progressDialog?.dismiss()
        Toast.makeText(this, getString(R.string.card_added_successfully), Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    override fun onDeleteCardSuccess() {
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        //progressDialog?.dismiss()
        llContainer_card.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        //progressDialog?.dismiss()
        if (code == 401) {
            val dialog = Dialog(this,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.error)
            tvDescription.text = getString(R.string.sorry_account_have_been_logged)
            dialogButton.text = getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                SharedPrefs.with(this).remove(ACCESS_TOKEN)
                finishAffinity()
                startActivity(Intent(this, SplashActivity::class.java))
            }
            dialog.show()
        } else {
            tvBack.showSnack(errorBody ?: getString(R.string.sww_error))
        }
    }

    override fun validationsFailure(type: String?) {
    }

    override fun onCardComplete() {
        isCardNumberComplete = true
    }

    override fun onCvcComplete() {
        isCVCComplete = true
    }

    override fun onExpirationComplete() {
        isExpiryDateComplete = true
    }

    override fun onFocusChange(focusField: CardInputListener.FocusField) {

    }

    override fun onPostalCodeComplete() {
        isPostalCodeComplete = true
    }


}
