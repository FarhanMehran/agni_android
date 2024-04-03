package com.capcorp.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.capcorp.BuildConfig;
import com.capcorp.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Utility {
    private Fragment frag;
    private final Activity act;
    private String campath;
    private String mediaType;
    private String IMAGENAME = "imageName";
    private final PassValues passValues;

    public Utility(Activity activity, String mediaType) {
        this.act = activity;
        this.passValues = (PassValues) activity;
        this.mediaType = mediaType;
    }


    public Utility(Fragment fragment, Activity activity, String mediaType) {
        this.act = activity;
        this.passValues = (PassValues) fragment;
        frag = fragment;
        this.mediaType = mediaType;
    }


    public Utility(Fragment fragment, Activity activity) {
        this.act = activity;
        passValues = (PassValues) fragment;
        frag = fragment;
    }

    private String getDate(long time) {
        Date date = new Date(time * 1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy ", Locale.getDefault()); // the format of your date
        return sdf.format(date);

    }

    public void selectImage() {
        switch (mediaType) {
            case UtilityConstants.CAMERA:
                camera();
                break;
            case UtilityConstants.GALLERY:
                gallery();
                break;
        }
    }

    private void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), UtilityConstants.PATH);

        campath = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
        IMAGENAME = campath;
        if (!dir.exists())
            dir.mkdirs();

        Uri photoURI = FileProvider.getUriForFile(act, BuildConfig.APPLICATION_ID + ".provider",
                new File(dir.getAbsolutePath(), campath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        if (frag instanceof Fragment) {
            frag.startActivityForResult(intent, UtilityConstants.PHOTO_REQUEST_CAMERA);
        } else {
            act.startActivityForResult(intent, UtilityConstants.PHOTO_REQUEST_CAMERA);
        }
    }

    private void gallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (frag instanceof Fragment) {
            frag.startActivityForResult(intent, UtilityConstants.PHOTO_REQUEST_GALLERY);
        } else {
            act.startActivityForResult(intent, UtilityConstants.PHOTO_REQUEST_GALLERY);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), UtilityConstants.PATH);
        if (!dir.exists())
            dir.mkdirs();
        switch (requestCode) {

            case 69:
                if (data != null && resultCode == Activity.RESULT_OK) {
                    File tempFile = new File(dir.getAbsolutePath(), IMAGENAME);
                    System.out.println("" + tempFile);
                    passValues.passImageURI(tempFile, Uri.parse("file://" + tempFile.getAbsolutePath()));
                }
                break;

            case UtilityConstants.PHOTO_REQUEST_GALLERY:

                if (data != null && resultCode == Activity.RESULT_OK) {
                    Log.d("authority", data.getData().getAuthority() + "");
                    campath = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
                    IMAGENAME = campath;
                    File destFile = new File(dir.getAbsolutePath(), campath);
                    if (data.getData().getAuthority().equals("com.google.android.apps.photos.contentprovider")) {
                        File image = null;
                        try {
                            image = new File(dir, UtilityConstants.IMAGES_PREFIX + Calendar.getInstance().getTimeInMillis()
                                    + UtilityConstants.IMAGES_SUFFIX);
                            FileOutputStream fOut = new FileOutputStream(image);
                            Bitmap googleBitmap = getBitmapFromUri(act, data.getData());
                            googleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            copyFile(image, destFile);
                            Uri photoURI = Uri.parse("file://" + destFile.getAbsolutePath());
                            customCrop(photoURI);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        File sourceFile = new File(getRealPathFromURI(data.getData()));
                        try {
                            copyFile(sourceFile, destFile);
                            Uri photoURI = Uri.parse("file://" + destFile.getAbsolutePath());
                            customCrop(photoURI);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case UtilityConstants.PHOTO_REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    if (UtilityConstants.IMAGENAME != null) {
                        File tempFile = new File(dir.getAbsolutePath(), IMAGENAME);

                        Uri uri = Uri.parse("file://" + tempFile.getAbsolutePath());
                        Uri photoURI = FileProvider.getUriForFile(act, BuildConfig.APPLICATION_ID + ".provider", tempFile);
                        //todo call utility function
                        customCrop(uri);
                    }
                }
                break;

            default:
                break;
        }
    }

    private void customCrop(Uri photoURI) {
        try {
            UCrop.Options options = new UCrop.Options();
            options.setCropFrameColor(ContextCompat.getColor(act, R.color.colorPrimary));
            options.setShowCropGrid(false);
            options.setToolbarColor(ContextCompat.getColor(act, R.color.colorPrimary));
            options.setHideBottomControls(true);
            if (frag instanceof Fragment) {
                UCrop.of(photoURI, photoURI)
                        .withAspectRatio(12, 12)
                        .withMaxResultSize(600, 600)
                        .withOptions(options)
                        .start(act, frag);
            } else {
                UCrop.of(photoURI, photoURI)
                        .withAspectRatio(12, 12)
                        .withMaxResultSize(600, 600)
                        .withOptions(options)
                        .start(act);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromUri(Activity activity, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                activity.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        FileChannel source = new FileInputStream(sourceFile).getChannel();
        FileChannel destination = new FileOutputStream(destFile).getChannel();
        if (source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        destination.close();
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = act.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String s = cursor.getString(idx);
            cursor.close();
            return s;
        }

    }

    public interface PassValues {
        void passImageURI(File file, Uri photoURI);
    }

}