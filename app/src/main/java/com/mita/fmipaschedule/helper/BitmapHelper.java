package com.mita.fmipaschedule.helper;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;

public class BitmapHelper {
    private Bitmap bitmap;

    public BitmapHelper(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public void setWidth(int newWidth){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newHeight = height / (width/newWidth);
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    public void setQuality(int quality){
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, new ByteArrayOutputStream());
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

}
