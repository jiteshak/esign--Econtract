/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model;

import com.econtract.esign.model.composite.SourceEntityLinkId;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
@IdClass(SourceEntityLinkId.class)
@Table(name = "src_entty_lnk")
public class SourceEntityLink {
    
    @Id
    @NotNull
    @Column(name = "sr_id")
    private Integer sourceId;
    
    @Id
    @NotNull
    @Column(name = "en_id")
    private Integer entityId;
    
    
    @LastModifiedBy
    @Column(name="sel_modby")
    private Integer modifiedBy;
    
    @LastModifiedDate
    @Column(name="sel_moddt")
    private LocalDateTime modifiedDate;
}
