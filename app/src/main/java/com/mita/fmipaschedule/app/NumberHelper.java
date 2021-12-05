package com.mita.fmipaschedule.app;

import java.text.NumberFormat;
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
