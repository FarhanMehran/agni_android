package com.agnidating.agni.utils.custom_view

import android.content.Context
import android.util.AttributeSet

class PoppinsMediumItalicTextView(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    init {

        typeface = TypeFaceProvider.getTypeFace(context, "Poppins-MediumItalic.ttf")
    }
}