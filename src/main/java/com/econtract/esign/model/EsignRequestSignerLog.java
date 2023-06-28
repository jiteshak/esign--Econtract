/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name="econ_sign_log")
public class EsignRequestSignerLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    @Column(name="es_id")
    private Integer signatoryId;
    
    @NotNull
    @Column(name = "ec_id")
    private Integer esignRequestId;
    
    
    @Column(name = "es_signdate")
    private LocalDateTime signDate;
    
    @NotNull
    @Column(name="es_name")
    private String applicantName;
    
    @NotNull
    @Column(name="es_contact")
    private String applicantContact;
    
    
    @Column(name="es_email")
    private String applicantEmail;
    
    
    @Column(name="es_uid")
    private Integer userId;
    
    @NotNull
    @Column(name="es_type", columnDefinition = "SMALLINT")
    private Integer type;
    
    @NotNull
    @Column(name="es_signtype", columnDefinition = "SMALLINT")
    private Integer signType;
    
    @NotNull
    @Column(name="es_status", columnDefinition = "SMALLINT")
    private Integer status;
    
    @Column(name="es_file")
    private String file;
    
    @Column(name="es_txnid")
    private String transactionId;
    
    @Column(name="es_signer")
    private String signer;
    
    @Column(name="es_ip")
    private String ip;
    
    @JsonIgnore
    @Column(name="es_response", columnDefinition = "TEXT")
    private String responseThirdParty;
    
    
    
    
    @Override
    public String toString() {
        return "EContract  Esign Request Signer Log []";
    }
}
