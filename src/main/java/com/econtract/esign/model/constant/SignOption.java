/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.constant;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TS
 */
public class SignOption {
    public static int SYSTEM_DEFAULT = 0;
    public static int ALL = 1;
    public static int OTP = 2;
    public static int AADHAAR = 3;
    
    
    public static boolean isValid(int o){
        if(SYSTEM_DEFAULT == o || ALL == o || OTP == o || AADHAAR == o){
            return true;
        }
        return false;
    }
    
    public static boolean isMobile(int o){
        if(ALL == o || OTP == o){
            return true;
        }
        return false;
    }
    
    public static boolean isAadhaar(int o){
        if(ALL == o || AADHAAR == o){
            return true;
        }
        return false;
    }
}
