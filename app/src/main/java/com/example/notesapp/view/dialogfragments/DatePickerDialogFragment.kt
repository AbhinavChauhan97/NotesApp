package com.example.notesapp.view.dialogfragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment

class DatePickerDialogFragment : DialogFragment() {

    var onDateSelection:((year:Int,month:Int,dayOfMonth:Int) -> Unit)? = null
    private val dateSetListener =
        DatePickerDialog.OnDateSetListener{
                view, year, month, dayOfMonth -> onDateSelection?.invoke(year,month,dayOfMonth)}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val datePickerDialog =  DatePickerDialog(requireActivity())
        datePickerDialog.setOnDateSetListener(dateSetListener)
        return datePickerDialog
    }

    public fun doOnDateSelected(onDateSelected:((year:Int,month:Int,dayOfMonth:Int) -> Unit)){
        this.onDateSelection = onDateSelected
    }
}