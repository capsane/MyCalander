package com.capsane.mycalander

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity(), DatePickerController, View.OnClickListener {

    private var mDayPickerView: DayPickerView? = null
    private var mGoLayout: RelativeLayout? = null
    private var mGo: TextView? = null
    private var mGoDelete: ImageView? = null
    private var mBackLayout: RelativeLayout? = null
    private var mBack: TextView? = null
    private var mBackDelete: ImageView? = null
    private var isSingle = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDayPickerView = findViewById(R.id.day_picker)
        mGoLayout = findViewById(R.id.go_layout)
        mGo = findViewById(R.id.go)
        mGoDelete = findViewById(R.id.go_delete)
        mBackLayout = findViewById(R.id.back_layout)
        mBack = findViewById(R.id.back)
        mBackDelete = findViewById(R.id.back_delete)
        mDayPickerView = findViewById(R.id.day_picker)
        mDayPickerView!!.controller = this
        mGoDelete!!.setOnClickListener(this)
        mBackDelete!!.setOnClickListener(this)

        // 设置最小可选日期为当天0点
        val minDay = "2020-2-13"
        if (!TextUtils.isEmpty(minDay)) {
            val calender = Calendar.getInstance()

            // 也可以设置为明天的0点
//            calender.add(Calendar.DATE, 1)
//            calendar.set(Calendar.HOUR_OF_DAY, 0)
//            calendar.set(Calendar.MINUTE, 0)
//            calendar.set(Calendar.SECOND, 0)
//            calendar.set(Calendar.MILLISECOND, 0)

            try {
                calender.timeInMillis = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(minDay).time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            mDayPickerView?.setMinDay(calender)
        }

        // setSingle必须要在setMinDay之后
        mDayPickerView?.setSingle(true)

    }


    private fun getYYYY_MM_dd(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.go_delete -> {
                mDayPickerView!!.selectedDays.first = null
                mDayPickerView!!.selectedDays.setLast(null)
            }
            R.id.back_delete -> {
                mDayPickerView!!.selectedDays.setLast(null)
            }
            else -> {
            }
        }

        //刷新日历??
        mDayPickerView!!.setUpAdapter()
        onDateRangeSelected(mDayPickerView!!.selectedDays)
    }

    override fun getMaxYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR) + 1
    }

    /**
     * 当日期发生改变每次都会调用的函数
     * @param year
     * @param month
     * @param day
     */
    override fun onDayOfMonthSelected(year: Int, month: Int, day: Int) {
        Toast.makeText(this, "$year-${month+1}-$day", Toast.LENGTH_SHORT).show()
    }

    /**
     * 选中多个的时候触发,但是每次选中或者改变日期的时候都会触发
     * @param selectedDays
     */
    override fun onDateRangeSelected(selectedDays: SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay>?) {

    }
}
