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
 * @author zaidk
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name="userlink_hdr")
public class UserLink {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ul_id")
    private Integer id;
    
    @NotNull
    @Column(name="us_id")
    private Integer userId;
    
    @NotNull
    @Column(name="link_id")
    private Integer linkId;
    
    
   
    @NotNull
    @Column(name="link_type", columnDefinition = "SMALLINT",length=1)
    private Integer linkType;
    
    
    @LastModifiedBy
    @Column(name = "up_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "up_moddt")
    private LocalDateTime modifiedDate;
   
    
}
