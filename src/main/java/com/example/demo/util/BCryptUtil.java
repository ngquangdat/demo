package com.example.demo.util;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtil {
    public static String hash(String data){
        return BCrypt.hashpw(data, BCrypt.gensalt(13));
    }

    public static boolean check(String data, String hash){
        return BCrypt.checkpw(data, hash);
    }
}
