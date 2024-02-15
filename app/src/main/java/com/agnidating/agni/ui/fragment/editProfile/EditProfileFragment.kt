package com.agnidating.agni.ui.fragment.editProfile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentEditProfileBinding
import com.agnidating.agni.model.EditImageModel
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.checkNetwork
import com.agnidating.agni.utils.custom_view.ImagePickerDialog
import com.agnidating.agni.utils.successDialog
import com.agnidating.agni.utils.toMultipartList
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


/**
 * Created by AJAY ASIJA
 */
@AndroidEntryPoint
class EditProfileFragment : ScopedFragment(), ImagePickerDialog.FileSelectListener {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var addImagesAdapter: EditImagesAdapter
    private val args:EditProfileFragmentArgs by navArgs()
    private val list=ArrayList<EditImageModel>()
    private val addedImages=ArrayList<File>()
    private val viewModel:EditProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_profile,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        listeners()
        bindObserver()
    }

    /**
     * Initialize view
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        if (args.images!=null){
            args.images!!.forEach {
                list.add(EditImageModel(image = it))
            }
        }
        addImagesAdapter = EditImagesAdapter(list){position,event->
            if (event==EditImagesAdapter.EVENT_ADD_IMAGE){
                val dialog= ImagePickerDialog(requireContext(),this)
                dialog.show(parentFragmentManager,"")
            }else{
                val item=list[position].image
                viewModel.deleteImage(item!!.id,position)
            }
        }
        binding.rvAddImages.adapter = addImagesAdapter


    }
    /**
     * handle all listeners here
     */
    private fun listeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btNext.setOnClickListener {
            if (checkNetwork()){
               if(addedImages.isEmpty()){
                   binding.ivBack.performClick()
               }else{
                   viewModel.editProfile(
                       addedImages.toMultipartList("img[]","image/*"),
                   )
               }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onFileSelected(file: File) {
        viewLifecycleOwner.lifecycleScope.launch {
            val compressedImageFile = Compressor.compress(requireContext(), file){
                default()
            }
            addedImages.add(compressedImageFile)
            list.add(EditImageModel(imageFile = compressedImageFile))
            withContext(Dispatchers.Main){
                addImagesAdapter.notifyDataSetChanged()
            }
        }
    }


    /**
     * bind all live data observers
     */
    private fun bindObserver() {
        viewModel.editLiveData.observe(viewLifecycleOwner){
            when(it){
                is ResultWrapper.Loading->{
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success->{
                    (requireActivity() as ScopedActivity).hideProgress()
                    successDialog(it.data.message.toString()){
                        binding.ivBack.performClick()
                    }
                }
                is ResultWrapper.Error->{
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
        viewModel.deleteResponse.observe(viewLifecycleOwner){
            when(it){
                is ResultWrapper.Loading->{
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success->{
                    (requireActivity() as ScopedActivity).hideProgress()
                     list.removeAt(it.data.position!!)
                     addImagesAdapter.notifyItemRemoved(it.data.position!!)
                }
                is ResultWrapper.Error->{
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }

}