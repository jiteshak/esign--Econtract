/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.composite;

import java.io.Serializable;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SourceEntityLinkId implements Serializable{
    
    @NotNull
    @Column(name = "sr_id")
    private Integer sourceId;
    
    @NotNull
    @Column(name = "en_id")
    private Integer entityId;
    
    
}
