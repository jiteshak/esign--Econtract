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

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="email_log")
public class EmailLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="em_id")
    private Integer id;
    
    @NotNull
    @Column(name = "ec_id")
    private Integer esignRequestId;
    
    @NotNull
    @Column(name = "em_from")
    private String from;
    
    @NotNull
    @Column(name = "em_to")
    private String to;
    
    
    @Column(name = "em_cc")
    private String cc;
    
    @Column(name = "em_bcc")
    private String bcc;
    
    @Column(name = "em_subject")
    private String subject;
    
    @NotNull
    @Column(name="em_status", columnDefinition = "SMALLINT")
    private Integer status;
    
    
    @Column(name = "em_msgdet")
    private String body;
    
    @Column(name = "em_att")
    private String attachment;
    
    @Column(name = "em_att2")
    private String attachment2;
    
    @Column(name = "em_att3")
    private String attachment3;
    
    @Column(name = "em_att4")
    private String attachment4;
    
    
    @NotNull
    @Column(name="em_type", columnDefinition = "SMALLINT")
    private Integer type;
    
    @Column(name = "em_dte")
    private LocalDateTime date;

    
    @ManyToOne
    @JoinColumn(name = "ec_id", insertable = false, updatable = false)
    private EsignRequest esignRequest;

    public void setEsignRequest(EsignRequest esignRequest) {
        this.esignRequest = esignRequest;
    }
    
    
    
    
    
    
    
    @Override
    public String toString() {
        return "EContract Email Log []";
    }
    
}
