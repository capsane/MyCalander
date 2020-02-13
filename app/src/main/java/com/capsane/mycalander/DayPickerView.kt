/***********************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Robin Chutaux
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
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
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DayPickerView : RecyclerView{

    private var mContext: Context? = null

    private var simpleMonthAdapter: SimpleMonthAdapter? = null

    private var currentScrollState = 0

    private var previousScrollPosition: Long = 0

    private var previousScrollState = 0

    private var typedArray: TypedArray? = null

    private var scrollListener: OnScrollListener? = null

    val selectedDays: SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay>?
        get() = simpleMonthAdapter?.selectedDays

    var controller: DatePickerController? = null
        set(value) {
            field = value
            setupAdapter()
            adapter = simpleMonthAdapter
        }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        if (!isInEditMode) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.DayPickerView)
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            init(context)
        }
    }

    private fun init(paramContext: Context?) {
        val linearLayoutManager = LinearLayoutManager(paramContext)
        layoutManager = linearLayoutManager
        mContext = paramContext
        setupListView()
        scrollListener = object : OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
//                val child = recyclerView.getChildAt(0) as SimpleMonthView ?: return
                previousScrollPosition = dy.toLong()
                previousScrollState = currentScrollState
            }
        }
    }

    fun setupAdapter() {
        if (simpleMonthAdapter == null) {
            simpleMonthAdapter = SimpleMonthAdapter(context, controller, typedArray)
        }
        simpleMonthAdapter?.notifyDataSetChanged()
    }

    private fun setupListView() {
        isVerticalScrollBarEnabled = false
        setOnScrollListener(scrollListener)
        setFadingEdgeLength(0)
    }

    fun setMinDay(minDay: Calendar?) {
        simpleMonthAdapter?.setMinDay(minDay)
    }

    fun setSingle(isSingle: Boolean) {
        simpleMonthAdapter?.setSingle(isSingle)
    }


}