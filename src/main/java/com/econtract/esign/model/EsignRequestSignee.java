/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

import com.econtract.esign.util.ValidatorUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name="econ_signees")
public class EsignRequestSignee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "es_id")
    private Integer id;
    
    @NotNull
    @Column(name = "ec_id")
    private Integer esignRequestId;
    
        
    @NotNull
    @Column(name="es_applname")
    private String applicantName;
    
    @NotNull
    @Column(name="es_contact")
    private String applicantContact;
    
    @Column(name="es_telephone")
    private String applicantTelephone;
    
    @NotNull
    @Column(name="es_email")
    private String applicantEmail;
    
    @NotNull
    @Column(name="es_address")
    private String applicantAddress;
    
    
    @NotNull
    @Column(name="es_seq", columnDefinition = "SMALLINT")
    private Integer sequence;
    
    @NotNull
    @Column(name="es_status", columnDefinition = "SMALLINT")
    private Integer status = 0;//unsigned = 0, signed = 1
    
    
    @JsonIgnore
    @NotNull
    @Column(name="es_token")
    private String token;
    
    
    @Column(name="es_signoption", length = 2)
    private Integer signOption = 0;
    
    @Column(name="es_last_txnid")
    private String transactionId;
    
    @Column(name="es_ip")
    private String ip;
        
    @LastModifiedBy
    @Column(name = "es_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "es_moddt")
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "ec_id", insertable = false, updatable = false)
    private EsignRequest esignRequest;

    
    public Integer getSignOption(){
        if(this.signOption == null){
            return 0;
        }
        
        return this.signOption;
    }
    
    
    public void setApplicationName(String applicantName){
        this.applicantName = ValidatorUtil.cleanName(applicantName);
    }
    
    public void setApplicantEmail(String applicantEmail){
        if(applicantEmail != null){
            applicantEmail = applicantEmail.toLowerCase();
        }
        this.applicantEmail = applicantEmail;
    }
    
    @Override
    public String toString() {
        return "EContract  Esign Request Signeer [id=" + id + ",application id =" + esignRequestId + ",applicantName=" + applicantName + "]";
    }

    
}
