package uz.sicnt.app.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateHelper {
    public static String getDateTime(Object obj){

        if(obj != null){

            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(
                    (Date) obj
                );
            } catch (Throwable t){
                return null;
            }

        }

        return null;

    }

    public static String getDate(Object obj){
        if(obj != null){
            try {
                return new SimpleDateFormat("yyyy-MM-dd").format(
                    (Date) obj
                );
            } catch (Throwable t){
                return null;
            }
        }
        return null;
    }

    public static String getDateWithFormat(Object obj, String format){
        if(obj != null){
            try {
                return new SimpleDateFormat(format).format(
                    (Date) obj
                );
            } catch (Throwable t){
                return null;
            }
        }
        return null;
    }

    public static String getLocalDateWithFormat(LocalDate localDate, String format){
        if(localDate != null){
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return localDate.format(formatter);
            } catch (Throwable t){
                return null;
            }
        }
        return null;
    }

    public static Date parseDateWithFormat(String dateStr, String formatStr){
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);

        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public static String convertFormat(String date_str, String initial_format, String target_format){
        try{
            return
                new SimpleDateFormat(target_format).format(
                    new SimpleDateFormat(initial_format).parse(date_str)
                );
        }catch (Exception ex){
            return null;
        }
    }

}
