/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "role_hdr")
public class Role {
    
//    public int BusinessAdmin = 1;
//    public int OpsAdmin = 2;
//    public int BusinessSignatory = 3;
//    public int FrankingVendor = 4;
//    public int SystemAdmin = 5;
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "rl_id")
    private Integer id;
    
    
    @NotNull
    @Column(name="rl_name")
    private String name;
    
    @NotNull
    @Column(name = "rv_links")
    private String access = "";
    
    @NotNull
    @Column(name="rl_active", columnDefinition = "SMALLINT")
    private Integer isActive = 1;
    
    @LastModifiedBy
    @Column(name="rl_modby")
    private Integer modifiedBy;
    
    @LastModifiedDate
    @Column(name="rl_moddt")
    private LocalDateTime modifiedDate;
    
    @NotNull
    @Column(name = "en_id")
    private Integer entityId;
    
    
    @ManyToOne
    @JoinColumn(name = "en_id", insertable = false, updatable = false)
    private EEntity entity;
    
    //@ManyToMany(fetch = FetchType.LAZY)
    //@JoinTable(name = "rolemod_lnk" , joinColumns = @JoinColumn(name = "rl_id"), inverseJoinColumns = @JoinColumn(name = "md_id"))
    //private List<Module> modules;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "roleview_lnk" , joinColumns = @JoinColumn(name = "rl_id"), inverseJoinColumns = @JoinColumn(name = "vw_id"))
    private List<Permission> permissions;
    
    
    public String getEntityName(){
        if(this.entity == null){
            return "";
        }
        return this.entity.getName();
    }
    
    
    @Override
    public String toString() {
        return "EContract Entity Role [id=" + id + ", name=" + name
                + ", isActive=" + isActive + "]";
    }
    
}
