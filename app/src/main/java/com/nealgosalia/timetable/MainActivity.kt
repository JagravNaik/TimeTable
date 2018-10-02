package com.nealgosalia.timetable

import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.annotation.IdRes
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.ncapdevi.fragnav.FragNavController
import com.nealgosalia.timetable.activities.PreferencesActivity
import com.nealgosalia.timetable.fragments.AttendanceFragment
import com.nealgosalia.timetable.fragments.SubjectsFragment
import com.nealgosalia.timetable.fragments.TimetableFragment
import com.roughike.bottombar.BottomBar
import com.roughike.bottombar.OnTabReselectListener
import com.roughike.bottombar.OnTabSelectListener

import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private val TAB_FIRST = FragNavController.TAB1
    private val TAB_SECOND = FragNavController.TAB2
    private val TAB_THIRD = FragNavController.TAB3
    internal var doubleBackToExitPressedOnce = false
    private var fragNavController: FragNavController? = null

    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragments = ArrayList(3)
        fragments.add(TimetableFragment())
        fragments.add(AttendanceFragment())
        fragments.add(SubjectsFragment())
        fragNavController = FragNavController(savedInstanceState, getSupportFragmentManager(), R.id.contentContainer, fragments, TAB_FIRST)
        val bottomBar = findViewById(R.id.bottomBar) as BottomBar
        bottomBar.setDefaultTabPosition(1)
        bottomBar.setOnTabSelectListener(object : OnTabSelectListener() {
            @Override
            fun onTabSelected(@IdRes tabId: Int) {
                when (tabId) {
                    R.id.tab_timetable -> fragNavController!!.switchTab(TAB_FIRST)
                    R.id.tab_attendance -> fragNavController!!.switchTab(TAB_SECOND)
                    R.id.tab_subjects -> fragNavController!!.switchTab(TAB_THIRD)
                }
            }
        })

        bottomBar.setOnTabReselectListener(object : OnTabReselectListener() {
            @Override
            fun onTabReSelected(@IdRes tabId: Int) {
                fragNavController!!.clearStack()
            }
        })
    }

    @Override
    fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @Override
    fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getResources().getString(R.string.press_back), Toast.LENGTH_SHORT).show()
        Handler().postDelayed(object : Runnable() {
            @Override
            fun run() {
                doubleBackToExitPressedOnce = false
            }
        }, 2000)
    }

    @Override
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.settings -> {
                val intent = Intent(this@MainActivity, PreferencesActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
