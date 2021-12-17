package com.mita.fmipaschedule.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppHelper {

    public static String getDefaultImage(){
        return "https://firebasestorage.googleapis.com/v0/b/fmipa-schedule.appspot.com/o/users%2Fdefault.png?alt=media&token=4211c65a-c0f9-4bdb-8b78-7c1cf73f9a39";
    }

    public static boolean permissionFile(Activity activity){
        boolean GRANTED = false;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                GRANTED = true;
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0 );
            }
        }else{
            GRANTED = true;
        }
        return GRANTED;
    }

    public static boolean permissionCamera(Activity activity){
        boolean GRANTED = false;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                GRANTED = true;
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 0 );
            }
        }else{
            GRANTED = true;
        }
        return GRANTED;
    }

    public static String getStatusText(String status){
        if (status!=null) {
            switch (status) {
                case "waiting":
                    return "Menunggu Konfirmasi";
                case "process":
                    return "Dalam Proses";
                case "sending":
                    return "Pengiriman";
                case "success":
                    return "Selesai";
                case "cancel":
                    return "Di Batalkan";
                default:
                    return "Error";
            }
        }else{
            return "Selesai";
        }
    }

    public static String getFile(String url){
        String ret = "";
        String[] u = url.split("/");
        if (u.length>0){
            String[] p = u[u.length-1].split("g?alt=");
            if (p.length>0){
                String[] a = p[0].split("%2F");
                if (a.length>0){
                    ret = a[a.length-1];
                }
            }
        }
        return ret.replaceAll("[?]", "");
    }

    public static String printToJpg(View view){
        String YOUR_FOLDER_NAME = "The Pos";
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), YOUR_FOLDER_NAME);
        // Create a storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("FAILED", "Failed to create directory");
            }
        }

        // Create a media file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "IMG_" + timeStamp + ".jpg";

        String selectedOutputPath = mediaStorageDir.getPath() + File.separator + imageName;
        Log.d(YOUR_FOLDER_NAME, "selected camera path " + selectedOutputPath);

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());

        int maxSize = 1080;

        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();

//        if (bWidth > bHeight) {
//            int imageHeight = (int) Math.abs(maxSize * ((float)bitmap.getWidth() / (float) bitmap.getHeight()));
//            bitmap = Bitmap.createScaledBitmap(bitmap, maxSize, imageHeight, true);
//        } else {
//            int imageWidth = (int) Math.abs(maxSize * ((float)bitmap.getWidth() / (float) bitmap.getHeight()));
//            bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, maxSize, true);
//        }

        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        OutputStream fOut = null;
        try {
            File file = new File(selectedOutputPath);
            fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectedOutputPath;
    }

}
