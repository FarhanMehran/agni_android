package com.agnidating.agni.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("TextSelected")
fun TextView.setTextSelected(text:String){
    this.text=text
    this.isSelected = true
}