/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.constant;

/**
 *
 * @author TS
 */
public class ClientType {
    public static int END_CUSTOMER = 1;
    public static int DSA = 2;
    public static int RP = 3;
    public static int CONNECTOR = 4;
    
    
    
    public static boolean isValid(int o){
        if(END_CUSTOMER == o || DSA == o || RP == o || CONNECTOR == o){
            return true;
        }
        return false;
    }
}
