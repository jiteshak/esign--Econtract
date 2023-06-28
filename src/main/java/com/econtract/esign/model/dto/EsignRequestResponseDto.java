/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.constant.EsignRequestStatus;
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
public class EsignRequestResponseDto {
    
    private Integer status;
    private Integer econRefId;
    
    private String econStatusCode;
    private String econStatusDesc;
    
    private String errorCode;
    private String message;
    
    public static EsignRequestResponseDto toDto(EsignRequest er){
        EsignRequestResponseDto ers = new EsignRequestResponseDto();
        
        ers.setStatus(1);
        ers.setEconRefId(er.getId());
        ers.setEconStatusCode(er.getStatus() + "");
        ers.setEconStatusDesc(EsignRequestStatus.getStatusName(er.getStatus()));
        return ers;
    }
}
