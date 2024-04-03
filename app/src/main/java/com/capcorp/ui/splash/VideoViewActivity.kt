package com.capcorp.ui.splash

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.capcorp.R
import kotlinx.android.synthetic.main.activity_video_view.*


class VideoViewActivity : AppCompatActivity() {

    lateinit var context: VideoViewActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_view)

        context = this@VideoViewActivity

        video_view.setVideoPath(intent.getStringExtra("video"))
        video_view.start()

        video_view.setOnPreparedListener { progressBar.visibility = View.INVISIBLE }
        video_view.setOnCompletionListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        video_view?.resume()
    }

}
