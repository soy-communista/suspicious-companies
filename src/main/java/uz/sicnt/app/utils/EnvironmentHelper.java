package uz.sicnt.app.utils;

import org.springframework.core.env.Environment;

public class EnvironmentHelper {

    public static Boolean get(Environment environment, String env){
        return
                environment != null &&
                        environment.getProperty("spring.profiles.active", "default") != null &&
                        environment.getProperty("spring.profiles.active", "default").equals(env);
    }
}
