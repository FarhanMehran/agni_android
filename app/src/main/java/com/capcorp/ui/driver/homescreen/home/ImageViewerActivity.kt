package com.capcorp.ui.driver.homescreen.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capcorp.R
import com.capcorp.utils.IS_FILE_IMAGE
import com.capcorp.utils.PROFILE_PIC_URL
import kotlinx.android.synthetic.main.activity_image_viewer.*
import java.io.File

class ImageViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        if (intent.hasExtra(PROFILE_PIC_URL) && !intent.hasExtra(IS_FILE_IMAGE)) {
            Glide.with(this).load(intent.getStringExtra(PROFILE_PIC_URL)).into(imageViewPhoto)
        } else {
            val file = File(intent.getStringExtra(PROFILE_PIC_URL))
            Glide.with(this).load(file).into(imageViewPhoto)

        }

        close.setOnClickListener {
            finish()
        }
    }
}
