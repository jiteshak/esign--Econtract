/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.Role;
import com.econtract.esign.model.Source;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
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
public class AclRoleDto {
    
    private Integer id;
    
    private Integer userId;
    
    private String name;
    
    private Integer entityId;
    
    private String entityName;
    
    private Integer sourceId;
    
    @JsonIgnore
    private Source source;
    
    private List<EntityDto> sourceEntities = new ArrayList<>();
    
    private List<ModuleDto> modules = new ArrayList<>();  
    
    private List<BranchDto> branches = new ArrayList<>();
    
    private List<PermissionDto> permissions = new ArrayList<>();
    
    public Integer getSourceId(){
        if(sourceId == null){
            return 0;
        }
        return sourceId;
    }
    
    public List<Integer> getSourceEntityIds(){
        List<Integer> el = new ArrayList<>();
        
        this.sourceEntities.forEach(e -> {
            el.add(e.getId());
        });
        
        return el;
    }
    
    
    
    public List<Integer> getBranchIds(){
        List<Integer> el = new ArrayList<>();
        
        this.branches.forEach(e -> {
            el.add(e.getId());
        });
        
        return el;
    }
    
    
    public static AclRoleDto toDto(Role r){
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        AclRoleDto rd = mm.map(r, AclRoleDto.class);
        
        rd.setEntityName(r.getEntity().getName());
        
        
        return rd;
    }
    
    public static List<AclRoleDto> toDtoList(List<Role> rs){
        return rs.stream().map(r -> AclRoleDto.toDto(r)).collect(Collectors.toList());
    }
}
