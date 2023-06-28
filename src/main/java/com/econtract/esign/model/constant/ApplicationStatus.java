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
public class ApplicationStatus {
    public static int INDIVIDUAL = 1;
    public static int HUF = 2;
    public static int PROPRIETORY = 3;
    public static int PARTNERSHIP = 4;
    public static int TRUST = 5;
    public static int COMPANY = 6;
    public static int LLP = 7;
    public static int OTHER = 8;
    
    
    public static boolean isValid(int o){
        if(INDIVIDUAL == o || HUF == o || PROPRIETORY == o || PARTNERSHIP == o
                || TRUST == o || COMPANY == o || LLP == o || OTHER == o){
            return true;
        }
        return false;
    }
}
