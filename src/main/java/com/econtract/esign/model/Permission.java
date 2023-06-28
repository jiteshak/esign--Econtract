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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name = "view_hdr")
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vw_id")
    private Integer id;
    
    @NotNull
    @Column(name = "vw_name")
    private String name;
    
    @NotNull
    @Column(name = "vw_url")
    private String code;
    
    @Column(name = "vw_active", length = 2)
    private Integer isActive = 1;
    
    @LastModifiedBy
    @Column(name="vw_modby")
    private Integer modifiedBy;
    
    @LastModifiedDate
    @Column(name="vw_moddt")
    private LocalDateTime modifiedDate;
    
    
}
