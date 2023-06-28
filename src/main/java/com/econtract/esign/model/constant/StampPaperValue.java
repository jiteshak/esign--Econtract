/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.constant;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author TS
 */
public class StampPaperValue {
    
    // this list should be in desc order
    static List<Integer> papers = Arrays.asList(10000, 5000, 1000, 500, 400, 300, 200, 100, 50, 20, 10, 5, 2, 1);

    public static List<Integer> getList(){
        return papers;
    }
    
    public static Integer getNextLowValue(int v){
        int n = 1;
        for(int i = 0; i < papers.size();i++){
            if(v > papers.get(i)){
                n = papers.get(i);
                break;
            }
        }
        
        return n;
    }
}
