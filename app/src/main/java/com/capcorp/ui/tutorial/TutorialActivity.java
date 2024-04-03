package com.capcorp.ui.tutorial;

import static com.capcorp.utils.ConstantsKt.PREF_LANG;
import static com.capcorp.utils.ConstantsKt.USER_TYPE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.capcorp.R;
import com.capcorp.utils.SharedPrefs;
import com.capcorp.utils.UserType;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;

public class TutorialActivity extends AppCompatActivity {
    private SimpleExoPlayer player;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private PlayerView playerView;
    private Button skipBtn;
    private String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        playerView = findViewById(R.id.ep_video_view);
        skipBtn = findViewById(R.id.tutorial_skip_btn);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releasePlayer();
                finish();
            }
        });
        initializePlayer();
    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        Uri mediaUri;
        SharedPreferences preferences = this.getSharedPreferences("CapcorpSharedPrefs", Context.MODE_PRIVATE);
        String lang = preferences.getString(PREF_LANG, "en");
        if (lang.equalsIgnoreCase("en")) {
            userType = SharedPrefs.with(this).getString(USER_TYPE, "");
            if (userType == UserType.USER) {
                //tvSwitchToShopper.text = getString(R.string.switch_to_driver)
                mediaUri = RawResourceDataSource.buildRawResourceUri(R.raw.traveler_tutorial_en);
            } else if (userType == UserType.DRIVER) {
                mediaUri = RawResourceDataSource.buildRawResourceUri(R.raw.shopper_tutorial_en);
            } else {
                mediaUri = RawResourceDataSource.buildRawResourceUri(R.raw.shopper_tutorial_en);
            }
        } else {
            userType = SharedPrefs.with(this).getString(USER_TYPE, "");
            if (userType == UserType.USER) {
                //tvSwitchToShopper.text = getString(R.string.switch_to_driver)
                mediaUri = RawResourceDataSource.buildRawResourceUri(R.raw.traveler_tutorial_es);
            } else if (userType == UserType.DRIVER) {
                mediaUri = RawResourceDataSource.buildRawResourceUri(R.raw.shopper_tutorial_es);
            } else {
                mediaUri = RawResourceDataSource.buildRawResourceUri(R.raw.shopper_tutorial_es);
            }
        }
        MediaItem mediaItem = MediaItem.fromUri(mediaUri);
        player.setMediaItem(mediaItem);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    releasePlayer();
                    finish();
                }
            }
        });
        player.prepare();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }
}
