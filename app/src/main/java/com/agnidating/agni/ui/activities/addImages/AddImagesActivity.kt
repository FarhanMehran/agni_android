package com.agnidating.agni.ui.activities.addImages

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.ActivityAddImagesBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.getStarted.GetStarted
import com.agnidating.agni.ui.activities.writeBio.WriteBio
import com.agnidating.agni.utils.custom_view.ImagePickerDialog
import com.agnidating.agni.utils.errorDialog
import com.agnidating.agni.utils.startNewActivityWithFinish
import com.agnidating.agni.utils.toMultipartList
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.agnidating.agni.utils.successDialog
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AddImagesActivity : ScopedActivity(), ImagePickerDialog.FileSelectListener {
    private lateinit var addImagesAdapter: AddImagesAdapter
    private lateinit var binding: ActivityAddImagesBinding
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val images=ArrayList<File>()
    private val viewModel:AddImagesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_images)
        addImagesAdapter= AddImagesAdapter(images,{
            val dialog=ImagePickerDialog(this,this)
            dialog.show(supportFragmentManager,"")
        },{
            images.removeAt(it)
            addImagesAdapter.notifyDataSetChanged()
        })
        binding.rvAddImages.adapter=addImagesAdapter
        listeners()
        bindObserver()
    }

    private fun bindObserver() {
        viewModel.updateLiveData.observe(this){
            when(it){
                is ResultWrapper.Loading->showProgress()
                is ResultWrapper.Success->{
                    hideProgress()
                    sharedPrefs.saveUser(it.data.data)
                    sharedPrefs.setSubscribed(it.data.data.subscribed=="1")
                    startNewActivityWithFinish(WriteBio::class.java,null)
                }
                is ResultWrapper.Error->{
                    showToast(it.error?.message!!)
                }
            }
        }
    }

    private fun listeners() {
        binding.btNext.setOnClickListener {
            if (images.isEmpty()){
                showToast("Please add at least one image")
            }else{
                viewModel.updateImage(images.toMultipartList("img[]","image/*"))
            }
        }

        binding.ivBack.setOnClickListener {
            errorDialog("You are being redirected to the Login screen.Do you want to continue?",true){
                startActivity(Intent(this, GetStarted::class.java))
                finishAffinity()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onFileSelected(file: File) {
        lifecycleScope.launch {
            val compressedImageFile = Compressor.compress(this@AddImagesActivity, file){
                default()
            }
            images.add(compressedImageFile)
            withContext(Dispatchers.Main){
                addImagesAdapter.notifyDataSetChanged()
            }
        }
    }
}