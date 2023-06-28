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
public class LoginRequestDto {
    
    private String username;
    
    private String password;

    public LoginRequestDto(String username) {
        this.username = username;
    }

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    
}
