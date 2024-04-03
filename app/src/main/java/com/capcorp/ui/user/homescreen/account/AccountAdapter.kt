package com.capcorp.ui.user.homescreen.account

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capcorp.BuildConfig
import com.capcorp.R
import com.capcorp.ui.payment.card_list.CardListActivity
import com.capcorp.ui.settings.profile.support.SupportActivity
import com.capcorp.ui.tutorial.TutorialActivity
import com.capcorp.ui.user.homescreen.account.activity.PayoutsActivity
import com.capcorp.ui.user.homescreen.account.activity.RecentsPayoutActivity
import com.capcorp.utils.AlertDialogUtil
import com.capcorp.utils.IS_MUTE
import com.capcorp.utils.PREF_LANG
import com.capcorp.utils.SharedPrefs
import com.capcorp.webservice.models.Account
import kotlinx.android.synthetic.main.item_setting.view.*

class AccountAdapter(
    private val mContext: Context,
    private val accountArray: ArrayList<Account>,
    private val listener: AccountInterface
) :
    RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_setting, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AccountAdapter.ViewHolder, position: Int) {
        holder.bind(accountArray[position])
    }

    override fun getItemCount(): Int {
        return accountArray.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(account: Account) = with(itemView) {
            tvTitle.text = account.title
            ivAccount.setImageResource(account.image)
            if (account.isLanguage) {
                tvLanguage.visibility = View.VISIBLE
                if (SharedPrefs.with(context).getString(PREF_LANG, "en").equals("en")) {
                    tvLanguage.text = context.getString(R.string.spanish)
                } else {
                    tvLanguage.text = context.getString(R.string.english)
                }
            } else {
                tvLanguage.visibility = View.GONE
            }
            if (account.isNotification) {
                var isMute = ""
                isMute = SharedPrefs.with(context).getString(IS_MUTE, "")
                switchNotification.isChecked = isMute != "true"
                switchNotification.visibility = View.VISIBLE
            } else {
                switchNotification.visibility = View.GONE
            }

            switchNotification.setOnCheckedChangeListener { _, isChecked ->
                listener.switchChange(isChecked)
            }

            tvLanguage.setOnClickListener {
                AlertDialogUtil.getInstance()
                    .createOkCancelDialog(context, R.string.change_language,
                        R.string.change_language_dialog_message, R.string.yes,
                        R.string.no, true, object : AlertDialogUtil.OnOkCancelDialogListener {
                            override fun onOkButtonClicked() {
                                if (SharedPrefs.with(context).getString(PREF_LANG, "en") == "en") {
                                    SharedPrefs.with(context).save(PREF_LANG, "es")
                                    tvLanguage.text = context.getString(R.string.english)
                                    listener.changeLanguage("es")

                                } else {
                                    SharedPrefs.with(context).save(PREF_LANG, "en")
                                    tvLanguage.text = context.getString(R.string.spanish)
                                    listener.changeLanguage("en")

                                }
                            }

                            override fun onCancelButtonClicked() {

                            }

                        }).show()
            }

            itemView.setOnClickListener {
                when (adapterPosition) {
                    0 -> {
                        context.startActivity(
                            Intent(
                                context,
                                CardListActivity::class.java
                            ).putExtra("from", "account")
                        )
                    }
                    1 -> {
                     //   context.startActivity(Intent(context, PayoutsActivity::class.java))
                        context.startActivity(Intent(context, RecentsPayoutActivity::class.java))
                    }
                    3 -> {
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Here2Dare")
                            var shareMessage = context.getString(R.string.let_me_recommend)
                            shareMessage =
                                shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            context.startActivity(Intent.createChooser(shareIntent, "choose one"))
                        } catch (e: Exception) {
                            e.toString()
                        }
                    }
                    4 -> {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://h2d.app/faqs"))
                        context.startActivity(browserIntent)
                    }
                    5 -> {
                        val language = SharedPrefs.with(context).getString(PREF_LANG, "en") ?: "en"
                        val videoUrl = if (language.equals("en")) {
                            "https://h2d-dev.s3-us-west-2.amazonaws.com/h2d-dev.s3-us-west-2.amazonaws.comVideo_654158746431.mp4"
                        } else {
                            "https://h2d-dev.s3-us-west-2.amazonaws.com/h2d-dev.s3-us-west-2.amazonaws.comVideo_1029712909073.mp4"
                        }
                        val playVideo = Intent(Intent.ACTION_VIEW)
                        playVideo.setDataAndType(Uri.parse(videoUrl), "video/mp4")
                        context.startActivity(playVideo)
                    }
                    6 -> {
                        context.startActivity(Intent(context, SupportActivity::class.java))
                    }
                    8 -> {
                        val intent = Intent(context, TutorialActivity::class.java)
                        context.startActivity(intent)
                    }
                    9 -> {
                        showLogoutDialog(context)
                    }
                }
            }
        }
    }

    private fun showLogoutDialog(context: Context) {

        AlertDialogUtil.getInstance().createOkCancelDialog(context, R.string.log_out,
            R.string.logout_dialog_message, R.string.yes,
            R.string.no, true, object : AlertDialogUtil.OnOkCancelDialogListener {
                override fun onOkButtonClicked() {
                    listener.doLogout()
                }

                override fun onCancelButtonClicked() {
                }

            }).show()
    }

}