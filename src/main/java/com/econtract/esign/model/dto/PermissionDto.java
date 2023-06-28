/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.Permission;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
public class PermissionDto {
    
    private Integer Id;
    
    private String name;
    
    private String code;
    
    
    
    public static PermissionDto toDto(Permission p){
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PermissionDto rd = mm.map(p, PermissionDto.class);
        
        return rd;
    }
    
    public static List<PermissionDto> toDtoList(List<Permission> ps){
        return ps.stream().map(p -> PermissionDto.toDto(p)).collect(Collectors.toList());
    }
}
