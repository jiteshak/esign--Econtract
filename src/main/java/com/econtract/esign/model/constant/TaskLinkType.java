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
public class TaskLinkType {
    public static int SOURCE_ID = 1;
    public static int ESIGN_REQUEST_ID = 2;
    
    
    public static String toString(int id){
        if(ESIGN_REQUEST_ID == id){
            return "Esign Request";
        }
        
        return "Source Id";
    }
    
}
