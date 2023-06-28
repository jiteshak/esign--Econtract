/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

import com.econtract.esign.util.ValidatorUtil;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
@Table(name="econ_txn")
public class EsignRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ec_id")
    private Integer id;
    
    @NotNull
    @Column(name="ec_refno1")
    private String referenceNumber1;
    
    
    @NotNull
    @Column(name="ec_refno2")
    private String referenceNumber2;
    
    @NotNull
    @Column(name="md_id")
    private Integer moduleId;
    
    @NotNull
    @Column(name="ec_date")
    private Date date;
    
    @NotNull
    @Column(name="ec_sancamt")
    private Double sanctionAmount;
    
    @Column(name="ec_location")
    private String location;
    
    
//    @NotNull
    @Column(name="ec_state", length = 2)
    private String state;
    
    @NotNull
    @Column(name="ec_applname")
    private String applicantName;
    
    @NotNull
    @Column(name="ec_contact")
    private String applicantContact;
    
    @Column(name="ec_telephone")
    private String applicantTelephone;
    
    @NotNull
    @Column(name="ec_email")
    private String applicantEmail;
    
    @NotNull
    @Column(name="ec_address")
    private String applicantAddress;
    
    @Column(name="ec_applsignoption")
    private Integer applicantSignOption = 0;
    
    
//    1 - Individual
//    2 - HUF
//    3 - Proprietary,
//    4 - Partnership, Company,
//    5 - Trust, 
//    6 - Proprietary
//    7 - LLP
//    8 - Other
    @NotNull
    @Column(name="ec_applstatus", columnDefinition = "SMALLINT")
    private Integer applicantionStatus;
    
    
//    1 - New
//    2 - Initiated
//    3 - Signed by Customer
//    4 - Signed by Business Signatory
//    5 - Printed
//    6 - Dispatched
//    7 - Completed
//    8 - Cancelled
    @NotNull
    @Column(name="st_id", columnDefinition = "SMALLINT")
    private Integer status;
    
//    0 - System default 
//    1- All options (Currently Aadhar & OTP - tomorrow could be any additional aspect)
//    2. OTP
//    3. Aadhar
    @Column(name="ec_signoption", columnDefinition = "SMALLINT")
    private Integer signOption = 2;
    
//    1 - Signature of ABFL Signatory required
//    0 - Signature of ABFL Signatory not required
    @Column(name="ec_officesign", columnDefinition = "SMALLINT")
    private Integer isSignatorySignRequired = 1;
    
//    1 - Signature of ABFL Signatory first
//    0 - Signature of Customer first
    @Column(name="ec_officesign_first", columnDefinition = "SMALLINT")
    private Integer officeSignFirst = 0;
    
    @Column(name="ec_officesign_rqdcnt", columnDefinition = "SMALLINT")
    private Integer numberOfOfficeSign = 1;
    
//    1 - Signature of Customer required
//    0 - Signature of Customer not required
    @Column(name="ec_customersign", columnDefinition = "SMALLINT")
    private Integer isCustomerSignRequired = 1;
    
    @NotNull
    @Column(name="ec_active", columnDefinition = "SMALLINT")
    private Integer isActive = 1;
    
    @Column(name="ec_file")
    private String file;
    
    
    @NotNull
    @Column(name="ec_token")
    private String token;
    
//    Client Type
//1 - End Customer
//2 - DSA
//3 - RP
//4 - Connector
    @Column(name="ec_clientpe", columnDefinition = "SMALLINT")
    private Integer clientType;
    
    
    @Column(name="sr_id", columnDefinition = "SMALLINT")
    private Integer sourceId;
    
    
    @Column(name="doc_id")
    private Integer documentCategoryId;
    
    
    @Column(name="br_id")
    private Integer branchId;
    
    @LastModifiedBy
    @Column(name = "ec_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "ec_moddt")
    private LocalDateTime modifiedDate;

    
    @Column(name = "ec_download" , length = 1)
    private Integer isAgreementDownloaded = 0;
    
    
    @Column(name = "ec_stamp" , length = 1)
    private Integer isStampAttached = 0;
    
    
    //this is the override to eproc_hdr settigs
    //0 - system
    //1 - stamp not allocat
    //2 - stamp allocat
    @Column(name = "ec_autoallocstamp" , length = 1)
    private Integer autoAllocateStampPaper = 0;
    
    public Integer getId() {
        return id;
    }
    
    public Integer getIsAgreementDownloaded(){
        if(isAgreementDownloaded == null){
            return 0;
        }
        return isAgreementDownloaded;
    }
    
    public Integer getAutoAllocateStampPaper() {
        if(autoAllocateStampPaper == null){
            return 0;
        }
        return autoAllocateStampPaper;
    }
    
    public Integer getIsStampAttached() {
        if(isStampAttached == null){
            return 0;
        }
        
        return isStampAttached;
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
    
    @ManyToOne
    @JoinColumn(name = "br_id", insertable = false, updatable = false)
    private Branch branch;
    
    
    @ManyToOne
    @JoinColumn(name = "sr_id", insertable = false, updatable = false)
    private Source source;
    
    @ManyToOne
    @JoinColumn(name = "md_id", insertable = false, updatable = false)
    private Module module;
    
    @ManyToOne
    @JoinColumn(name = "doc_id", insertable = false, updatable = false)
    private DocumentCategory documentCategory;
    
    @OneToMany
    @JoinColumn(name = "ec_id", insertable = false, updatable = false)
    private List<EsignRequestSignee> signees;
    
    
    @PrePersist
    @PreUpdate
    public void preUpdate() {
        modifiedDate = LocalDateTime.now();
//        modifiedBy = LoggedUser.get();
    }
    
    
    @Override
    public String toString() {
        return "EContract Esign Request [id=" + id + ", reference=" + referenceNumber1 + ", applicantName=" + applicantName + ", status=" + status + "]";
    }
}
