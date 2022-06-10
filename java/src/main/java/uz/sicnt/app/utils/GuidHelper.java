package uz.sicnt.app.utils;

import java.util.UUID;

public class GuidHelper {

    public static String get(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
