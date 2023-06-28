/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
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
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name="src_hdr")
public class Source {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sr_id")
    private Integer id;
    
    
    @NotNull
    @Column(name="sr_name")
    private String name;
    
    @Column(name="sr_code")
    private String code;
    
    @NotNull
    @Column(name="sr_active", columnDefinition = "SMALLINT")
    private Integer isActive = 1;
    
    
    @Column(name="sr_appid")
    private String appId;
    
    
    @Column(name="sr_skey")
    private String secretKey;
    
    
    @LastModifiedBy
    @Column(name = "sr_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "sr_moddt")
    private LocalDateTime modifiedDate;
    
    @NotNull
    @Column(name="sr_txncount")
    private Integer txnCount = 0;
    
    
    @Column(name="sr_path")
    private String path;
    
    @Column(name="sr_webhook")
    private String webhook;

    
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sr_id", insertable = false, updatable = false)
    private List<User> users;
    
    public User getUser(){
        User u = null;
        
        if(this.users.size() > 0){
            u =  this.users.get(0);
        }
       
       return u;
    }
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "src_entty_lnk", joinColumns = @JoinColumn(name = "sr_id"), inverseJoinColumns = @JoinColumn(name = "en_id"))
    private List<EEntity> entities;
    
    public List<Integer> getEntityIds(){
        List<Integer> sel = new ArrayList<>();
        this.entities.forEach(e -> {
            sel.add(e.getId());
        });
        return sel;
    }
    
    @Override
    public String toString() {
        return "EContract Source System [id=" + id + ", name=" + name + "]";
    }
}
