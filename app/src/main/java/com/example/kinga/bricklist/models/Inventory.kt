package com.example.kinga.bricklist.models;

import android.os.Build
import android.support.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class Inventory {
    var id: Int = -1
    var name: String = ""
    var active: Int = 1
    var date: Int = 0

    constructor(id: Int, name: String, active: Int, date: Int){
        this.id = id
        this.name = name
        this.active = active
        this.date = date
    }

    constructor()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): Int {
        val date = Date()
        val sdf = SimpleDateFormat("yyyyMMdd")
        return sdf.format(date).toInt()
    }
}
