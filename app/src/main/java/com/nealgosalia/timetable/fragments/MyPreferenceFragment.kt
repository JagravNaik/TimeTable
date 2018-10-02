package com.nealgosalia.timetable.fragments

import android.app.AlarmManager
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast

import com.nealgosalia.timetable.MainActivity
import com.nealgosalia.timetable.R
import com.nealgosalia.timetable.activities.PreferencesActivity
import com.nealgosalia.timetable.utils.Alarms

import java.io.File
import java.util.ArrayList

import android.content.Context.ALARM_SERVICE

/**
 * Created by men_in_black007 on 15/12/16.
 */

class MyPreferenceFragment : PreferenceFragment() {
    private var mActivity: PreferencesActivity? = null

    @Override
    fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)
        mActivity = getActivity() as PreferencesActivity
        val notificationTime = findPreference("notificationTime") as ListPreference
        notificationTime.setOnPreferenceChangeListener(object : Preference.OnPreferenceChangeListener() {
            @Override
            fun onPreferenceChange(preference: Preference, o: Object): Boolean {
                val prefs = PreferenceManager.getDefaultSharedPreferences(getActivity())
                val editor = prefs.edit()
                editor.putString("NOTIFICATION_TIME", o as String)
                editor.apply()
                if (alarmsList.size() !== 0) {
                    for (alarm in alarmsList) {
                        val alarmManager = alarm.getContext().getSystemService(ALARM_SERVICE) as AlarmManager
                        alarmManager.cancel(alarm.getPendingIntent())
                    }
                }
                if (!o.equals("-1")) {
                    val i = Intent()
                    i.setAction("com.nealgosalia.timetable.NOTIFY")
                    getActivity().sendBroadcast(i)
                    Log.d(TAG, "Broadcasted")
                }
                return true
            }
        })
        val prefs = PreferenceManager.getDefaultSharedPreferences(getActivity())
        val editor = prefs.edit()
        editor.putString("NOTIFICATION_TIME", notificationTime.getValue())
        editor.apply()
        val backup = findPreference("backup")
        val restore = findPreference("restore")
        val reset = findPreference("reset")
        backup.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener() {
            @Override
            fun onPreferenceClick(preference: Preference): Boolean {
                if (mActivity!!.isStoragePermissionGranted(getActivity())) {
                    val alertDialog = AlertDialog.Builder(getActivity())
                    alertDialog.setTitle(getResources().getString(R.string.backup))
                    alertDialog.setMessage(getResources().getString(R.string.backup_question))
                    alertDialog.setPositiveButton(getResources().getString(R.string.yes), object : DialogInterface.OnClickListener() {
                        @Override
                        fun onClick(dialog: DialogInterface, which: Int) {
                            val backupDBPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Timetable/"
                            val subjectDB = File(backupDBPath + "subject.db")
                            val lectureDB = File(backupDBPath + "lecture.db")
                            if (!(subjectDB.exists() || lectureDB.exists())) {
                                if (mActivity!!.exportDatabase("subject.db") && mActivity!!.exportDatabase("lecture.db")) {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.backup_successful), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.please_create_timetable), Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val overwriteDialog = AlertDialog.Builder(getActivity())
                                        .setTitle(getResources().getString(R.string.warning))
                                        .setMessage(getResources().getString(R.string.overwrite_backup))
                                        .setPositiveButton(getResources().getString(R.string.yes), object : DialogInterface.OnClickListener() {
                                            @Override
                                            fun onClick(dialog: DialogInterface, which: Int) {
                                                subjectDB.delete()
                                                lectureDB.delete()
                                                if (mActivity!!.exportDatabase("subject.db") && mActivity!!.exportDatabase("lecture.db")) {
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.backup_successful), Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.please_create_timetable), Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.no), null)
                                        .create()
                                overwriteDialog.show()
                            }
                        }
                    })
                    alertDialog.setNegativeButton(getResources().getString(R.string.no), null)
                    alertDialog.show()
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_permission_granted), Toast.LENGTH_SHORT).show()
                }
                return false
            }
        })
        restore.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener() {
            @Override
            fun onPreferenceClick(preference: Preference): Boolean {
                if (mActivity!!.isStoragePermissionGranted(getActivity())) {
                    val alertDialog = AlertDialog.Builder(getActivity())
                    alertDialog.setTitle(getResources().getString(R.string.restore))
                    alertDialog.setMessage(getResources().getString(R.string.restore_question))
                    alertDialog.setPositiveButton(getResources().getString(R.string.yes), object : DialogInterface.OnClickListener() {
                        @Override
                        fun onClick(dialog: DialogInterface, which: Int) {
                            val backupDBPath = "data/data/com.nealgosalia.timetable/databases/"
                            val subjectDB = File(backupDBPath + "subject.db")
                            val lectureDB = File(backupDBPath + "lecture.db")
                            if (!(subjectDB.exists() || lectureDB.exists())) {
                                if (mActivity!!.importDatabase("subject.db") && mActivity!!.importDatabase("lecture.db")) {
                                    restartApplication()
                                    Toast.makeText(getActivity(), getResources().getString(R.string.restore_successful), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.backup_not_found), Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val overwriteDialog = AlertDialog.Builder(getActivity())
                                        .setTitle(getResources().getString(R.string.warning))
                                        .setMessage(getResources().getString(R.string.overwrite_timetable))
                                        .setPositiveButton(getResources().getString(R.string.yes), object : DialogInterface.OnClickListener() {
                                            @Override
                                            fun onClick(dialog: DialogInterface, which: Int) {
                                                subjectDB.delete()
                                                lectureDB.delete()
                                                if (mActivity!!.importDatabase("subject.db") && mActivity!!.importDatabase("lecture.db")) {
                                                    restartApplication()
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.restore_successful), Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.backup_not_found), Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.no), null)
                                        .create()
                                overwriteDialog.show()
                            }
                        }
                    })
                    alertDialog.setNegativeButton(getResources().getString(R.string.no), null)
                    alertDialog.show()
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_permission_granted), Toast.LENGTH_SHORT).show()
                }
                return false
            }
        })
        reset.setOnPreferenceClickListener(object : Preference.OnPreferenceClickListener() {
            @Override
            fun onPreferenceClick(preference: Preference): Boolean {
                val alertDialog = AlertDialog.Builder(getActivity())
                alertDialog.setTitle(getResources().getString(R.string.reset_the_timetable))
                alertDialog.setMessage(getResources().getString(R.string.reset_question))
                alertDialog.setPositiveButton(getResources().getString(R.string.yes), object : DialogInterface.OnClickListener() {
                    @Override
                    fun onClick(dialog: DialogInterface, which: Int) {
                        val resetDBPath = "data/data/com.nealgosalia.timetable/databases/"
                        val subjectDB = File(resetDBPath + "subject.db")
                        val lectureDB = File(resetDBPath + "lecture.db")
                        if (subjectDB.exists() && lectureDB.exists()) {
                            subjectDB.delete()
                            lectureDB.delete()
                        }
                        restartApplication()
                        Toast.makeText(mActivity, getResources().getString(R.string.reset_successful), Toast.LENGTH_SHORT).show()
                    }
                })
                alertDialog.setNegativeButton(getResources().getString(R.string.no), null)
                alertDialog.show()
                return false
            }
        })
    }

    fun addAlarm(alarm: Alarms) {
        alarmsList.add(alarm)
    }

    private fun restartApplication() {
        val intent = Intent(getActivity(), MainActivity::class.java)
        getActivity().finish()
        startActivity(intent)
    }

    companion object {

        private val TAG = "MyPreferenceFragment"
        private val alarmsList = ArrayList()
    }
}
