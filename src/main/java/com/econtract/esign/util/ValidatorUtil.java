/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.util;

import java.util.regex.Pattern;

/**
 *
 * @author TS
 */
public class ValidatorUtil {
    
    public static boolean isIfsc(String Ifsc){
        String regex = "^[A-Z]{4}0[A-Z0-9]{6}$";
        
        return Ifsc.matches(regex);
    }
    
    public static boolean isEmail(String email){
        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        
        return email.matches(regex);
    }
    
    public static boolean isMobile(String mobile){
        String regex = "[1-9][0-9]{9}";
        
        return mobile.matches(regex);
    }
    
    public static boolean isPan(String pan){
        String regex = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        
        return pan.matches(regex);
    }
    
    public static boolean isBankAccount(String accountNumber){
        String regex = "^\\d{9,18}$";
        
        return accountNumber.matches(regex);
    }
    
    public static boolean isUtilityCode(String utilityCode){
        String regex = "^\\d{9,18}$";
        Pattern p = Pattern.compile(regex);
        
        return utilityCode.matches(regex);
    }
    
    public static String cleanName(String name){
        if(name == null){
            return "";
        }
        
        return name.replaceAll("[^a-zA-Z0-9\\s,.'-]", "");
    }
}
