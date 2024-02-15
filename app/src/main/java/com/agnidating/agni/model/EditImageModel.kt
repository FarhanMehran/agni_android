package com.agnidating.agni.model

import com.agnidating.agni.model.profile.Images
import java.io.File

/**
 * Create by AJAY ASIJA on 05/03/2022
 */
data class EditImageModel(
    val image: Images?=null,
    val imageFile: File?=null,
)