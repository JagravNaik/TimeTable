package com.nealgosalia.timetable.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

import com.nealgosalia.timetable.database.FragmentDatabase
import com.nealgosalia.timetable.utils.Lecture

import java.util.ArrayList
import java.util.Calendar

import android.content.Context.ALARM_SERVICE

/**
 * Created by kira on 18/12/16.
 */

class OnBootReceiver : BroadcastReceiver() {
    private var lecturesList: List<Lecture> = ArrayList()

    @Override
    fun onReceive(context: Context, intent: Intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals("com.nealgosalia.timetable.NOTIFY")) {
            val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
            val notificationTime = Integer.parseInt(mSharedPreference.getString("NOTIFICATION_TIME", "-1"))
            if (notificationTime != -1) {
                Log.d(TAG, intent.getAction())
                val db = FragmentDatabase(context)
                val calendar = Calendar.getInstance()
                lecturesList = ArrayList(db.getLectureList())
                if (lecturesList.size() !== 0) {
                    for (lecture in lecturesList) {
                        calendar.setTimeInMillis(System.currentTimeMillis())
                        val dayOfWeek = (lecture.getDay() + 2) % 7
                        if (calendar.get(Calendar.DAY_OF_WEEK) > dayOfWeek) {
                            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH) + 1)
                        }
                        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(lecture.getStartTime().substring(0, 2)))
                        calendar.set(Calendar.MINUTE, Integer.parseInt(lecture.getStartTime().substring(3, 5)))
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        calendar.setTimeInMillis(calendar.getTimeInMillis() - notificationTime * MINUTE)
                        val i = Intent(context.getApplicationContext(), MyReceiver::class.java)
                        i.putExtra("SUBJECT_NAME", lecture.getSubjectName())
                        i.putExtra("START_TIME", lecture.getStartTime())
                        val randomRequestCode = calendar.getTimeInMillis() as Int
                        val pendingIntent = PendingIntent.getBroadcast(context, randomRequestCode, i, PendingIntent.FLAG_UPDATE_CURRENT)
                        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent)
                        Log.d(TAG, "Notification Time: " + String.valueOf(calendar.getTime()) + " " + String.valueOf(calendar.getTimeInMillis()))
                    }
                }
            }
        }
    }

    companion object {

        private val MINUTE = 60000
        private val TAG = "OnBootReceiver"
    }
}