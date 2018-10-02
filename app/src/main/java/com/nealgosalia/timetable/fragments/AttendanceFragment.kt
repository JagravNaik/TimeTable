package com.nealgosalia.timetable.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.nealgosalia.timetable.R
import com.nealgosalia.timetable.adapters.AttendanceAdapter
import com.nealgosalia.timetable.database.SubjectDatabase
import com.nealgosalia.timetable.database.SubjectDetails
import com.nealgosalia.timetable.utils.DividerItemDecoration
import com.nealgosalia.timetable.utils.RecyclerItemClickListener
import com.nealgosalia.timetable.utils.Subject
import com.shawnlin.numberpicker.NumberPicker

import java.util.ArrayList

class AttendanceFragment : Fragment() {

    private val subjectsList = ArrayList()
    private var listSubjects: RecyclerView? = null
    private var mAttendanceAdapter: AttendanceAdapter? = null
    private var subjectDatabase: SubjectDatabase? = null
    private var placeholderText: TextView? = null
    private var view: View? = null
    private val progressList = ArrayList()
    private val p = Paint()

    @Override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle): View? {
        view = inflater.inflate(R.layout.fragment_attendance, container, false)
        listSubjects = view!!.findViewById(R.id.listAttendance)
        placeholderText = view!!.findViewById(R.id.attendancePlaceholderText)
        subjectDatabase = SubjectDatabase(getActivity())
        subjectsList.clear()
        for (subjectDetails in subjectDatabase!!.getSubjectDetail()) {
            val progress: Int
            val subject = Subject()
            subject.setSubjectName(subjectDetails.getSubject())
            subject.setAttendedLectures(subjectDetails.getAttendedLectures())
            subject.setTotalLectures(subjectDetails.getTotalLectures())
            if (subjectDetails.getTotalLectures() !== 0) {
                progress = subjectDetails.getAttendedLectures() * 100 / subjectDetails.getTotalLectures()
            } else {
                progress = 0
            }
            progressList.add(progress)
            subjectsList.add(subject)
        }
        if (subjectsList.size() !== 0) {
            placeholderText!!.setVisibility(View.GONE)
        }
        mAttendanceAdapter = AttendanceAdapter(subjectsList, progressList)
        val mLayoutManager = LinearLayoutManager(getActivity())
        listSubjects!!.setLayoutManager(mLayoutManager)
        listSubjects!!.setItemAnimator(DefaultItemAnimator())
        listSubjects!!.addItemDecoration(DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL))
        listSubjects!!.setAdapter(mAttendanceAdapter)
        listSubjects!!.addOnItemTouchListener(RecyclerItemClickListener(getContext(), object : RecyclerItemClickListener.OnItemClickListener() {
            @Override
            fun onItemClick(view: View, position: Int) {
                val subject = subjectsList.get(position)
                showAttendanceDialog(subject, position)
            }
        }))
        initSwipe()
        return view
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            @Override
            fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            @Override
            fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.getAdapterPosition()
                val subjectNew = Subject()
                var attended = subjectsList.get(position).getAttendedLectures()
                var total = subjectsList.get(position).getTotalLectures()
                val progress: Int
                if (direction == ItemTouchHelper.LEFT) {
                    progress = attended * 100 / ++total
                } else {
                    progress = ++attended * 100 / ++total
                }
                subjectNew.setSubjectName(subjectsList.get(position).getSubjectName())
                subjectNew.setAttendedLectures(attended)
                subjectNew.setTotalLectures(total)
                val sdNew = SubjectDetails()
                sdNew.setSubject(subjectNew.getSubjectName())
                sdNew.setAttendedLectures(subjectNew.getAttendedLectures())
                sdNew.setTotalLectures(subjectNew.getTotalLectures())
                subjectDatabase!!.updateSubject(sdNew)
                progressList.set(position, progress)
                subjectsList.set(position, subjectNew)
                mAttendanceAdapter!!.notifyDataSetChanged()
            }

            @Override
            fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView
                    val height = itemView.getBottom() as Float - itemView.getTop() as Float
                    val width = height / 3

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#4CAF50"))
                        val background = RectF(itemView.getLeft() as Float, itemView.getTop() as Float, dX, itemView.getBottom() as Float)
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done)
                        val icon_dest = RectF(itemView.getLeft() as Float + width, itemView.getTop() as Float + width, itemView.getLeft() as Float + 2 * width, itemView.getBottom() as Float - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    } else if (dX < 0) {
                        p.setColor(Color.parseColor("#F44336"))
                        val background = RectF(itemView.getRight() as Float + dX, itemView.getTop() as Float, itemView.getRight() as Float, itemView.getBottom() as Float)
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clear)
                        val icon_dest = RectF(itemView.getRight() as Float - 2 * width, itemView.getTop() as Float + width, itemView.getRight() as Float - width, itemView.getBottom() as Float - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(listSubjects)
    }

    fun showAttendanceDialog(subject: Subject, position: Int) {
        val dialogBuilder = AlertDialog.Builder(getContext())
        val inflater = getActivity().getLayoutInflater()
        val dialogView = inflater.inflate(R.layout.dialog_edit_attendance, null)
        val attendedLecturesNumberPicker = dialogView.findViewById(R.id.attendedLecturesNumberPicker)
        val totalLecturesNumberPicker = dialogView.findViewById(R.id.totalLecturesNumberPicker)
        attendedLecturesNumberPicker.setValue(subject.getAttendedLectures())
        totalLecturesNumberPicker.setValue(subject.getTotalLectures())
        //int attendedLectures = attendedLecturesNumberPicker.getValue();
        //int totalLectures = ;
        attendedLecturesNumberPicker.setMaxValue(totalLecturesNumberPicker.getValue())
        totalLecturesNumberPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener() {
            @Override
            fun onValueChange(numberPicker: NumberPicker, i: Int, i1: Int) {
                attendedLecturesNumberPicker.setMaxValue(totalLecturesNumberPicker.getValue())
            }
        })
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(getResources().getString(R.string.attendance) + ": " + subject.getSubjectName())
        dialogBuilder.setPositiveButton(getResources().getString(R.string.save), object : DialogInterface.OnClickListener() {
            fun onClick(dialog: DialogInterface, whichButton: Int) {
                val attendedLectures = attendedLecturesNumberPicker.getValue()
                val totalLectures = totalLecturesNumberPicker.getValue()
                val subjectDetails = SubjectDetails(
                        subject.getSubjectName(),
                        attendedLectures,
                        totalLectures
                )
                subjectDatabase!!.updateSubject(subjectDetails)
                progressList.set(position, attendedLectures * 100 / totalLectures)
                subject.setAttendedLectures(attendedLectures)
                subject.setTotalLectures(totalLectures)
                subjectsList.set(position, subject)
                mAttendanceAdapter!!.notifyDataSetChanged()
            }
        })
        dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), null)
        val b = dialogBuilder.create()
        b.show()
    }
}