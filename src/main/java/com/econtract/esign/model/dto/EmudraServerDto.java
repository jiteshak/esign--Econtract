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
public class EmudraServerDto {
    private String ReturnStatus;
    private String ErrorMessage;
    private String Returnvalue;
    private String Transactionnumber;
    private String Referencenumber;
}
