package io.github.salemlockwood.android.smscarmanager;

/**
 * Created by Melky on 30/01/2016.
 */
public class StringHelper {
    public static String strSeparator = "__,__";
    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }
    public static String convertArrayToString(Double[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+String.valueOf(array[i]);
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }
    public static Double[] convertStringToDoubleArray(String str){
        String[] arr = str.split(strSeparator);
        Double[] values = new Double[arr.length];
        int aux = 0;
        for(String a : arr){
            values[aux] = Double.valueOf(a);
            aux++;
        }
        return values;
    }
}
