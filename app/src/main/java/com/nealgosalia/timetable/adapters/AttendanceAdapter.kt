package com.nealgosalia.timetable.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.daimajia.numberprogressbar.NumberProgressBar
import com.nealgosalia.timetable.R
import com.nealgosalia.timetable.utils.Subject

class AttendanceAdapter(private val subjectList: List<Subject>, private val progressList: List<Integer>) : RecyclerView.Adapter<AttendanceAdapter.MyViewHolder>() {

    val itemCount: Int
        @Override
        get() = subjectList.size()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var subjectName: TextView
        var attendance: NumberProgressBar

        init {
            subjectName = view.findViewById(R.id.subjectNameAttendance)
            attendance = view.findViewById(R.id.attendance_progress_bar)
        }
    }

    @Override
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_attendance, parent, false)

        return MyViewHolder(itemView)
    }

    @Override
    fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val subject = subjectList[position]
        holder.subjectName.setText(subject.getSubjectName())
        holder.attendance.setProgress(progressList[position])
    }
}
