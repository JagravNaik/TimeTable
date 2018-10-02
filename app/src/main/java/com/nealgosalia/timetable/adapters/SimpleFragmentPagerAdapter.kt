package com.nealgosalia.timetable.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.nealgosalia.timetable.R
import com.nealgosalia.timetable.fragments.FridayFragment
import com.nealgosalia.timetable.fragments.MondayFragment
import com.nealgosalia.timetable.fragments.SaturdayFragment
import com.nealgosalia.timetable.fragments.SundayFragment
import com.nealgosalia.timetable.fragments.ThursdayFragment
import com.nealgosalia.timetable.fragments.TuesdayFragment
import com.nealgosalia.timetable.fragments.WednesdayFragment

class SimpleFragmentPagerAdapter(fm: FragmentManager, private val mContext: Context) : FragmentPagerAdapter(fm) {

    val count: Int
        @Override
        get() = 7

    @Override
    fun getItem(position: Int): Fragment {
        return if (position == 0) {
            MondayFragment()
        } else if (position == 1) {
            TuesdayFragment()
        } else if (position == 2) {
            WednesdayFragment()
        } else if (position == 3) {
            ThursdayFragment()
        } else if (position == 4) {
            FridayFragment()
        } else if (position == 5) {
            SaturdayFragment()
        } else {
            SundayFragment()
        }
    }

    @Override
    fun getItemPosition(`object`: Object): Int {
        return POSITION_NONE
    }

    @Override
    fun getPageTitle(position: Int): CharSequence {
        return if (position == 0) {
            mContext.getString(R.string.monday)
        } else if (position == 1) {
            mContext.getString(R.string.tuesday)
        } else if (position == 2) {
            mContext.getString(R.string.wednesday)
        } else if (position == 3) {
            mContext.getString(R.string.thursday)
        } else if (position == 4) {
            mContext.getString(R.string.friday)
        } else if (position == 5) {
            mContext.getString(R.string.saturday)
        } else {
            mContext.getString(R.string.sunday)
        }
    }
}