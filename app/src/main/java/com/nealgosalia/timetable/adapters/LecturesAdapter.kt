package com.nealgosalia.timetable.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.nealgosalia.timetable.R
import com.nealgosalia.timetable.utils.Lecture

class LecturesAdapter(private val lectureList: List<Lecture>) : RecyclerView.Adapter<LecturesAdapter.MyViewHolder>() {

    val itemCount: Int
        @Override
        get() = lectureList.size()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lectureName: TextView
        var lectureTime: TextView
        var lectureroom: TextView

        init {
            lectureName = view.findViewById(R.id.lectureName)
            lectureTime = view.findViewById(R.id.lectureTime)
            lectureroom = view.findViewById(R.id.lectureroom)

        }
    }

    @Override
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_lectures, parent, false)

        return MyViewHolder(itemView)
    }

    @Override
    fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val lecture = lectureList[position]
        holder.lectureName.setText(lecture.getSubjectName())
        holder.lectureTime.setText(lecture.getStartTime() + " - " + lecture.getEndTime())
        holder.lectureroom.setText(lecture.getRoomNo())
    }
}