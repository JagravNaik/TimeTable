package com.nealgosalia.timetable.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import java.util.ArrayList

/**
 * Created by men_in_black007 on 11/12/16.
 */

class SubjectDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val subjectDetail: ArrayList<SubjectDetails>
        get() {
            val allSubjects = ArrayList<SubjectDetails>()
            val sql = "select distinct * from $TABLE_DETAIL order by $SUBJECT"
            val db = this.getReadableDatabase()
            val c = db.rawQuery(sql, null)
            try {
                while (c.moveToNext()) {
                    val subjectDetails = SubjectDetails()
                    subjectDetails.setSubject(c.getString(c.getColumnIndex(SUBJECT)))
                    subjectDetails.setAttendedLectures(c.getInt(c.getColumnIndex(ATT_LECTURES)))
                    subjectDetails.setTotalLectures(c.getInt(c.getColumnIndex(TOT_LECTURES)))
                    allSubjects.add(subjectDetails)
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                c.close()
            }
            db.close()
            return allSubjects
        }

    @Override
    fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    @Override
    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DETAIL")
        onCreate(db)
    }

    fun addSubject(sd: SubjectDetails) {
        val db = this.getWritableDatabase()
        val values = ContentValues()
        values.put(SUBJECT, sd.getSubject())
        values.put(ATT_LECTURES, sd.getAttendedLectures())
        values.put(TOT_LECTURES, sd.getTotalLectures())
        db.insert(TABLE_DETAIL, null, values)
        db.close()
    }

    fun removeSubject(sd: SubjectDetails) {
        val db = this.getWritableDatabase()
        val selection = SubjectDatabase.SUBJECT + " LIKE ?"
        val selectionArgs = arrayOf<String>(sd.getSubject())
        db.delete(SubjectDatabase.TABLE_DETAIL, selection, selectionArgs)
    }

    fun updateSubject(sd: SubjectDetails) {
        val db = this.getWritableDatabase()
        val values = ContentValues()
        Log.d("SubjectDatabase", sd.getAttendedLectures() + " " + sd.getTotalLectures())
        values.put(ATT_LECTURES, sd.getAttendedLectures())
        values.put(TOT_LECTURES, sd.getTotalLectures())
        val args = arrayOf<String>(sd.getSubject())
        db.update(TABLE_DETAIL, values, "$SUBJECT LIKE ?", args)
    }

    companion object {

        private val TAG = "SubjectDatabase"
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "subject.db"
        private val TABLE_DETAIL = "subject"
        private val SUBJECT = "subjectName"
        private val ATT_LECTURES = "att_lectures"
        private val TOT_LECTURES = "tot_lectures"
        private val CREATE_TABLE = "create table $TABLE_DETAIL($SUBJECT varchar, $ATT_LECTURES int, $TOT_LECTURES int);"
    }
}