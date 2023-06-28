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
import org.hibernate.envers.NotAudited;
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
@Table(name = "modl_hdr")
public class Module {
        

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "md_id")
    private Integer id;

    @Column(name = "md_pid")
    private Integer parentId;

    @NotNull
    @Column(name = "md_name")
    private String name;

    @NotNull
    @Column(name = "md_active", columnDefinition = "SMALLINT")
    private Integer isActive = 1;

    @NotNull
    @Column(name = "md_type", columnDefinition = "SMALLINT")
    private Integer type;
    
    @Column(name = "md_state")
    private String state;

    @Column(name = "st_id", columnDefinition = "SMALLINT")
    private Integer initialState;

    @LastModifiedBy
    @Column(name = "md_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "md_moddt")
    private LocalDateTime modifiedDate;

    @NotNull
    @Column(name = "en_id")
    private Integer entityId;
    
    
    @ManyToOne
    @JoinColumn(name = "en_id", insertable = false, updatable = false)
    private EEntity entity;
    
    
    @Override
    public String toString() {
        return "EContract Entity Module [id=" + id + ", name=" + name
                + ", isActive=" + isActive + "]";
    }

}
