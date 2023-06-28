/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

import java.time.LocalDateTime;
import java.util.Date;
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
@Table(name = "stmp_info")
public class StampPaper {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "sp_id")
    private Integer id;
    
//    @NotNull
    @Column(name="sr_id")
    private Integer sourceId;
    
    @NotNull
    @Column(name="sp_refno")
    private String referenceNo;
    
    @NotNull
    @Column(name="sp_value")
    private Integer value;
    
    @NotNull
    @Column(name="sp_state")
    private String state;
    
    @NotNull
    @Column(name="sp_date")
    private Date procurementDate;
    
    @NotNull
    @Column(name="sp_expdt")
    private Date expiryDate;
    
//      0 - Available
//      1 - Allocated
    @NotNull
    @Column(name="sp_status", columnDefinition = "SMALLINT")
    private Integer status;
    
    @NotNull
    @Column(name="sp_active", columnDefinition = "SMALLINT")
    private Integer isActive;
    
    @Column(name="sp_file1")
    private String file1;
    
    @Column(name="sp_file2")
    private String file2;

    @LastModifiedBy
    @Column(name = "sp_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "sp_moddt")
    private LocalDateTime modifiedDate;

    @NotNull
    @Column(name = "en_id")
    private Integer entityId;
    
    
    
    
    
    @Override
    public String toString() {
        return "EContract StampPaper [id=" + id + ",state=" + state + ",referenceNo=" + referenceNo + ",value=" + value + "]";
    }
}
