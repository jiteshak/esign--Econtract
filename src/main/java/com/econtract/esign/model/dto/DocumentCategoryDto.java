/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import java.time.LocalDateTime;
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
public class DocumentCategoryDto {
    
    private Integer id;
    private String name;
    private Integer code;
    private Integer modifiedBy;
    private LocalDateTime modifiedDate;
    
    private EntityDto entity;
}
