package com.example.notesapp.model

import org.threeten.bp.LocalDate
import java.io.Serializable

data class Note(val id:String):Serializable{

    constructor() : this("")

    var title:String? = null
    var content:String? = null
    var lmYear:Int? = null
    var lmMonth:Int? = null
    var lmDay:Int? = null
    var lmHour:Int? = null
    var lmMinute:Int? = null
    var photoUrl:String? = null
    var reminder:String? = null
}
