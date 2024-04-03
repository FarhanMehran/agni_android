package com.agnidating.agni.base


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class BaseAdapter<VB : ViewBinding>(val layout: Int):RecyclerView.Adapter<BaseAdapter.MyViewHolder<VB>>() {

    private lateinit var binding:VB
    private lateinit var mContext: Context


    class MyViewHolder<VB:ViewBinding>(val binding:VB):RecyclerView.ViewHolder(binding.root)

    fun getBinding():VB{
        return binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<VB> {
        mContext=parent.context
        binding= DataBindingUtil.inflate(LayoutInflater.from(parent.context),layout,parent,false)
        return MyViewHolder(binding)
    }
    fun requireContext():Context{
        return mContext
    }

}