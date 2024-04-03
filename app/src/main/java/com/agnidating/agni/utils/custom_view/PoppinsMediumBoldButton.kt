package com.agnidating.agni.utils.custom_view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton

class PoppinsMediumBoldButton(context: Context, attrs: AttributeSet?) : MaterialButton(context, attrs) {
    init {

        typeface = TypeFaceProvider.getTypeFace(context, "Poppins-Medium.ttf")
    }
}