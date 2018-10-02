package com.nealgosalia.timetable.activities

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.nealgosalia.timetable.R
import com.nealgosalia.timetable.fragments.MyPreferenceFragment

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class PreferencesActivity : AppCompatActivity() {

    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        getFragmentManager().beginTransaction().replace(android.R.id.content, MyPreferenceFragment()).commit()
    }

    @Override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
        }
    }

    fun importDatabase(ePath: String): Boolean {
        try {
            val sd = Environment.getExternalStorageDirectory()
            if (sd.canWrite()) {
                val currentDBPath = sd + "/Timetable/" + ePath
                val internalDBPath = "data/data/com.nealgosalia.timetable/databases/"
                val backupDBPath = internalDBPath + ePath
                val internalDB = File(internalDBPath)
                internalDB.mkdirs()
                val currentDB = File(currentDBPath)
                val backupDB = File(backupDBPath)
                if (currentDB.exists()) {
                    val src = FileInputStream(currentDB).getChannel()
                    val dst = FileOutputStream(backupDB).getChannel()
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun exportDatabase(mPath: String): Boolean {
        try {
            val outDir = File(Environment.getExternalStorageDirectory() + File.separator + "Timetable")
            if (!outDir.exists()) {
                Log.d(TAG, "Creating directory")
                outDir.mkdir()
            } else {
                Log.d(TAG, "Directory present")
            }
            val currentDBPath = Environment.getDataDirectory() + "/data/com.nealgosalia.timetable/databases/" + mPath
            val backupDBPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Timetable/" + mPath
            val currentDB = File(currentDBPath)
            val backupDB = File(backupDBPath)
            if (currentDB.exists()) {
                val src = FileInputStream(currentDB).getChannel()
                val dst = FileOutputStream(backupDB).getChannel()
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun isStoragePermissionGranted(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) === PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission granted")
                return true
            } else {
                Log.v(TAG, "Permission denied")
                ActivityCompat.requestPermissions(activity, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission granted")
            return true
        }
    }

    companion object {

        private val TAG = "PreferencesActivity"
    }
}