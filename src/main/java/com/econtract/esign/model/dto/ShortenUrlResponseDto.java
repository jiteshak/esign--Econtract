/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ShortenUrlResponseDto {
    @JsonProperty("ReturMessage")
    String returMessage;
    
    @JsonProperty("ReturnCode")
    String returnCode;
    
    @JsonProperty("URLReturned")
    String urlReturned;

    
}
