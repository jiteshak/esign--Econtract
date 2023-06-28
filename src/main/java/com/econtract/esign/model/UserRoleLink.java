/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;


import com.econtract.esign.model.composite.UserRoleLinkId;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
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
@IdClass(UserRoleLinkId.class)
@Table(name = "usr_roleink")
public class UserRoleLink {
    
    @Id
    @NotNull
    @Column(name = "us_id")
    private Integer userId;
    
    @Id
    @NotNull
    @Column(name = "rl_id")
    private Integer roleId;
    
    @LastModifiedBy
    @Column(name="ur_modby")
    private Integer modifiedBy;
    
    @LastModifiedDate
    @Column(name="ur_moddt")
    private LocalDateTime modifiedDate;

    
    @ManyToOne
    @JoinColumn(name = "us_id", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "rl_id" , insertable = false, updatable = false)
    private Role role;
    
    
    @Override
    public String toString() {
        return "EContract User Role [rl_id=" + role + ", us_id=" + user
                + "]";
    }
}