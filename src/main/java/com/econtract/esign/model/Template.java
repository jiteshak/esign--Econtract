/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Entity
@Audited
@Table(name="tmpl_hdr")
@Getter
@Setter
@NoArgsConstructor
public class Template {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="tm_id")
    private Integer id;
    
    @Column(name="md_id")
    private Integer moduleId;
    
    
    @Column(name="md_clientpe")
    private Integer clientType;
    
    @Column(name="doc_id")
    private Integer documentCategoryId;
    
    @NotNull
    @Column(name="tm_doc", columnDefinition = "TEXT", length = 5000)
    private String document;
    
    
//    Type of Template
//    1 - Terms & Condition Template
//    2 - Annexure I Template
//    3 - Sign Agreement Email to Applicant
//    4 - Sign Agreement SMS to Applicant
    @NotNull
    @Column(name="tm_type")
    private Integer type;
    
    @LastModifiedBy
    @Column(name = "tm_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "tm_moddt")
    private LocalDateTime modifiedDate;

    @NotNull
    @Column(name = "en_id")
    private Integer entityId;

    
    
    
    @Override
    public String toString() {
        return "EContract Template [id=" + id + ", moduleId=" + moduleId + ", docId=" + documentCategoryId + "]";
    }
}
