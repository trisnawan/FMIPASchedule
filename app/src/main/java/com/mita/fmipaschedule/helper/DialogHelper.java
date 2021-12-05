package com.mita.fmipaschedule.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class DialogHelper {

    public static void snackBar(View view, String message){
        Snackbar.make(view, message, BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    public static void snackBar(View view, String message, boolean isIndent){
        if (isIndent) {
            Snackbar.make(view, message, BaseTransientBottomBar.LENGTH_LONG).show();
        }else{
            Snackbar.make(view, message, BaseTransientBottomBar.LENGTH_INDEFINITE).show();
        }
    }

    public static void alert(Activity activity, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message).setTitle(title);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void toastShort(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String throwableHelper(Throwable throwable){
        if (throwable!=null) {
            throwable.printStackTrace();
            Log.e("Throwable", throwable.getMessage());
            return throwable.getLocalizedMessage();
        }else{
            return "";
        }
    }

}
