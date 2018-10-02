package com.nealgosalia.timetable.utils

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Created by kira on 18/1/17.
 */

class RecyclerItemClickListener(context: Context, private val mListener: OnItemClickListener?) : RecyclerView.OnItemTouchListener {

    internal var mGestureDetector: GestureDetector

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    init {
        mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            @Override
            fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })
    }

    @Override
    fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.getX(), e.getY())
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
        }
        return false
    }

    @Override
    fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {
    }

    @Override
    fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}