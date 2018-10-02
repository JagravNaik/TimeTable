package com.nealgosalia.timetable.database

/**
 * Created by men_in_black007 on 11/12/16.
 */

class SubjectDetails {

    var subject: String? = null
    var attendedLectures: Int = 0
    var totalLectures: Int = 0

    constructor() {}

    constructor(subject: String, attendedLectures: Int, totalLectures: Int) {
        this.subject = subject
        this.attendedLectures = attendedLectures
        this.totalLectures = totalLectures
    }
}