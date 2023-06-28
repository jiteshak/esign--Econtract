/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

import com.econtract.esign.model.constant.EsignProcessSettings;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
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
@Entity
@Audited
@Table(name="eproc_hdr")
public class EsignProcess {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ep_id")
    private Integer id;
    
    @Column(name="md_id")
    private Integer moduleId;
    
    @Column(name="doc_id")
    private Integer documentCategoryId;
    
    
    @Column(name="src_id")
    private Integer sourceId;
    
    @Column(name="ep_clientpe", columnDefinition = "SMALLINT")
    private Integer clientType;
    
    
    @Column(name="ep_signoption", columnDefinition = "SMALLINT")
    private Integer signOption;
    
    
    @Column(name="ep_officesign", columnDefinition = "SMALLINT")
    private Integer isSignatorySignRequired = 1;
    
    @Column(name="ep_customersign", columnDefinition = "SMALLINT")
    private Integer isCuctomerSignRequired = 1;
    
//    [
//        {    “allocateStampPaper”: “FALSE”,              
//            “termsAndConditions”: “FALSE”,
//            “annexureI”: “EXTERNAL”,
//            “stampPaperAnnexure”: “FALSE”                
//        }   
//    ]
//      allocateStampPaper - TRUE/FALSE
//      termsAndConditions - SYSTEM/EXTERNAL/FALSE
//      annexureI - SYSTEM/EXTERNAL
//      stampPaperAnnexure - TRUE/FALSE
    @JsonIgnore
    @Column(name="ep_txnproc")
    private String process;
    
    @Transient
    private EsignProcessSettings processSettings;
    
    @NotNull
    @Column(name="ep_active", columnDefinition = "SMALLINT")
    private Integer isActive = 1;
    
    
    @LastModifiedBy
    @Column(name = "ec_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "ec_moddt")
    private LocalDateTime modifiedDate;
    
    
    public EsignProcessSettings getProcessSettings(){
        if(this.processSettings == null && this.process != null){
            setProcessSettings();
        }
        
        if(this.processSettings != null){
            return this.processSettings;
        }
        
        //if not found
        EsignProcessSettings eps = new EsignProcessSettings();
        return eps;
    }
    
    public void setProcessSettings(EsignProcessSettings eps){
        ObjectMapper om = new ObjectMapper();
        try {
            String process = om.writeValueAsString(eps); 
            this.setProcess(process);
        }catch (Exception e) {} 
    }
    
    public void setProcessSettings(){
        ObjectMapper om = new ObjectMapper();
        try {
            this.processSettings = om.readValue(this.process, EsignProcessSettings.class);
        }catch (Exception e) {} 
    }
    
    
    public void setProcess(String process){
        this.process = process;
        this.setProcessSettings();
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString(){
        return "Process [id = "+ id +",process = "+ process +", moduleId = "+ moduleId +", doc_id = "+ documentCategoryId +", src = "+ sourceId +", client = "+ clientType +" isSignatorySignRequired = "+ isSignatorySignRequired +" isCustomerSignRequired = "+ isCuctomerSignRequired +"]";
    }
}
