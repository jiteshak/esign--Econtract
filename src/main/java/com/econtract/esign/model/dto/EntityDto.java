/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.model.dto;

import com.econtract.esign.model.EEntity;
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
public class EntityDto {
    
    private Integer id;
    
    private String name;

    private String aliasName;
    
    public EntityDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public static EntityDto toDto(EEntity e){
        ModelMapper mm = new ModelMapper();
        return mm.map(e, EntityDto.class);
    }
    
    public static List<EntityDto> toDtoList(List<EEntity> es){
        return es.stream().map(e -> EntityDto.toDto(e)).collect(Collectors.toList());
    }
}
