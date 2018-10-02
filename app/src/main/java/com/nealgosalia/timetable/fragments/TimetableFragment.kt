package com.nealgosalia.timetable.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast

import com.nealgosalia.timetable.R
import com.nealgosalia.timetable.adapters.SimpleFragmentPagerAdapter
import com.nealgosalia.timetable.database.FragmentDatabase
import com.nealgosalia.timetable.database.FragmentDetails
import com.nealgosalia.timetable.database.SubjectDatabase
import com.nealgosalia.timetable.database.SubjectDetails
import com.nealgosalia.timetable.receivers.MyReceiver
import com.nealgosalia.timetable.utils.Alarms

import java.util.ArrayList
import java.util.Calendar
import java.util.Locale

import android.content.Context.ALARM_SERVICE


class TimetableFragment : Fragment() {
    private var btnNext: Button? = null
    private val subjectsList = ArrayList()
    private var spinnerSubjects: Spinner? = null
    private var tabLayout: TabLayout? = null
    private var textDialog: TextView? = null
    private var startTime: TimePicker? = null
    private var endTime: TimePicker? = null
    private var viewPager: ViewPager? = null
    private var roomN: EditText? = null
    private var count: Int = 0
    private var fragmentDatabase: FragmentDatabase? = null
    private var subjectDatabase: SubjectDatabase? = null

