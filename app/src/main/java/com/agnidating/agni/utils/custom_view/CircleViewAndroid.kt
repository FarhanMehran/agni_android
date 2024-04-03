package com.agnidating.agni.utils.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.agnidating.agni.R

class CircleViewAndroid @JvmOverloads
constructor(
    private val mContext: Context,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : View(mContext, attributeSet, defStyleAttr) {

    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var size: Int=0

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = ContextCompat.getColor(mContext,R.color.highlight_color)
        val radius = size / 2f
        canvas!!.drawCircle(size / 2f, size / 2f, radius, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = measuredWidth.coerceAtMost(measuredHeight)
        setMeasuredDimension(size, size)
    }

}