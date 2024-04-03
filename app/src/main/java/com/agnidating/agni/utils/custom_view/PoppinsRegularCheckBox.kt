package com.agnidating.agni.utils.custom_view

import android.content.Context
import android.util.AttributeSet

class PoppinsRegularCheckBox(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatCheckBox(context, attrs) {
    init {

        typeface = TypeFaceProvider.getTypeFace(context, "Poppins-Regular.ttf")
    }
}