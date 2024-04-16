package com.agnidating.agni.utils.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.agnidating.agni.R
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HighlightableBottomNavigation(val mContext: Context,val attrs: AttributeSet): BottomNavigationView(mContext,attrs) {

    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var itemHeight=0
    var itemWidth=0
    var size=0
    var selectedMenuItem=-1

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = ContextCompat.getColor(mContext,R.color.highlight_color)
        if(selectedMenuItem!=-1){
            highLightMenuItem(canvas)
        }
    }

    private fun highLightMenuItem(canvas: Canvas?) {
        //get selected menu item
        val menuItem=findViewById<BottomNavigationItemView>(selectedMenuItem)
        if (menuItem.height<menuItem.width){
            itemHeight=menuItem.width
            itemWidth=menuItem.width
        }else{
            itemWidth=menuItem.width
            itemHeight=menuItem.width
        }
        //now itemItem will always be lower
        val extraLeft=(itemWidth-itemHeight)/2f
        val centerX=menuItem.left+extraLeft+itemHeight/2f
        val centerY=menuItem.top-itemHeight+itemHeight/2f
        canvas!!.drawCircle(centerX,centerY,itemHeight/2f,paint)
    }

    fun highLightItem(itemId:Int){
        selectedMenuItem=itemId
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }
}
