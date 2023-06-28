/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.EsignProcess;
import com.econtract.esign.model.StampPaper;
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
public class EsignProcessResponse {
    
    private EsignProcess esignProcess;
    private List<StampPaper> stampPapers;
    
    
    public EsignProcessResponse(EsignProcess esignProcess, List<StampPaper> stampPapers){
        this.esignProcess = esignProcess;
        this.stampPapers = stampPapers;
    }
    
}
