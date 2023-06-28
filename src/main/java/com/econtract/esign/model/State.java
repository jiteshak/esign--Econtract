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
 * 
 * 
 * This is to save agreement state and not for the country state
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name="state_hdr")
public class State {
    
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "st_id")
    private Integer id;
    
    @NotNull
    @Column(name = "st_name")
    private String name;
    
    @LastModifiedBy
    @Column(name = "st_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "st_moddt")
    private LocalDateTime modifiedDate;

    @NotNull
    @Column(name = "en_id")
    private Integer entityId;

    
    @Override
    public String toString() {
        return "EContract State [id=" + id + ", name=" + name +"]";
    }
}
