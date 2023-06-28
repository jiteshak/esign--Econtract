package com.econtract.esign.util;

import java.security.SecureRandom;
import org.springframework.util.DigestUtils;
import java.util.Random;

public class PasswordUtil {

    public PasswordUtil(){

    }

    public  static String generateOtp(){
        Random rand = new Random();

        int  n = rand.nextInt(99999) + 100000;

        return n + "";
    }

    public static String hash(String password){
        StringBuilder hexString = new StringBuilder();
        return DigestUtils.md5DigestAsHex(salt(password).getBytes());
    }
    
    public static String hash2(String data){
        StringBuilder hexString = new StringBuilder();
        return DigestUtils.md5DigestAsHex(data.getBytes());
    }


    public static String salt(String password){
        return "salt_"+ password +"_esign_hard";
    }
    
    public static String generateOpaque(String userId){
        SecureRandom random = new SecureRandom();
        long longToken1 = Math.abs( random.nextLong() );
        long longToken2 = Math.abs( random.nextLong() );
        String randomString = Long.toString( longToken1, 30 ) + Long.toString( longToken2, 30 ) + userId;
        randomString = randomString.replace("/", "").replace("\\", "");
        return randomString;
    }
}
