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
@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name="stamp_econ_link")
public class EsignRequestStampPaperLink {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    @Column(name = "ec_id")
    private Integer esignRequestId;
    
    @NotNull
    @Column(name = "sp_id")
    private Integer stampPaperId;
    
    
    @NotNull
    @Column(name="se_refno")
    private String stampPaperReferenceNo;
    
    @NotNull
    @Column(name="se_value")
    private Integer stampPaperValue;
    
//      1 - Linked
//      0 - De-Linked
    @NotNull
    @Column(name="se_status", columnDefinition = "SMALLINT")
    private Integer status;
    
    @LastModifiedBy
    @Column(name = "se_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "se_moddt")
    private LocalDateTime modifiedDate;    
    
    
    @Override
    public String toString() {
        return "EContract Esign Request StampPaper [id=" + id + ", stampPaperValue=" + stampPaperValue + "]";
    }
}
