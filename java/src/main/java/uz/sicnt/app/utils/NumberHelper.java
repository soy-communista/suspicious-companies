package uz.sicnt.app.utils;

import java.math.BigDecimal;

public class NumberHelper {
    public static Integer parseInt(Object obj){
        if(obj != null){

            try {
                return Integer.parseInt(obj.toString());
            } catch (Throwable t){
                return null;
            }

        }
        return null;
    }

    public static Long parseLong(Object obj){
        if(obj != null){
            try {
                return Long.parseLong(obj.toString());
            } catch (Throwable t){
                return null;
            }
        }
        return null;
    }

    public static BigDecimal parseBigDecimal(Object obj){
        if(obj != null){

            try {
                return
                        new BigDecimal(obj.toString());
            } catch (Throwable t){
                return null;
            }

        }
        return null;
    }

}
