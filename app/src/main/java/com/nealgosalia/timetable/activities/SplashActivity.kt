package com.nealgosalia.timetable.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window

import com.nealgosalia.timetable.MainActivity
import com.nealgosalia.timetable.R

class SplashActivity : Activity() {
    private var runnable: Runnable? = null
    private var handler: Handler? = null

    protected fun onCreate(savedInstanceState: Bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = getWindow()
        window.getDecorView().setOverScrollMode(View.OVER_SCROLL_NEVER)
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        initialize()
    }

    private fun initialize() {
        handler = Handler()
        runnable = object : Runnable() {
            @Override
            fun run() {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
        handler!!.postDelayed(runnable, 1000)
    }

    @Override
    fun onBackPressed() {
        finish()
        handler!!.removeCallbacks(runnable)
    }

}