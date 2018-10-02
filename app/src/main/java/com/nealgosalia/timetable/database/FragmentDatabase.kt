package com.nealgosalia.timetable.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.nealgosalia.timetable.utils.Lecture

import java.util.ArrayList
import java.util.Locale

class FragmentDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val lecturesList = ArrayList()

    val lectureList: List
        get() {
            val sql = "select * from $TABLE_DETAIL"
            val db = this.getReadableDatabase()
            val cursor = db.rawQuery(sql, null)
            try {
                lecturesList.clear()
                while (cursor.moveToNext()) {
                    val lecture = Lecture()
                    lecture.setSubjectName(cursor.getString(1))
                    lecture.setStartTime(String.format(Locale.US, "%02d:%02d", cursor.getInt(2), cursor.getInt(3)))
                    lecture.setEndTime(String.format(Locale.US, "%02d:%02d", cursor.getInt(4), cursor.getInt(5)))
                    lecture.setDay(cursor.getInt(0))
                    lecture.setRoomNo(cursor.getString(6))
                    lecturesList.add(lecture)
                }
            } catch (e: Exception) {
            } finally {
                cursor.close()
            }
            db.close()
            return lecturesList
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

    fun add(fd: FragmentDetails) {
        val db = this.getWritableDatabase()
        val values = ContentValues()
        values.put(DAY, fd.getDay())
        values.put(SUBJECT, fd.getSubject())
        values.put(START_HOUR, fd.getStartHour())
        values.put(START_MINUTE, fd.getStartMinute())
        values.put(END_HOUR, fd.getEndHour())
        values.put(END_MINUTE, fd.getEndMinute())
        values.put(ROOM_NUMBER, fd.getRoomNo())
        db.insert(TABLE_DETAIL, null, values)
        db.close()
    }

    fun remove(fd: FragmentDetails) {
        val db = this.getWritableDatabase()
        val selection = FragmentDatabase.DAY + "=? AND " + FragmentDatabase.START_HOUR + "=? AND " + FragmentDatabase.START_MINUTE + "=?"
        val selectionArgs = arrayOf<String>(Integer.toString(fd.getDay()), Integer.toString(fd.getStartHour()), Integer.toString(fd.getEndMinute()))
        db.delete(FragmentDatabase.TABLE_DETAIL, selection, selectionArgs)
    }

    fun getLectureList(pos: Int): List {
        val sql = "select * from $TABLE_DETAIL where day=$pos order by $START_HOUR,$START_MINUTE"
        val db = this.getReadableDatabase()
        val cursor = db.rawQuery(sql, null)
        try {
            lecturesList.clear()
            while (cursor.moveToNext()) {
                val lecture = Lecture()
                lecture.setSubjectName(cursor.getString(1))
                lecture.setStartTime(String.format(Locale.US, "%02d:%02d", cursor.getInt(2), cursor.getInt(3)))
                lecture.setEndTime(String.format(Locale.US, "%02d:%02d", cursor.getInt(4), cursor.getInt(5)))
                lecture.setDay(cursor.getInt(0))
                lecture.setRoomNo(cursor.getString(6))
                lecturesList.add(lecture)
            }
        } catch (e: Exception) {
        } finally {
            cursor.close()
        }
        db.close()
        return lecturesList
    }

    companion object {

        private val TAG = "FragmentDatabase"
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "lecture.db"
        private val TABLE_DETAIL = "lectureDetails"
        private val DAY = "day"
        private val SUBJECT = "subject"
        private val START_HOUR = "startHour"
        private val END_HOUR = "endHour"
        private val START_MINUTE = "startMinute"
        private val END_MINUTE = "endMinute"
        private val ROOM_NUMBER = "roomNo"
        private val CREATE_TABLE = ("create table "
                + TABLE_DETAIL + "(" + DAY + " integer not null, " + SUBJECT + " varchar not null,"
                + START_HOUR + " integer not null," + START_MINUTE + " integer not null,"
                + END_HOUR + " integer not null," + END_MINUTE + " integer not null," + ROOM_NUMBER + " varchar not null);")
    }
}