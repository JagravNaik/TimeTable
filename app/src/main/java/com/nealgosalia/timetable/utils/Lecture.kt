package com.nealgosalia.timetable.utils

import java.util.Comparator

class Lecture : Comparable<Lecture> {

    var subjectName: String? = null
    var startTime: String? = null
    var endTime: String? = null
    var roomNo: String? = null
    var day: Int = 0


    @Override
    fun compareTo(o: Lecture): Int {
        return Lecture.Comparators.NAME.compare(this, o)
    }

    object Comparators {

        var NAME: Comparator<Lecture> = object : Comparator<Lecture>() {
            @Override
            fun compare(o1: Lecture, o2: Lecture): Int {
                return o1.startTime!!.compareTo(o2.startTime)
            }
        }
    }
}
