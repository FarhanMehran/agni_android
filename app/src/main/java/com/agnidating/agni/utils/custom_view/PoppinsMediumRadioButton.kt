package com.agnidating.agni.utils.custom_view

import android.content.Context
import android.util.AttributeSet

class PoppinsMediumRadioButton(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatRadioButton(context, attrs) {
    init {

        typeface = TypeFaceProvider.getTypeFace(context, "Poppins-Medium.ttf")
    }
}