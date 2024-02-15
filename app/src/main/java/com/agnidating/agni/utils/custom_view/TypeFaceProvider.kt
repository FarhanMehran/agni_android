package com.agnidating.agni.utils.custom_view

import android.content.Context
import android.graphics.Typeface
import android.util.Log

object TypeFaceProvider {
  private  var typeFace: Typeface? = null

    private val typeFaceHashMap = HashMap<String, Typeface?>()

    fun getTypeFace(context: Context, fontName:String):Typeface?{
        typeFace = typeFaceHashMap[fontName]
        if(typeFace == null){
            typeFace = Typeface.createFromAsset(context.assets, fontName)
            typeFaceHashMap.put(fontName, typeFace)
        }
        return typeFace
    }

}