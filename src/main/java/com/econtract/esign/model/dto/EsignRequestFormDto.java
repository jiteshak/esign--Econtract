/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.constant.EsignRequestStatus;
import java.util.ArrayList;
import java.util.Date;
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
public class EsignRequestFormDto {
    private int id;
    private String documentRefNo;
    private String referenceId;
    
    
    private Integer entityId = 0;
    private String entity;
    
    private Integer documentCategoryId = 0;
    private Integer documentCategory; //code
    
    private Integer clientType;
    
    private Integer productId = 0;
    private String product;
    
    private Date agreementDate;
    private double sanctionAmount;
    private String location;
    
    private Integer branchId = 0;
    private String branch;
    
    private Integer applicationStatus;
    private Integer status = EsignRequestStatus.NEW;
    
    private String firmName;
    private String firmEmail;
    private String firmAddress;
    private String firmTelephone;
    private String firmMobile;
    
    private String applicantName;
    private String applicantEmail;
    private String applicantMobile;
    private String applicantTelephone;
    private String applicantAddress;
    private Integer applicantSignOption;
    
    private List<EsignRequestApplicantFormDto> coApplicants = new ArrayList<>();
    
    private Integer signOption;
    private Integer sourceId = 0;
    private String source;
    
    private Integer isAgreementDownloaded = 0;
    
    private Integer autoAllocateStampPaper = 0;
    private Integer isStampPaperAttached;
    
    private List<StampPaperDto> stampPapers = new ArrayList<>();
    
    public void addCoApplicant(EsignRequestApplicantFormDto erar){
        this.coApplicants.add(erar);
    }
    
    public void addStampPaper(StampPaperDto spr){
        this.stampPapers.add(spr);
    }
}
