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
@Table(name="otp_log")
public class OtpLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ol_id")
    private Integer id;
    
    @NotNull
    @Column(name="link_id")
    private Integer linkId;
    
    @NotNull
    @Column(name="link_type")
    private Integer linkType;
    
    @NotNull
    @Column(name="ol_contact")
    private String contact;
    
    
    @Column(name="ol_email")
    private String email;
    
    @NotNull
    @Column(name="ol_otp")
    private String otp;
 
    
    @Column(name="ol_str1")
    private String description;
    
    @Column(name = "ol_enddt")
    private LocalDateTime endDate;
    
    
    @Column(name = "ol_atmpt")
    private Integer attempt = 0;
    
    @NotNull
    @Column(name="se_closed", columnDefinition = "SMALLINT")
    private Integer closed = 0;
    
    @LastModifiedBy
    @Column(name = "ol_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "ol_moddt")
    private LocalDateTime modifiedDate;

    
}
