/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.composite;

import java.io.Serializable;
import java.util.Objects;
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
public class UserRoleLinkId implements Serializable {
 
    @NotNull
    @Column(name = "us_id")
    private Integer userId;
    
    @NotNull
    @Column(name = "rl_id")
    private Integer roleId;

}
