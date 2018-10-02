package com.nealgosalia.timetable.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.nealgosalia.timetable.R
import com.nealgosalia.timetable.utils.Subject

class SubjectsAdapter(private val subjectsList: List<Subject>) : RecyclerView.Adapter<SubjectsAdapter.MyViewHolder>() {

    val itemCount: Int
        @Override
        get() = subjectsList.size()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var subjectName: TextView

        init {
            subjectName = view.findViewById(R.id.subjectName)
        }
    }

    @Override
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_subjects, parent, false)

        return MyViewHolder(itemView)
    }

    @Override
    fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val subject = subjectsList[position]
        holder.subjectName.setText(subject.getSubjectName())
    }
}