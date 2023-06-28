/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

/**
 *
 * @author TS
 */
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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name="entty_hdr")
public class EEntity {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="en_id")
    private Integer id;

    @Column(name="en_pid")
    private Integer parentId;
    
    @NotNull
    @Column(name="en_name")
    private String name;
    
    
    @NotNull
    @Column(name="en_active", columnDefinition = "SMALLINT")
    private Integer isActive = 1;
    
    @Column(name="en_alias")
    private String aliasName;
    
    @LastModifiedBy
    @Column(name="en_modby")
    private Integer modifiedBy;
    
    @LastModifiedDate
    @Column(name="en_moddt")
    private LocalDateTime modifiedDate;
    
    @Column(name="en_txncount")
    private Integer txnCount;
    
    
    @Column(name="en_otpsign")
    private Integer otpSignCount = 0;
    
    
    @Column(name="en_aadharsign")
    private Integer adhaarSignCount = 0;
    
    
    @Column(name="en_dscsign")
    private Integer dscSignCount = 0;
    
    @Column(name="en_usrcnt")
    private Integer usersCount = 0;
    
    @Column(name="en_actusrcnt")
    private Integer activeUsersCount = 0;
    
    
    @Override
    public String toString() {
        return "EContract Entity [id=" + id + ", name=" + name + 
                ", isActive=" + isActive + "]";
    }
}
