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
@Table(name="doc_cat")
public class DocumentCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="doc_id")
    private Integer id;
    
    @NotNull
    @Column(name="doc_name")
    private String name;
    
    @NotNull
    @Column(name="doc_code", columnDefinition = "SMALLINT")
    private Integer code;
    
    @LastModifiedBy
    @Column(name = "doc_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "doc_moddt")
    private LocalDateTime modifiedDate;

    @NotNull
    @Column(name = "en_id")
    private Integer entityId;
    
    
    @ManyToOne
    @JoinColumn(name = "en_id", insertable = false, updatable = false)
    private EEntity entity;

    
    
    
    @Override
    public String toString() {
        return "EContract Document Category [id=" + id + ", name=" + name+ "]";
    }
    
}
