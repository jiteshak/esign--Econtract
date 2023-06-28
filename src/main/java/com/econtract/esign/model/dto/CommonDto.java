/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
public class CommonDto {
    
    private String message;
    private String data;
    
    public CommonDto(String m){
        message = m;
    }
    
    public CommonDto(String m, String d){
        message = m;
        data = d;
    }
    
}
