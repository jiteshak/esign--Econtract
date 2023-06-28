/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.Module;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

/**
 *
 * @author TS
 */
@Getter
@Setter
@NoArgsConstructor
public class ModuleDto {
    
    
    public ModuleDto(Integer id, String name, Integer entityId, String entityName) {
        this.id = id;
        this.name = name;
        this.entityId = entityId;
        this.entityName = entityName;
    }
    
    private Integer id;
    
    private String name;
    
    private Integer entityId;
    
    private String entityName;
    
    
    
    
    public static ModuleDto toDto(Module m){
        ModelMapper mm = new ModelMapper();
        ModuleDto md = mm.map(m, ModuleDto.class);
        
        md.setEntityName(m.getEntity().getName());
        
        return md;
    }
    
    public static List<ModuleDto> toDtoList(List<Module> ms){
        return ms.stream().map(m -> ModuleDto.toDto(m)).collect(Collectors.toList());
    }
}
