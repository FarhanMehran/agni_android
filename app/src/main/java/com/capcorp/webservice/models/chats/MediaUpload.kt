package com.capcorp.webservice.models.chats

import com.capcorp.utils.MediaUploadStatus
import java.io.File

data class MediaUpload(
    var mediaUploadStatus: String = MediaUploadStatus.UPLOADED,
    var transferId: Int? = -1,
    var file: File?
)