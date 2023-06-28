/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.Branch;
import com.econtract.esign.model.Role;
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
public class BranchDto {
    
    
    public BranchDto(Integer id, String name, Integer entityId, String entityName) {
        this.id = id;
        this.name = name;
        this.entityId = entityId;
        this.entityName = entityName;
    }
    
    private Integer id;
    
    private String name;
    
    private Integer entityId;
    
    private String entityName;
    
    
    
    
    public static BranchDto toDto(Branch b){
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        BranchDto bd = mm.map(b, BranchDto.class);
        
        bd.setEntityName(b.getEntity().getName());
        
        return bd;
    }
    
    public static List<BranchDto> toDtoList(List<Branch> bs){
        return bs.stream().map(b -> BranchDto.toDto(b)).collect(Collectors.toList());
    }
}