    @Override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle): View {
        val view = inflater.inflate(R.layout.activity_timetable, container, false)
        fragmentDatabase = FragmentDatabase(getActivity())
        subjectDatabase = SubjectDatabase(getActivity())
        viewPager = view.findViewById(R.id.viewpager)
        viewPager!!.setAdapter(SimpleFragmentPagerAdapter(getChildFragmentManager(), getActivity()))
        tabLayout = view.findViewById(R.id.sliding_tabs)
        tabLayout!!.setupWithViewPager(viewPager)
        val c = Calendar.getInstance()
        val dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
        if (Calendar.MONDAY === dayOfWeek) {
            viewPager!!.setCurrentItem(0)
        } else if (Calendar.TUESDAY === dayOfWeek) {
            viewPager!!.setCurrentItem(1)
        } else if (Calendar.WEDNESDAY === dayOfWeek) {
            viewPager!!.setCurrentItem(2)
        } else if (Calendar.THURSDAY === dayOfWeek) {
            viewPager!!.setCurrentItem(3)
        } else if (Calendar.FRIDAY === dayOfWeek) {
            viewPager!!.setCurrentItem(4)
        } else if (Calendar.SATURDAY === dayOfWeek) {
            viewPager!!.setCurrentItem(5)
        } else if (Calendar.SUNDAY === dayOfWeek) {
            viewPager!!.setCurrentItem(6)
        }
        val fab = view.findViewById(R.id.fabTimeTable)
        fab.setOnClickListener(object : View.OnClickListener() {
            @Override
            fun onClick(view: View) {
                count = 0
                showTimeTableDialog()
            }
        })
        return view
    }

    fun showTimeTableDialog() {
        val dialogBuilder = AlertDialog.Builder(getActivity())
        val inflater = getActivity().getLayoutInflater()
        @SuppressLint("InflateParams") val dialogView = inflater.inflate(R.layout.dialog_add_timetable, null)

        startTime = dialogView.findViewById(R.id.startTime)
        endTime = dialogView.findViewById(R.id.endTime)
        val btnCancel = dialogView.findViewById(R.id.btnCancel)
        btnNext = dialogView.findViewById(R.id.btnNext)
        textDialog = dialogView.findViewById(R.id.textDialog)
        spinnerSubjects = dialogView.findViewById(R.id.spinnerSubjects)
        roomN = dialogView.findViewById(R.id.room)
        setSubjectList()
        val spinnerAdapter = object : ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, subjectsList) {
            @Override
            fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            @Override
            fun getDropDownView(position: Int, convertView: View, @NonNull parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubjects!!.setAdapter(spinnerAdapter)

        startTime!!.setVisibility(View.GONE)
        endTime!!.setVisibility(View.GONE)
        roomN!!.setVisibility(View.GONE)
        dialogBuilder.setView(dialogView)
        textDialog!!.setText(getResources().getString(R.string.choose_subject))
        val dialog = dialogBuilder.create()
        btnCancel.setOnClickListener(object : View.OnClickListener() {
            @Override
            fun onClick(view: View) {
                dialog.dismiss()
            }
        })
        btnNext!!.setOnClickListener(object : View.OnClickListener() {
            @Override
            fun onClick(view: View) {
                count++
                if (count == 1) {
                    if (spinnerSubjects!!.getSelectedItemPosition() !== 0) {
                        spinnerSubjects!!.setVisibility(View.GONE)
                        textDialog!!.setText(getResources().getString(R.string.enter_room_number))
                        roomN!!.setVisibility(View.VISIBLE)
                    } else {
                        Toast.makeText(getActivity(), "Please select a subject", Toast.LENGTH_SHORT).show()
                        count--
                    }
                } else if (count == 2) {
                    roomN!!.setVisibility(View.GONE)
                    textDialog!!.setText(getResources().getString(R.string.enter_start_time))
                    startTime!!.setVisibility(View.VISIBLE)
                    btnNext!!.setText(getResources().getString(R.string.next))
                } else if (count == 3) {
                    startTime!!.setVisibility(View.GONE)
                    textDialog!!.setText(getResources().getString(R.string.enter_end_time))
                    endTime!!.setVisibility(View.VISIBLE)
                    btnNext!!.setText(getResources().getString(R.string.done))
                } else if (count == 4) {
                    val startHour: Int
                    val startMinute: Int
                    val endHour: Int
                    val endMinute: Int
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        startHour = startTime!!.getCurrentHour()
                        startMinute = startTime!!.getCurrentMinute()
                        endHour = endTime!!.getCurrentHour()
                        endMinute = endTime!!.getCurrentMinute()

                    } else {
                        startHour = startTime!!.getHour()
                        startMinute = startTime!!.getMinute()
                        endHour = endTime!!.getHour()
                        endMinute = endTime!!.getMinute()
                    }
                    if (endHour > startHour || endHour == startHour && endMinute > startMinute) {
                        val edit = roomN!!.getText().toString().trim()
                        val day = tabLayout!!.getSelectedTabPosition()
                        val subjectName = subjectsList.get(spinnerSubjects!!.getSelectedItemPosition())
                        fragmentDatabase!!.add(FragmentDetails(day, subjectName, startHour, startMinute, endHour, endMinute, edit))
                        dialog.dismiss()
                        viewPager!!.getAdapter().notifyDataSetChanged()
                        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext())
                        val notificationTime = Integer.parseInt(mSharedPreference.getString("NOTIFICATION_TIME", "-1"))
                        Log.d(TAG, Integer.toString(notificationTime))
                        if (notificationTime != -1) {
                            setAlarmForNotification(subjectName, day, notificationTime, startHour, startMinute)
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.end_time_should_be), Toast.LENGTH_LONG).show()
                        count--
                    }
                }

            }
        })
        dialog.show()
    }

    private fun setSubjectList() {
        var breakFlag = 0
        subjectsList.clear()
        subjectsList.add("Select one")
        for (subjectDetails in subjectDatabase!!.getSubjectDetail()) {
            if (breakFlag == 0) {
                if (subjectDetails.getSubject().compareTo("Break") > 0) {
                    subjectsList.add(getResources().getString(R.string.Break))
                    breakFlag++
                }
            }
            subjectsList.add(subjectDetails.getSubject())
        }
        if (breakFlag == 0) {
            subjectsList.add(getResources().getString(R.string.Break))
        }
    }

    private fun setAlarmForNotification(subjectName: String, day: Int, notificationTime: Int, startHour: Int, startMinute: Int) {
        val dayOfWeek = (day + 2) % 7
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.DAY_OF_WEEK) > dayOfWeek) {
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH) + 1)
        }
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, startHour)
        calendar.set(Calendar.MINUTE, startMinute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.setTimeInMillis(calendar.getTimeInMillis() - notificationTime * MINUTE)
        val myIntent = Intent(getActivity(), MyReceiver::class.java)
        val requestCode = System.currentTimeMillis() as Int / 1000
        myIntent.putExtra("SUBJECT_NAME", subjectName)
        myIntent.putExtra("START_TIME", String.format(Locale.US, "%02d:%02d", startHour, startMinute))
        val pendingIntent = PendingIntent.getBroadcast(getActivity(), requestCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarms = Alarms()
        alarms.setContext(getActivity())
        alarms.setPendingIntent(pendingIntent)
        val mpf = MyPreferenceFragment()
        mpf.addAlarm(alarms)
        val alarmManager = getActivity().getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent)
    }

    companion object {

        private val MINUTE = 60000
        private val TAG = "TimetableFragment"
    }
}
