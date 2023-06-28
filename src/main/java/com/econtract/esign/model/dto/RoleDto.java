/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.Branch;
import com.econtract.esign.model.Role;
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
public class RoleDto {
    
    private Integer id;
    
    private Integer userId;
    
    private String name;
    
    private Integer entityId;
    
    private String entityName;
    
    
    private List<ModuleDto> modules = new ArrayList<>();
    
    private List<PermissionDto> permissions = new ArrayList<>();
    
    
    //form keys
    private List<Integer> moduleIds = new ArrayList<>();
    private List<Integer> permissionIds = new ArrayList<>();
    
    private String error;
    
    public static boolean isValid(RoleDto rd){
        
        if(rd.getName()== null){
            rd.setError("Name is required");
        }else if(rd.getName().isEmpty()){
            rd.setError("Name is required");
        }else if(!(rd.getEntityId() > 0 )){
            rd.setError("Entity is required");
        }else if(rd.getPermissionIds()== null){
            rd.setError("Please select atleast one permission");
        }else if(!(rd.getPermissionIds().size() > 0)){
            rd.setError("Please select atleast one permission");
        }
        
        
        return rd.getError() == null;
    }
    
    public static RoleDto toDto(Role r){
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RoleDto rd = mm.map(r, RoleDto.class);
        
        rd.setEntityName(r.getEntity().getName());
        
//        rd.setModules(ModuleDto.toDtoList(r.getModules()));
        
        return rd;
    }
    
    public static List<RoleDto> toDtoList(List<Role> rs){
        return rs.stream().map(r -> RoleDto.toDto(r)).collect(Collectors.toList());
    }
    
    public static RoleDto toRootDto(Role r){
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setDeepCopyEnabled(false);
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RoleDto rd = mm.map(r, RoleDto.class);
        
        rd.setEntityName(r.getEntity().getName());
        
        
        return rd;
    }
    
    public static List<RoleDto> toDtoRootList(List<Role> rs){
        return rs.stream().map(r -> RoleDto.toRootDto(r)).collect(Collectors.toList());
    }
    
    
    public static Role toModel(RoleDto ud){
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mm.getConfiguration().setDeepCopyEnabled(false);
        Role um = mm.map(ud, Role.class);
        
        
        return um;
    }
}
