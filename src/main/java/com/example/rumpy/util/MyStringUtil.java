package com.example.rumpy.util;

import java.util.Random;

public class MyStringUtil {
    public static final String STRING_LIST_SEPARATOR = ";;";

    public static String generateRandom(){
        return generateRandom(15);
    }//end method generate

    public static String generateRandom(int count){
        int leftLimit = 48; // numeric '0'
        int rightLimit = 122; // letter 'z'

        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >=97))
                .limit(count)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }//end method generate
}//end class MyStringUtil
