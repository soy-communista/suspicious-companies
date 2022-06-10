package uz.sicnt.app.utils;

public class StringHelper {
    public static String get(Object obj){
        if(obj != null){

            try {
                return obj.toString();
            } catch (Throwable t){
                return null;
            }

        }
        return null;
    }

    public static String getNotNullAmong(String ... strs){
        for (String str : strs) {
            if(get(str) != null && !get(str).equals("")){
                return str;
            }
        }

        return null;
    }

    public static boolean isNotNullOrEmpty(String str){
        return get(str) != null && !get(str).equals("");
    }

    public static String padChar(Object obj, String tschar){
        if(get(obj) != null){

            try {
                String str = obj.toString();
                return (tschar + str).substring(str.length());
            } catch (Throwable t){
                return null;
            }

        }
        return null;
    }

}
