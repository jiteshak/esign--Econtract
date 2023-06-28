/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.EsignRequest;
import com.econtract.esign.model.constant.EsignRequestStatus;
import java.time.LocalDateTime;
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
public class EsignRequestStatusDto {
    
    
    private Integer econRefId;
    private String documentRefNo;
    private String referenceId;
    
    
    private String econStatusCode;
    private String econStatusDesc;
    private Integer agreementDownloaded;
    private Integer documentCat;
    private Integer product;
    private Integer isStampPaperAttached;
    private Integer signOption;
    private LocalDateTime updatedOn;
    
    private String srcSystem;
    private String fileurl;
    private String entity;
    
    private String errorCode;
    private String message;
    
    public static EsignRequestStatusDto toDto(EsignRequest er, String sourceAgreementUrl){
        EsignRequestStatusDto ers = new EsignRequestStatusDto();
        
        
        ers.setEconRefId(er.getId());
        ers.setDocumentRefNo(er.getReferenceNumber1());
        ers.setReferenceId(er.getReferenceNumber2());
        ers.setEconStatusCode(er.getStatus() + "");
        ers.setEconStatusDesc(EsignRequestStatus.getStatusName(er.getStatus()));
        ers.setAgreementDownloaded(er.getIsAgreementDownloaded());
        ers.setDocumentCat(er.getDocumentCategoryId());
        ers.setProduct(er.getModuleId());
        ers.setIsStampPaperAttached(er.getIsStampAttached());
        ers.setSignOption(er.getSignOption());
        ers.setUpdatedOn(er.getModifiedDate());
        ers.setSrcSystem(er.getSource() != null ? er.getSource().getName() : "");
        ers.setFileurl(sourceAgreementUrl.replace("{id}", er.getId() + ""));
        ers.setEntity(er.getModule() != null && er.getModule().getEntity() != null ? er.getModule().getEntity().getName() : "");
        return ers;
    }
}
