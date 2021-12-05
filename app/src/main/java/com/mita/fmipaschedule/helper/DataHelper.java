package com.mita.fmipaschedule.helper;

public class DataHelper {

    public static String phoneNumber(String phone){
        String p;
        if (phone.startsWith("08")){
            p = "+62"+phone.substring(1);
        }
        else if (phone.startsWith("62")){
            p = "+"+phone;
        }
        else if (phone.startsWith("8")){
            p = "+62"+phone;
        }
        else {
            p = phone;
        }
        return p;
    }

    public static String phoneClean(String phone){
        String ph = DataHelper.phoneNumber(phone);
        return ph.replace("+", "u");
    }

    public static boolean isStrongPassword(String inputPassword){
        int upper=0, lower=0, number=0;
        for (int i = 0; i < inputPassword.length(); i++){
            if (Character.isLowerCase(inputPassword.charAt(i))){
                lower = 1;
            }
            if (Character.isUpperCase(inputPassword.charAt(i))){
                upper = 1;
            }
            if (Character.isDigit(inputPassword.charAt(i))){
                number = 1;
            }
        }
        return (upper + lower + number) >= 2 && inputPassword.length() > 8;
    }
}
