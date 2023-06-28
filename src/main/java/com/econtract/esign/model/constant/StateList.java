/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author TS
 */
public class StateList {
    public static String MH = "MH";
    
    public static boolean isValid(String o){
        List<String> states = Arrays.asList("AN", "AP", "AR", "AS", "BR", "CG", "CH", "DN", "DD", "DL", "GA", "GJ", "HR", "HP", "JK", "JH", "KA", "KL", "LA", "LD", "MP", "MH", "MN", "ML", "MZ", "NL", "OR", "PY", "PB", "RJ", "SK", "TN", "TS", "TR", "UP", "UK", "WB");
        
        if(states.contains(o)){
            return true;
        }
        return false;
    }
}
