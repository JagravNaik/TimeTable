package com.nealgosalia.timetable.fragments

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView

import com.nealgosalia.timetable.R
import com.nealgosalia.timetable.adapters.SubjectsAdapter
import com.nealgosalia.timetable.database.SubjectDatabase
import com.nealgosalia.timetable.database.SubjectDetails
import com.nealgosalia.timetable.utils.DividerItemDecoration
import com.nealgosalia.timetable.utils.Subject

import java.util.ArrayList
import java.util.Collections

class SubjectsFragment : Fragment() {

    private val subjectsList = ArrayList()
    private var listSubjects: RecyclerView? = null
    private var mSubjectsAdapter: SubjectsAdapter? = null
    private var subjectDatabase: SubjectDatabase? = null
    private var placeholderText: TextView? = null
    private var view: View? = null
    private var dialogView: View? = null
    private val p = Paint()
    private var alertDialog: AlertDialog.Builder? = null
    private var editSubject: AutoCompleteTextView? = null
    private var newSubjectName: AutoCompleteTextView? = null

    internal var sub: Array<String>
    @Override
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle): View? {
        view = inflater.inflate(R.layout.activity_subjects, container, false)
        listSubjects = view!!.findViewById(R.id.listSubjects)
        placeholderText = view!!.findViewById(R.id.subjectsPlaceholderText)
        subjectDatabase = SubjectDatabase(getActivity())
        subjectsList.clear()
        for (subjectDetails in subjectDatabase!!.getSubjectDetail()) {
            val subject = Subject()
            subject.setSubjectName(subjectDetails.getSubject())
            subjectsList.add(subject)
        }
        Log.d("SubjectsFragment", String.valueOf(subjectsList.size()))
        if (subjectsList.size() !== 0) {
            placeholderText!!.setVisibility(View.GONE)
        }
        mSubjectsAdapter = SubjectsAdapter(subjectsList)
        val mLayoutManager = LinearLayoutManager(getActivity())
        listSubjects!!.setLayoutManager(mLayoutManager)
        listSubjects!!.setItemAnimator(DefaultItemAnimator())
        listSubjects!!.addItemDecoration(DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL))
        listSubjects!!.setAdapter(mSubjectsAdapter)
        val fab = view!!.findViewById(R.id.fab)
        fab.setOnClickListener(object : View.OnClickListener() {
            @Override
            fun onClick(view: View) {
                showSubjectDialog()
            }
        })
        initSwipe()
        return view
    }

    fun showSubjectDialog() {
        val dialogBuilder = AlertDialog.Builder(getActivity())
        val inflater = getActivity().getLayoutInflater()
        val dialogView = inflater.inflate(R.layout.dialog_add_subject, null)
        dialogBuilder.setView(dialogView)
        newSubjectName = dialogView.findViewById(R.id.newSubjectName)
        sub = getResources().getStringArray(R.array.subjectNames)
        val adapter = ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sub)
        newSubjectName!!.setThreshold(2)
        newSubjectName!!.setAdapter(adapter)
        dialogBuilder.setTitle(getResources().getString(R.string.subject))
        dialogBuilder.setMessage(getResources().getString(R.string.enter_subject_name))
        dialogBuilder.setPositiveButton(getResources().getString(R.string.add), object : DialogInterface.OnClickListener() {
            fun onClick(dialog: DialogInterface, whichButton: Int) {
                val subject = Subject()
                val tempSubject = newSubjectName!!.getText().toString().trim()
                subject.setSubjectName(tempSubject)
                subjectDatabase!!.addSubject(SubjectDetails(tempSubject, 0, 0))
                subjectsList.add(subject)
                Collections.sort(subjectsList, Subject.Comparators.NAME)
                mSubjectsAdapter!!.notifyDataSetChanged()
                placeholderText!!.setVisibility(View.GONE)
                newSubjectName!!.setText("")
            }
        })
        dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), null)
        val dialog = dialogBuilder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false)
        newSubjectName!!.addTextChangedListener(object : TextWatcher() {
            @Override
            fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            @Override
            fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            @Override
            fun afterTextChanged(editable: Editable) {
                if (editable.length() >= 1) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true)
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false)
                }
            }
        })
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

                if (direction == ItemTouchHelper.LEFT) {
                    deleteSwipe(position)
                } else {
                    initDialog(position)
                }
            }

            @Override
            fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView
                    val height = itemView.getBottom() as Float - itemView.getTop() as Float
                    val width = height / 3

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#FF5722"))
                        val background = RectF(itemView.getLeft() as Float, itemView.getTop() as Float, dX, itemView.getBottom() as Float)
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white)
                        val icon_dest = RectF(itemView.getLeft() as Float + width, itemView.getTop() as Float + width, itemView.getLeft() as Float + 2 * width, itemView.getBottom() as Float - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    } else if (dX < 0) {
                        p.setColor(Color.parseColor("#009688"))
                        val background = RectF(itemView.getRight() as Float + dX, itemView.getTop() as Float, itemView.getRight() as Float, itemView.getBottom() as Float)
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white)
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

    private fun initDialog(position: Int) {
        alertDialog = AlertDialog.Builder(getActivity())
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_subject, null)
        editSubject = dialogView!!.findViewById(R.id.edit_subject)
        sub = getResources().getStringArray(R.array.subjectNames)
        val adapte = ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sub)
        if (editSubject != null) {
            editSubject!!.setThreshold(2)
            editSubject!!.setAdapter(adapte)
        }
        alertDialog!!.setView(dialogView)
        alertDialog!!.setTitle(getResources().getString(R.string.edit_subject))
        alertDialog!!.setPositiveButton("Save", object : DialogInterface.OnClickListener() {
            @Override
            fun onClick(dialog: DialogInterface, which: Int) {
                val subject = subjectsList.get(position)
                val sd = SubjectDetails(
                        editSubject!!.getText().toString(),
                        subject.getAttendedLectures(),
                        subject.getTotalLectures()
                )
                subjectDatabase!!.updateSubject(sd)
                subjectsList.set(position, subject)
                Collections.sort(subjectsList, Subject.Comparators.NAME)
                mSubjectsAdapter!!.notifyDataSetChanged()
                dialog.dismiss()
            }
        })
        alertDialog!!.setNegativeButton(getResources().getString(R.string.cancel), object : DialogInterface.OnClickListener() {
            @Override
            fun onClick(dialog: DialogInterface, which: Int) {
                mSubjectsAdapter!!.notifyDataSetChanged()
                dialog.dismiss()
            }
        })
        val dialog = alertDialog!!.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false)
        editSubject!!.addTextChangedListener(object : TextWatcher() {
            @Override
            fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            @Override
            fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            @Override
            fun afterTextChanged(editable: Editable) {
                if (editable.length() >= 1) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true)
                    mSubjectsAdapter!!.notifyDataSetChanged()
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false)
                    mSubjectsAdapter!!.notifyDataSetChanged()
                }
            }
        })
        mSubjectsAdapter!!.notifyDataSetChanged()
    }

    fun deleteSwipe(position: Int) {
        val alertDialog = android.app.AlertDialog.Builder(getActivity())
        alertDialog.setTitle(getResources().getString(R.string.warning))
        alertDialog.setMessage(getResources().getString(R.string.subject_question))
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), object : DialogInterface.OnClickListener() {
            @Override
            fun onClick(dialog: DialogInterface, which: Int) {
                val subject = subjectsList.get(position)
                val sd = SubjectDetails()
                sd.setSubject(subject.getSubjectName())
                subjectDatabase!!.removeSubject(sd)
                subjectsList.remove(position)
                mSubjectsAdapter!!.notifyDataSetChanged()
            }
        })
        alertDialog.setNegativeButton(getResources().getString(R.string.no), object : DialogInterface.OnClickListener() {
            @Override
            fun onClick(dialog: DialogInterface, which: Int) {
                mSubjectsAdapter!!.notifyDataSetChanged()
            }
        })
        alertDialog.show()
        mSubjectsAdapter!!.notifyDataSetChanged()
    }
}