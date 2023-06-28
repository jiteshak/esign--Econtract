/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.EsignRequestSignee;
import com.econtract.esign.model.EsignRequestStampPaperLink;
import com.econtract.esign.model.StampPaper;
import java.util.ArrayList;
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
public class EsignRequestUploadDto {
    private EsignRequest esignRequest;
    private List<EsignRequestSignee> esignRequestSignees;
    private List<StampPaper> stampPapers;
    private List<EsignRequestStampPaperLink> stampPaperLinks;
    
    public void addEsignRequestSignees(EsignRequestSignee esignRequestSignees) {
        if(this.esignRequestSignees == null){
            this.esignRequestSignees = new ArrayList<EsignRequestSignee>();
        }
        this.esignRequestSignees.add(esignRequestSignees);
    }
    
}
