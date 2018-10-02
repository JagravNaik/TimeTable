package com.nealgosalia.timetable.utils

import java.util.Comparator

class Subject : Comparable<Subject> {

    var subjectName: String? = null
    var attendedLectures: Int = 0
    var totalLectures: Int = 0

    @Override
    fun compareTo(o: Subject): Int {
        return Comparators.NAME.compare(this, o)
    }


    object Comparators {

        var NAME: Comparator<Subject> = object : Comparator<Subject>() {
            @Override
            fun compare(o1: Subject, o2: Subject): Int {
                return o1.subjectName!!.compareTo(o2.subjectName)
            }
        }
    }
}
