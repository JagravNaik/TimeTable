package com.nealgosalia.timetable.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.nealgosalia.timetable.R
import com.nealgosalia.timetable.adapters.LecturesAdapter
import com.nealgosalia.timetable.database.FragmentDatabase
import com.nealgosalia.timetable.database.FragmentDetails
import com.nealgosalia.timetable.utils.DividerItemDecoration
import com.nealgosalia.timetable.utils.Lecture
import com.nealgosalia.timetable.utils.RecyclerItemClickListener

import java.util.ArrayList

class SundayFragment : Fragment() {

    private var db: FragmentDatabase? = null
    private var lecturesList: List<Lecture> = ArrayList()
    private var recyclerLectures: RecyclerView? = null
    private var mLectureAdapter: LecturesAdapter? = null
    private var placeholderText: TextView? = null
    private var view: View? = null

    @Override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle): View? {
        view = inflater.inflate(R.layout.fragment_sunday, container, false)
        placeholderText = view!!.findViewById(R.id.sundayPlaceholderText)
        db = FragmentDatabase(getActivity())
        lecturesList = ArrayList(db!!.getLectureList(6))
        if (lecturesList.size() !== 0) {
            placeholderText!!.setVisibility(View.GONE)
        }
        recyclerLectures = view!!.findViewById(R.id.listSunday)
        mLectureAdapter = LecturesAdapter(lecturesList)
        val mLayoutManager = LinearLayoutManager(getActivity())
        recyclerLectures!!.setLayoutManager(mLayoutManager)
        recyclerLectures!!.setItemAnimator(DefaultItemAnimator())
        recyclerLectures!!.addItemDecoration(DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL))
        recyclerLectures!!.setAdapter(mLectureAdapter)
        recyclerLectures!!.addOnItemTouchListener(RecyclerItemClickListener(getContext(), object : RecyclerItemClickListener.OnItemClickListener() {
            @Override
            fun onItemClick(view: View, position: Int) {
                val lecture = lecturesList[position]
                showDeleteDialog(lecture, position)
            }
        }))
        return view
    }

    fun showDeleteDialog(lecture: Lecture, position: Int) {
        val dialogBuilder = AlertDialog.Builder(getContext())
        dialogBuilder.setTitle(getResources().getString(R.string.delete))
        dialogBuilder.setMessage(getResources().getString(R.string.delete_lecture))
        dialogBuilder.setPositiveButton(getResources().getString(R.string.yes), object : DialogInterface.OnClickListener() {
            fun onClick(dialog: DialogInterface, whichButton: Int) {
                val fd = FragmentDetails(
                        lecture.getDay(),
                        lecture.getSubjectName(),
                        Integer.parseInt(lecture.getStartTime().substring(0, 2)),
                        Integer.parseInt(lecture.getStartTime().substring(3, 5)),
                        Integer.parseInt(lecture.getEndTime().substring(0, 2)),
                        Integer.parseInt(lecture.getEndTime().substring(3, 5)),
                        lecture.getRoomNo()


                )
                db!!.remove(fd)
                lecturesList.remove(position)
                mLectureAdapter!!.notifyDataSetChanged()
            }
        })
        dialogBuilder.setNegativeButton(getResources().getString(R.string.no), null)
        val b = dialogBuilder.create()
        b.show()
    }
}