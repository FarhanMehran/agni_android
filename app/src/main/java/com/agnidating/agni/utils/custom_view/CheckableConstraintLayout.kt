package com.agnidating.agni.utils.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import androidx.constraintlayout.widget.ConstraintLayout
import com.agnidating.agni.R

class CheckableConstraintLayout : ConstraintLayout, Checkable {
    private var defStyleRes: Int = 0
    private var defStyleAttr: Int = 0
    var mContext: Context
    lateinit var attrs: AttributeSet
    private var checked = false

    
    constructor(context: Context) : super(context) {
        mContext = context
        initAtrr()
    }
    
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        this.attrs = attrs!!
        initAtrr()
    }
    
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mContext = context
        this.attrs = attrs!!
        this.defStyleAttr = defStyleAttr
        initAtrr()
    }
    
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        mContext = context
        this.attrs = attrs!!
        this.defStyleAttr = defStyleAttr
        this.defStyleRes = defStyleRes
        initAtrr()
    }

    private fun initAtrr() {
        val typedArray = mContext.theme.obtainStyledAttributes(attrs, R.styleable.CheckableConstraint,
            0, 0)
        checked=typedArray.getBoolean(R.styleable.CheckableConstraint_checked,false)
    }

    override fun setChecked(checked: Boolean) {
        this.checked = checked
        refreshDrawableState()
    }

    override fun isChecked(): Boolean {
        return checked
    }

    override fun toggle() {
        checked = !checked
        refreshDrawableState()
    }


    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (checked) {
            View.mergeDrawableStates(
                drawableState,
                CheckedStateSet
            )
        }
        return drawableState
    }

    companion object {
        private val CheckedStateSet = intArrayOf(
            android.R.attr.state_checked
        )
    }

}