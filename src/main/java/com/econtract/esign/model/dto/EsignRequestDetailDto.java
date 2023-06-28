/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.model.EsignRequestSignerLog;
import java.util.List;
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
public class EsignRequestDetailDto {
    private EsignRequestDetailsDto2 esignRequest;
    private int currentUser;
    private List<EsignRequestSigneeDto> esignRequestSignees;
    private List<EsignRequestSignerLog> esignRequestSignerLogs;
    
    
    
    public void setEsignRequest(EsignRequest er){
        this.esignRequest = EsignRequestDetailsDto2.toDto(er);
    }
    
    public void setEsignRequest(EsignRequest er, List<EsignRequestSignee> ers){
        this.esignRequest = EsignRequestDetailsDto2.toDto(er);
        this.esignRequestSignees = EsignRequestSigneeDto.toDtoList(ers);
    }
    
}
