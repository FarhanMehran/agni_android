package com.capcorp.ui.user_signup.upload_documents

import android.os.Bundle
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.webservice.models.VerifyDocumentRequestModel

class UploadDocumentsActivity : BaseActivity() {

    companion object {
        var screenType: String = ""
        var dropDownCountry: String = ""
    }

    val verifyDocumentRequestModel = VerifyDocumentRequestModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_documents)
        screenType = intent.getStringExtra("from_screen_type").toString()
        supportFragmentManager.beginTransaction()
            .add(R.id.container, UploadDocumentFragment(), "UploadDocumentFragment").commit()
    }
}
