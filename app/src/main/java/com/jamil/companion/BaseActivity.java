package com.jamil.companion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.jamil.companion.cache.EntityCache;

public abstract class BaseActivity  extends AppCompatActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();

    protected Typeface mComicLetteringFont;
    protected Typeface mComicTitleFont;

    protected int mBatteryLevel = 0;

    protected MediaPlayer mPageFlipMediaPlayer;
    protected MediaPlayer mCashRegisterMediaPlayer;

    protected BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get Battery percentage
            mBatteryLevel = intent.getIntExtra("level", 0);
            Log.d(TAG, "Battery Level: " + mBatteryLevel + "%");
            //Toast.makeText(BaseActivity.this, "Battery Level: " + level + "%", Toast.LENGTH_SHORT).show();
        }
    };

    protected void setDefaultTitle()
    {
        setTitle("Marvel Pocket Companion");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultTitle();
    }

    /**
     * Checks if the network is currently available for internet access.
     *
     * @return if the network is available or not
     */
    public boolean isNetworkAvailable()
    {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // network is present and available
            isAvailable = true;
        }

        // Show message to user that the network is not available right now
        if (!isAvailable) {
            Toast.makeText(this, "Network currently unavailable.", Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    protected int clearOldResultCacheEntries()
    {
        EntityCache cache = new EntityCache(this);
        return cache.clearOldResponses();
    }

    protected void makeRoundedColoredButton(Button button, String hexColor)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(30);
        shape.setColor(Color.parseColor(hexColor));
        button.setBackground(shape);
        button.setTextColor(Color.WHITE);
        button.setTextSize(20);
    }

    public Typeface getComicLetteringFont()
    {
        if (mComicLetteringFont == null) {
            mComicLetteringFont = Typeface.createFromAsset(getAssets(), "fonts/animeace.ttf");
        }
        return mComicLetteringFont;
    }

    public Typeface getComicTitleFont()
    {
        if (mComicTitleFont == null) {
            mComicTitleFont = Typeface.createFromAsset(getAssets(), "fonts/Bangers.ttf");
        }
        return mComicTitleFont;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBatteryInfoReceiver);
    }

    protected void createMediaPlayer()
    {
        if (mPageFlipMediaPlayer == null) {
            mPageFlipMediaPlayer = MediaPlayer.create(this, R.raw.page_flip);
            mPageFlipMediaPlayer.setVolume(0.4f, 0.4f);
            mCashRegisterMediaPlayer = MediaPlayer.create(this, R.raw.cash_register);
            mCashRegisterMediaPlayer.setVolume(0.5f, 0.5f);
        }
    }

    protected void destroyMediaPlayer()
    {
        // Release Media Players if they have been allocated
        if (mPageFlipMediaPlayer != null) {
            mPageFlipMediaPlayer.release();
            mPageFlipMediaPlayer = null;
        }

        if (mCashRegisterMediaPlayer != null) {
            mCashRegisterMediaPlayer.release();
            mCashRegisterMediaPlayer = null;
        }
    }

    protected void playPageFlip()
    {
        if (!mPageFlipMediaPlayer.isPlaying()) {
            mPageFlipMediaPlayer.start();
        }
    }

    protected void playCashRegister()
    {
        if (!mCashRegisterMediaPlayer.isPlaying()) {
            mCashRegisterMediaPlayer.start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        createMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        destroyMediaPlayer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playPageFlip();
        overridePendingTransition(R.anim.back, R.anim.back_exit);
    }

    public void pageTransition(Intent intent)
    {
        playPageFlip();
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }
}
