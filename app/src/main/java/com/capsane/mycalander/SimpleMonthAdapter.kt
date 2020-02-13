/***********************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2014 Robin Chutaux
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.capsane.mycalander

import android.content.Context
import android.content.res.TypedArray
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import com.capsane.mycalander.SimpleMonthView.OnDayClickListener
import java.io.Serializable
import java.util.*

class SimpleMonthAdapter(
    context: Context,
    private val mController: DatePickerController,
    private val typedArray: TypedArray
) : RecyclerView.Adapter<SimpleMonthAdapter.ViewHolder>(),
    OnDayClickListener {
    private val mContext: Context
    private val calendar: Calendar
    val selectedDays: SelectedDays<CalendarDay?>
    private val firstMonth: Int
    private var lastMonth: Int
    private var lastMonthDay: Int
    private val selectableDaysDuration: Int
    private var maxYear: Int
    private var isSingle: Boolean
    private var minDay: Calendar? = null

    init {
        calendar = Calendar.getInstance()
        firstMonth = typedArray.getInt(
            R.styleable.DayPickerView_firstMonth,
            calendar[Calendar.MONTH]
        )
        // 设置可选时间
        selectableDaysDuration =
            typedArray.getInt(R.styleable.DayPickerView_selectableDaysDuration, -1)
        // 兼容根据日期设置maxYear，lastMonth
        if (selectableDaysDuration <= 0) {
            maxYear = mController.maxYear
            lastMonth = typedArray.getInt(
                R.styleable.DayPickerView_lastMonth,
                calendar[Calendar.MONTH] % MONTHS_IN_YEAR
            )
            lastMonthDay = -1
        } else {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, selectableDaysDuration)
            maxYear = calendar[Calendar.YEAR]
            lastMonth =
                calendar[Calendar.MONTH] % MONTHS_IN_YEAR
            lastMonthDay = calendar[Calendar.DATE]
        }
        isSingle = typedArray.getBoolean(R.styleable.DayPickerView_selectSingle, true)
        selectedDays = SelectedDays()
        mContext = context
        init()
    }

    private fun init() {
        if (typedArray.getBoolean(R.styleable.DayPickerView_currentDaySelected, false)) {
            onDayTapped(CalendarDay(System.currentTimeMillis()))
        }
        if (isSingle) {
            selectedDays.setLast(CalendarDay(System.currentTimeMillis()))
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val simpleMonthView = SimpleMonthView(mContext, typedArray)
        simpleMonthView.setMinDay(minDay)
        simpleMonthView.setMaxYear(maxYear)
        simpleMonthView.setLastMonth(lastMonth)
        simpleMonthView.setLastMonthDay(lastMonthDay)
        return ViewHolder(simpleMonthView, this)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val v = viewHolder.simpleMonthView
        val drawingParams =
            HashMap<String, Int>()
        val month: Int
        val year: Int
        month =
            (firstMonth + position % MONTHS_IN_YEAR) % MONTHS_IN_YEAR
        year =
            position / MONTHS_IN_YEAR + calendar[Calendar.YEAR] + (firstMonth + position % MONTHS_IN_YEAR) / MONTHS_IN_YEAR
        var selectedFirstDay = -1
        var selectedLastDay = -1
        var selectedFirstMonth = -1
        var selectedLastMonth = -1
        var selectedFirstYear = -1
        var selectedLastYear = -1
        if (selectedDays.first != null) {
            selectedFirstDay = selectedDays.first!!.day
            selectedFirstMonth = selectedDays.first!!.month
            selectedFirstYear = selectedDays.first!!.year
        }
        if (selectedDays.last != null && !isSingle) {
            selectedLastDay = selectedDays.last!!.day
            selectedLastMonth = selectedDays.last!!.month
            selectedLastYear = selectedDays.last!!.year
        }
        v.reuse()
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_YEAR] = selectedFirstYear
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_YEAR] = selectedLastYear
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_MONTH] = selectedFirstMonth
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_MONTH] = selectedLastMonth
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_DAY] = selectedFirstDay
        drawingParams[SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_DAY] = selectedLastDay
        drawingParams[SimpleMonthView.VIEW_PARAMS_YEAR] = year
        drawingParams[SimpleMonthView.VIEW_PARAMS_MONTH] = month
        drawingParams[SimpleMonthView.VIEW_PARAMS_WEEK_START] = calendar.firstDayOfWeek
        v.setMonthParams(drawingParams)
        v.invalidate()
    }

    override fun getItemCount(): Int {
        var itemCount = (maxYear - calendar[Calendar.YEAR] + 1) * MONTHS_IN_YEAR
//        var itemCount = (((mController!!.maxYear - calendar.get(Calendar.YEAR)) + 1) * MONTHS_IN_YEAR)
        if (firstMonth != -1) itemCount -= firstMonth
        if (lastMonth != -1) itemCount -= MONTHS_IN_YEAR - lastMonth - 1
        return itemCount
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     *
     * @param minDay 可选中的最小日期
     */
    fun setMinDay(minDay: Calendar?) {
        this.minDay = minDay
    }

    fun setSingle(isSingle: Boolean) {
        this.isSingle = isSingle
        if (isSingle) {
            // 默认选中日期为minDay
            selectedDays.setFirst(CalendarDay(minDay!!.timeInMillis))
//            selectedDays.setLast(CalendarDay(System.currentTimeMillis()))
        } else {
            selectedDays.setLast(null)
        }
    }

    class ViewHolder(itemView: View, onDayClickListener: OnDayClickListener?) : RecyclerView.ViewHolder(itemView) {
        val simpleMonthView = (itemView as SimpleMonthView).apply {
            layoutParams = AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isClickable = true
            setOnDayClickListener(onDayClickListener)
        }
    }

    override fun onDayClick(
        simpleMonthView: SimpleMonthView,
        calendarDay: CalendarDay
    ) {
        calendarDay?.let { onDayTapped(it) }
    }

    private fun onDayTapped(calendarDay: CalendarDay) {
        mController.onDayOfMonthSelected(calendarDay.year, calendarDay.month, calendarDay.day)
        setSelectedDay(calendarDay)
    }

    private fun setSelectedDay(calendarDay: CalendarDay) {
        if (selectedDays.first != null && selectedDays.last == null) {
            selectedDays.setLast(calendarDay)
            if (selectedDays.first!!.month < calendarDay.month) {
                for (i in 0 until selectedDays.first!!.month - calendarDay.month - 1) mController.onDayOfMonthSelected(
                    selectedDays.first!!.year,
                    selectedDays.first!!.month + i,
                    selectedDays.first!!.day
                )
            }
        } else if (selectedDays.last != null) {
            selectedDays.setFirst(calendarDay)
            if (!isSingle) {
                selectedDays.setLast(null)
            }
        } else {
            selectedDays.setFirst(calendarDay)
        }
        mController.onDateRangeSelected(selectedDays)
        notifyDataSetChanged()
    }

    class CalendarDay : Serializable {
        private var calendar: Calendar? = null
        @JvmField
        var day = 0
        @JvmField
        var month = 0
        @JvmField
        var year = 0

        constructor() {
            setTime(System.currentTimeMillis())
        }

        constructor(year: Int, month: Int, day: Int) {
            setDay(year, month, day)
        }

        constructor(timeInMillis: Long) {
            setTime(timeInMillis)
        }

        constructor(calendar: Calendar) {
            year = calendar[Calendar.YEAR]
            month = calendar[Calendar.MONTH]
            day = calendar[Calendar.DAY_OF_MONTH]
        }

        private fun setTime(timeInMillis: Long) {
            if (calendar == null) {
                calendar = Calendar.getInstance()
            }
            calendar!!.timeInMillis = timeInMillis
            month = calendar!![Calendar.MONTH]
            year = calendar!![Calendar.YEAR]
            day = calendar!![Calendar.DAY_OF_MONTH]
        }

        fun set(calendarDay: CalendarDay) {
            year = calendarDay.year
            month = calendarDay.month
            day = calendarDay.day
        }

        fun setDay(year: Int, month: Int, day: Int) {
            this.year = year
            this.month = month
            this.day = day
        }

        val date: Date
            get() {
                if (calendar == null) {
                    calendar = Calendar.getInstance()
                }
                calendar!![year, month] = day
                return calendar!!.time
            }

        override fun toString(): String {
            val stringBuilder = StringBuilder()
            stringBuilder.append("{ year: ")
            stringBuilder.append(year)
            stringBuilder.append(", month: ")
            stringBuilder.append(month)
            stringBuilder.append(", day: ")
            stringBuilder.append(day)
            stringBuilder.append(" }")
            return stringBuilder.toString()
        }

        companion object {
            private const val serialVersionUID = -5456695978688356202L
        }
    }

    class SelectedDays<K> : Serializable {
        var first: K? = null
            private set
        var last: K? = null
            private set

        fun setFirst(first: K) {
            this.first = first
        }

        fun setLast(last: K) {
            this.last = last
        }

        companion object {
            private const val serialVersionUID = 3942549765282708376L
        }
    }

    companion object {
        @JvmField
        val MONTHS = arrayOf("一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二")
        const val MONTHS_IN_YEAR = 12
    }
}