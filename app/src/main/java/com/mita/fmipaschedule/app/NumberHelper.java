package com.mita.fmipaschedule.app;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NumberHelper {

    public static String rupiah(Double number){
        Locale locale = new Locale("in", "ID");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        return numberFormat.format((double) number);
    }

    public static String rupiah(String number){
        Locale locale = new Locale("in", "ID");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        return numberFormat.format(Double.parseDouble(number));
    }

    public static String timeFormat(long timestamp){
        long s = timestamp/1000;
        long m = s/60;
        long H = m/60;
        return H + ":" + (m - (H * 60));
    }

    public static String timeFormat(long start, long end, String split){
        return timeFormat(start) + split + timeFormat(end);
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateFormat(long timestamp){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date(timestamp));
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateFormat(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static int cleanInt(String string){
        if (string!=null) {
            if (string.length() > 0) {
                string = string.replaceAll("[^0-9]", "");
                if (string.length() > 0) {
                    return Integer.parseInt(string);
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        }else{
            return 0;
        }
    }

    public static int cleanInt(Object object){
        if (object!=null){
            return cleanInt(object.toString());
        }else{
            return 0;
        }
    }

}
