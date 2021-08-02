package ar.com.unlam.itercorpappchallenge

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(val listener: (dat: Int, month: Int, year: Int) -> Unit) :
    DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
    listener(day,month,year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val picker = DatePickerDialog(activity as Context, this,year,month,day)
        return picker
    }
}