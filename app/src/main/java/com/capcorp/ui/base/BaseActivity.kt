package com.capcorp.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capcorp.R
import com.capcorp.utils.PREF_LANG
import com.capcorp.utils.SharedPrefs
import com.codebrew.encober.utils.LocaleHelper


open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(
            LocaleHelper.onAttach(
                base,
                SharedPrefs.with(this).getString(PREF_LANG, "en")
            )
        )
    }

    open fun start(activity: Class<out Activity?>?) {
        startActivity(Intent(this, activity))
    }


    open fun startWithClearStack(activity: Class<out Activity?>?) {
        startActivity(
            Intent(
                this,
                activity
            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        overridePendingTransition(0, 0)
        finish()
    }

    open fun startWithClearTopStack(activity: Class<out Activity?>?) {
        startActivity(Intent(this, activity).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }
}
