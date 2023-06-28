/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

import com.econtract.esign.model.dto.ModuleDto;
import com.econtract.esign.model.dto.AclRoleDto;
import com.econtract.esign.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.WhereJoinTable;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 *
 * @author TS
 * 
 * INSERT INTO public.usr_hdr(
	us_id, us_chpwd, us_contact, us_contact1, us_contact2, us_dob, us_email, us_email1, us_email2, us_fname, us_gender, us_active, us_lname, us_mname, us_pwd, us_uname)
	VALUES (1, 0, 9702097681, null, null, '1993-09-12', '786.di.s@gmail.com', ' ', ' ', 'suhail', 'M', 1, 'tamboli', 'saeed', '84d636daa7592ea1a774f5022ad04ebc', '786.di.s@gmail.com');
 */

@Entity
@Audited
@Table(name = "usr_hdr")
@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "aus_id")
    private Integer id;
    
    @Column(name = "sr_id")
    private Integer sourceId;
    
    @Column(name = "stmp_sr_id")
    private Integer stampPaperSourceId;
    
    @Column(name = "us_id")
    private Integer InternalId;
    
    @Column(name = "us_extid")
    private String externalId;

    @NotNull
    @Column(name = "us_uname")
    private String userName;
    
    @NotNull
    @Column(name = "us_fname")
    private String firstName;
    
    @Column(name = "us_mname")
    private String middleName;
    
    @Column(name = "us_lname")
    private String lastName;
    
    @NotNull
    @Column(name = "us_email")
    private String email;
    
    @JsonIgnore
    @NotNull
    @Column(name = "us_pwd")
    private String password;
    
    @NotNull
    @Column(name = "us_chpwd", columnDefinition = "SMALLINT")
    private Integer changePassword;

    @NotNull
    @Column(name = "us_active", columnDefinition = "SMALLINT")
    private Integer isActive;

    @NotNull
    @Column(name = "us_contact", columnDefinition = "BIGINT")
    private Long contact;
    
    @Column(name = "us_contact1", columnDefinition = "BIGINT")
    private Long contact1;
    
    @Column(name = "us_contact2", columnDefinition = "BIGINT")
    private Long contact2;
    
    @Column(name = "us_email1")
    private String email1;
    
    @Column(name = "us_email2")
    private String email2;

    @LastModifiedBy
    @Column(name = "us_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "us_moddt")
    private LocalDateTime modifiedDate;

    @Column(name = "us_gender")
    private String gender;//M,F,T
    
//    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "us_dob")
    private Date dob;
    
    @Column(name = "us_group")
    private String group;
    
    @Column(name = "us_lastlogin")
    private LocalDateTime lastLogin;
    
    @Column(name = "us_logintry")
    private LocalDateTime loginTry;
    
    @Column(name = "us_login_attempt")
    private Integer loginAttempt = 0;
    
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usr_roleink", joinColumns = @JoinColumn(name = "us_id"), inverseJoinColumns = @JoinColumn(name = "rl_id"))
    private List<Role> roles;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sr_id", insertable = false, updatable = false)
    private Source source;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stmp_sr_id", insertable = false, updatable = false)
    private Source stampPaperSource;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "userlink_hdr", joinColumns = @JoinColumn(name = "us_id"), inverseJoinColumns = @JoinColumn(name = "link_id"))
    @WhereJoinTable(clause =  " link_type = 1 ")
    private List<Branch> branches;
    
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "userlink_hdr", joinColumns = @JoinColumn(name = "us_id"), inverseJoinColumns = @JoinColumn(name = "link_id"))
    @WhereJoinTable(clause =  " link_type = 2 ")
    private List<Module> modules;
    
    
    public Integer getSourceId(){
        if(this.source == null){
            return 0;
        }
        return this.source.getId();
    }
    
    
    public String getRoleIds(){
        String ids = "";
        if(this.roles == null){
            return "";
        }
        
        for(int i = 0; i < this.roles.size(); i++){
            if(!ids.isEmpty()){
                ids += ",";
            }
            ids += this.roles.get(i).getId().toString();
        }
        return ids;
    }
    
    public String getName(){
        String n = "";
        
        if(this.firstName != null){
            n += this.firstName;
        }
        
        if(this.lastName != null){
            n += " " + this.lastName;
        }
        
        if(this.firstName != null){
            n += " " + this.firstName;
        }
        
        return n;
    }
    
    @Override
    public String toString() {
        return "EContract Entity User [id=" + id + ", name=" + userName
                + ", isActive=" + isActive + "]";
    }

}
