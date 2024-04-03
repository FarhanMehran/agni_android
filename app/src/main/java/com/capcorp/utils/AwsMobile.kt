package com.capcorp.utils

import android.content.Context
import android.util.Log
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.capcorp.R
import java.io.File


/*
* Created by Rohit Sharma on 18/3/18
*
* */

class AwsMobile(val context: Context, val key: String) {

    private val TAG = AwsMobile::class.java.simpleName

    @Volatile
    private var S3_CLIENT_INSTANCE: AmazonS3Client? = null

    @Volatile
    private var TRANSFER_UTILITY_INSTANCE: TransferUtility? = null

    init {
        AWSMobileClient.getInstance().initialize(context).execute()
        setupAws()
        ///AWSMobileClient.getInstance().initialize(context).execute()
    }

    fun getS3ClientInstance(): AmazonS3Client {
        return S3_CLIENT_INSTANCE ?: synchronized(this) {
            S3_CLIENT_INSTANCE
                ?: AmazonS3Client(
                    BasicAWSCredentials(
                        context.getString(R.string.aws_key),
                        context.getString(R.string.aws_secret_key)
                    )
                ).apply {
                    setRegion(Region.getRegion(Regions.US_WEST_2))
                }.also { S3_CLIENT_INSTANCE = it }
        }
    }

    fun getTransferUtility(context: Context): TransferUtility {
        return TransferUtility.builder()
            .s3Client(getS3ClientInstance())
            .context(context.applicationContext)
            .defaultBucket("h2d-dev")
            .build()
    }


    fun getTransferUtilityInstance(context: Context): TransferUtility {
        return TRANSFER_UTILITY_INSTANCE ?: synchronized(this) {
            TRANSFER_UTILITY_INSTANCE ?: getTransferUtility(context)
                .also { TRANSFER_UTILITY_INSTANCE = it }
        }
    }

    private fun setupAws() {
        /*val credentials = BasicAWSCredentials(context.getString(R.string.aws_key), context.getString(R.string.aws_secret_key))
        s3Client = AmazonS3Client(credentials)
        transferUtility = TransferUtility.builder()
                .context(context)
                .awsConfiguration(AWSMobileClient.getInstance().configuration)
                //.s3Client(AmazonS3Client(AWSMobileClient.getInstance().credentialsProvider))
                    .s3Client(s3Client)
                .build()*/
        //getTransferUtilityInstance(context)
    }


    fun uploadWithTransferUtility(key: String, file: File) {
        getTransferUtilityInstance(context)
        val uploadObserver = TRANSFER_UTILITY_INSTANCE?.upload(key, file)
        uploadObserver?.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED === state) {
                    Log.e(TAG, "File uploaded successfully")
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentDoneF = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percentDoneF.toInt()

                Log.d(
                    "YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                            + " bytesTotal: " + bytesTotal + " " + percentDone + "%"
                )
            }

            override fun onError(id: Int, ex: Exception) {
                Log.e(TAG, ex.toString())
            }

        })

//        // If you prefer to poll for the data, instead of attaching a
//        // listener, check for the state and progress in the observer.
//        if (TransferState.COMPLETED === uploadObserver.state) {
//            // Handle a completed upload.
//        }
//
//        Log.d("YourActivity", "Bytes Transferrred: " + uploadObserver.bytesTransferred)
//        Log.d("YourActivity", "Bytes Total: " + uploadObserver.bytesTotal)
    }


    fun uploadFile(key: String, file: File, transferListener: TransferListener): Int {
        /*if(!::TRANSFER_UTILITY_INSTANCE.isInitialized) {
            setupAws()
        }*/
        getTransferUtilityInstance(context)
        val uploadObserver = TRANSFER_UTILITY_INSTANCE?.upload(key, file)
        uploadObserver ?: return 0
        uploadObserver.setTransferListener(transferListener)
        return uploadObserver.id
    }

    fun cancelUploading(transferId: Int?) {
        transferId?.let { TRANSFER_UTILITY_INSTANCE?.cancel(it) }
    }

}