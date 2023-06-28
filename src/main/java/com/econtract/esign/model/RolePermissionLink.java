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
@Table(name = "roleview_lnk")
public class RolePermissionLink {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "rv_id")
    private Integer id;
    
    @NotNull
    @Column(name = "rl_id")
    private Integer roleId;
    
    @NotNull
    @Column(name = "vw_id")
    private Integer permissionId;
    
    @LastModifiedBy
    @Column(name="vw_modby")
    private Integer modifiedBy;
    
    @LastModifiedDate
    @Column(name="vw_moddt")
    private LocalDateTime modifiedDate;
}
