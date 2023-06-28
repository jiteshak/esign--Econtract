/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

import com.econtract.esign.model.constant.TaskLinkType;
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
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name="job_log")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jb_id")
    private Integer id;
    
    @Column(name = "jb_file", length = 500)
    private String file;
    
    @NotNull
    @Column(name = "link_id", length = 3)
    private Integer linkId;
    
    @NotNull
    @Column(name = "link_type", length = 3)
    private Integer linkType;
    
    @NotNull
    @Column(name = "jb_status", length = 3)
    private Integer status;
    
    @Column(name = "jb_message")
    private String message;
    
    
    @LastModifiedBy
    @Column(name = "jb_modby")
    private Integer modifiedBy;

    @LastModifiedDate
    @Column(name = "jb_moddt")
    private LocalDateTime modifiedDate;
    
    public String getLinkTypeString(){
        return TaskLinkType.toString(this.linkType);
    }
    
    @Override
    public String toString() {
        return "EContract Entity [id=" + id + ", task=" + linkId + 
                ", type=" + linkType + "]";
    }
}
