package com.agnidating.agni.utils.custom_view

import android.content.Context
import android.util.AttributeSet

class PoppinsMediumCheckedBox(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatCheckBox(context, attrs) {
    init {

        typeface = TypeFaceProvider.getTypeFace(context, "Poppins-Medium.ttf")
    }
}