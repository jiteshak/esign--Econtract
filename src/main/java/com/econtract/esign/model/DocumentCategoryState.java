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
@Table(name="dcat_hdr")
public class DocumentCategoryState {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="dc_id")
    private Integer id;
    
    
    @Column(name="doc_id")
    private Integer documentCategoryId;
    
    @Column(name="md_id")
    private Integer moduleId;
    
    @NotNull
    @Column(name="doc_name")
    private String name;
    
    @NotNull
    @Column(name="doc_code", columnDefinition = "SMALLINT")
    private Integer code;
    
    
    @NotNull
    @Column(name="dc_state")
    private String state;
    
    @NotNull
    @Column(name="dc_stampval")
    private Integer stampValue;
    
    
    @Column(name="dc_mincamt")
    private Integer minAmount;
    
    @Column(name="dc_maxcamt")
    private Integer maxAmount;
    
    
    @LastModifiedBy
    @Column(name = "dc_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "dc_moddt")
    private LocalDateTime modifiedDate;

    @NotNull
    @Column(name = "en_id")
    private Integer entityId;
    
    
    
    @ManyToOne
    @JoinColumn(name = "en_id", insertable = false, updatable = false)
    private EEntity entity;
    
    @ManyToOne
    @JoinColumn(name = "doc_id", insertable = false, updatable = false)
    private DocumentCategory documentCategory;
    
    
    
    @Override
    public String toString() {
        return "EContract Document Category [id=" + id + ", name=" + name+ "]";
    }
}
